package br.com.bobwizley.rootboot.feature.biomediscovery;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;

/**
 * Builds a biome's conventional translation key with a readable identifier-derived fallback.
 * Keeping the namespace in modded fallbacks avoids ambiguous names without maintaining a closed
 * biome list.
 */
public final class BiomeDisplayName {

    private BiomeDisplayName() {
    }

    public static MutableComponent component(Identifier biomeId) {
        return Component.translatableWithFallback(translationKey(biomeId), fallback(biomeId));
    }

    public static String translationKey(Identifier biomeId) {
        return biomeId.toLanguageKey("biome");
    }

    public static String fallback(Identifier biomeId) {
        String path = titleCase(biomeId.getPath());
        if (Identifier.DEFAULT_NAMESPACE.equals(biomeId.getNamespace())) {
            return path;
        }
        return titleCase(biomeId.getNamespace()) + ": " + path;
    }

    private static String titleCase(String value) {
        return Arrays.stream(value.split("[._/\\-]+"))
                .filter(part -> !part.isEmpty())
                .map(part -> part.substring(0, 1).toUpperCase(Locale.ROOT) + part.substring(1))
                .collect(Collectors.joining(" "));
    }
}
