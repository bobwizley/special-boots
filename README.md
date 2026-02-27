# Special Boots

A Fabric mod for Minecraft 1.21.11 that adds boot enchantments: **Heavyfoot** and **Lightfoot**.

## What it does

Both enchantments are single-level and can only be applied to boots.

### Heavyfoot

While wearing enchanted boots, the player automatically:

- **Destroys flowers and bushes** (short grass, tall grass, ferns, dead bushes, sweet berry bushes, and all flowers) in a small area around their feet.
- **Converts grass blocks into dirt paths** beneath their steps.

### Lightfoot

Prevents farmland from being trampled when the player jumps on it, protecting crops from being destroyed.

## Requirements

- Minecraft 1.21.11
- Fabric Loader 0.18.4+
- Fabric API

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/installer/) for Minecraft 1.21.11.
2. Download the [Fabric API](https://modrinth.com/mod/fabric-api) and place it in your `mods/` folder.
3. Download the latest `specialboots-x.x.x.jar` from the releases and place it in your `mods/` folder.
4. Launch the game.

You can apply the enchantments to boots using an enchanting table, anvil with an enchanted book, or the commands:

```
/enchant @s specialboots:heavyfoot
/enchant @s specialboots:lightfoot
```

## Building from source

You need Java 21 installed.

```bash
git clone <repo-url>
cd special-boots
./gradlew build
```

The compiled jar will be in `build/libs/specialboots-<version>.jar`.

## Contributing

1. Fork the repository and create a branch for your feature or fix.
2. Make sure the project builds cleanly with `./gradlew build`.
3. Keep changes focused -- one feature or fix per pull request.
4. Open a pull request with a clear description of what you changed and why.

## License

This project is licensed under the MIT License.
