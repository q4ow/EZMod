# E-Z Mod

A powerful Minecraft Forge mod for seamless screenshot capture and instant sharing via e-z.host.

## Features

- **One-Click Screenshot**: Capture and upload screenshots instantly (F12)
- **Secure API Integration**: Direct integration with e-z.host API
- **Command Aliases**: Quick access to Hypixel Skyblock dungeons
- **Modern UI**: Clean, intuitive interface with clickable actions
- **Clipboard Support**: One-click URL copying
- **Deletion Management**: Easy access to deletion URLs

## Installation

1. Ensure you have Minecraft Forge 1.8.9 installed
2. Download the latest release from [GitHub Releases](https://github.com/q4ow/EZMod/releases)
3. Place the `.jar` file in your `.minecraft/mods` folder
4. Launch Minecraft with the Forge profile

## Configuration

1. Get your API key from [e-z.host](https://e-z.host)
2. In Minecraft:
   - Options → Mod Options → E-Z Mod
   - Enter your API key
   - Click "Save Changes"

## Command Aliases

Quick access commands for Hypixel Skyblock:

- `!d` - Warp to Dungeon Hub
- `!c` - Open Collections
- `!f1` to `!f7` - Join specific dungeon floors
- `!m1` to `!m7` - Join master mode floors

## For Developers

### Building

```bash
./gradlew build
```

### Requirements
- Java 8+
- Minecraft Forge 1.8.9
- Gradle 4.10.3

## License
MIT License - See [LICENSE](LICENSE) file for details
