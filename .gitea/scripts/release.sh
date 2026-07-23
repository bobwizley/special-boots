#!/usr/bin/env bash

set -euo pipefail

tag="${GITEA_REF_NAME:?GITEA_REF_NAME is required}"
api_url="${GITEA_API_URL:?GITEA_API_URL is required}"
repository="${GITEA_REPOSITORY:?GITEA_REPOSITORY is required}"
token="${GITEA_TOKEN:?GITEA_TOKEN is required}"

if [[ ! "$tag" =~ ^v([0-9]+)\.([0-9]+)\.([0-9]+)$ ]]; then
    echo "Release tag must match vMAJOR.MINOR.PATCH: $tag" >&2
    exit 1
fi

version="${tag#v}"
tag_commit="$(git rev-parse "${tag}^{commit}")"

if ! git merge-base --is-ancestor "$tag_commit" refs/remotes/origin/master; then
    echo "Release tag $tag does not point to a commit in master history" >&2
    exit 1
fi

auth_header="Authorization: token $token"
release_url="$api_url/repos/$repository/releases/tags/$tag"

release_json="$(
    curl \
        --fail-with-body \
        --silent \
        --show-error \
        --header "$auth_header" \
        "$release_url"
)" || {
    echo "Release $tag must exist before the workflow runs" >&2
    exit 1
}

if [[ "$(jq -r '.tag_name' <<<"$release_json")" != "$tag" ]]; then
    echo "Release tag returned by Gitea does not match $tag" >&2
    exit 1
fi

if [[ "$(jq -r '.draft' <<<"$release_json")" != "false" ]]; then
    echo "Release $tag must not be a draft" >&2
    exit 1
fi

if [[ "$(jq -r '.prerelease' <<<"$release_json")" != "false" ]]; then
    echo "Release $tag must not be a prerelease" >&2
    exit 1
fi

release_id="$(jq -er '.id' <<<"$release_json")"
asset_name="rootboot-$version.jar"
assets_url="$api_url/repos/$repository/releases/$release_id/assets"

assets_json="$(
    curl \
        --fail-with-body \
        --silent \
        --show-error \
        --header "$auth_header" \
        "$assets_url"
)"

./gradlew build --no-daemon "-Pmod_version=$version"

if jq -e --arg name "$asset_name" '.[] | select(.name == $name)' <<<"$assets_json" >/dev/null; then
    echo "Release asset $asset_name already exists; nothing to publish"
    exit 0
fi

asset_path="build/libs/$asset_name"
if [[ ! -f "$asset_path" ]]; then
    echo "Expected release asset was not built: $asset_path" >&2
    exit 1
fi

curl \
    --fail-with-body \
    --silent \
    --show-error \
    --request POST \
    --header "$auth_header" \
    --form "attachment=@$asset_path;type=application/java-archive" \
    "$assets_url?name=$asset_name" \
    >/dev/null

echo "Published $asset_name to release $tag"
