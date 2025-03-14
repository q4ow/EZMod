# E-Z Mod

<div align="center">
  <i>"Screenshot sharing made simple"</i>
</div>

<p align="center" style="margin-top: 25px;">
  <img src="https://assets.e-z.gg/e-ztransparent.png" alt="E-Z Logo" width="400">
</p>

## Overview

E-Z Mod is a lightweight Minecraft Forge utility mod designed to enhance your gameplay experience with seamless screenshot sharing capabilities and convenient command shortcuts. With a single keypress, capture and upload screenshots directly to [e-z.host](https://e-z.host), a reliable image hosting service.

## Features

### 🖼️ One-Click Screenshot Sharing

- **Instant Capture**: Press F12 (configurable) to take a high-quality screenshot
- **Automatic Uploads**: Screenshots are immediately uploaded to e-z.host
- **Elegant Results**: Receive a beautifully formatted chat message with your image links

### 🔗 Link Management

- **Multiple Link Formats**: Get both direct image and raw file links
- **Easy Copying**: Click buttons in chat to copy links to clipboard
- **Deletion Control**: Each upload includes a deletion link for when you need it

### ⌨️ Command Aliases

- **Quick Commands**: Use shorthand aliases to execute common commands
- **Dungeon Shortcuts**: Quickly navigate to different Hypixel SkyBlock dungeons
- **Time-Saving**: Reduce typing with intuitive aliases like `!d` for dungeon hub

### ⚙️ Customization

- **API Key Configuration**: Securely store your e-z.host API key
- **Keybinding Options**: Change the screenshot key to suit your preferences
- **User-Friendly Interface**: Simple configuration menu for all settings

## Installation

1. Install [Minecraft Forge](https://files.minecraftforge.net/) for your Minecraft version
2. Download the latest E-Z Mod release from [GitHub Releases](https://github.com/q4ow/ezmod/releases)
3. Place the downloaded `.jar` file in your Minecraft `mods` folder
4. Launch Minecraft with the Forge profile

## Setup

1. Obtain an API key from [e-z.host](https://e-z.host)
2. In Minecraft, open the mod configuration menu (Main Menu → Mods → E-Z Mod → Config)
3. Enter your API key in the provided field
4. Save your configuration

## Usage

### Screenshot Sharing

1. Join any Minecraft world or server
2. Press F12 (default) to capture and upload a screenshot
3. Wait for the upload to complete (typically under a second)
4. Receive a formatted message in chat with your image links
5. Click the buttons to:
   - Open the image in your browser
   - Copy the image URL
   - Copy the raw file URL
   - Copy the deletion URL

### Command Aliases

Type any of these aliases in chat to quickly execute common commands:

| Alias | Command | Description |
|-------|---------|-------------|
| `!d` | `/warp dungeon_hub` | Warp to the Dungeon Hub |
| `!c` | `/collections` | Open your collections menu |
| `!f1 - !f7` | `/joininstance CATACOMBS_FLOOR_...` | Join Catacombs Floors |
| `!m1` - `!m7` | `/joininstance MASTER_CATACOMBS_FLOOR_...` | Join Master Mode floors |

__More aliases coming soon! Suggestions are welcome, just open an issue.__

### Commands

- `/ezmod copyurl <url>` - Copies the specified URL to your clipboard

## Requirements

- Minecraft 1.8.9
- Forge 11.15.1.2318 or higher
- Java 8 or higher
- An internet connection for uploading screenshots
- A valid e-z.host API key

## Compatibility

E-Z Mod is designed to be compatible with most popular Forge mods. If you encounter any compatibility issues, please report them on our GitHub issues page.

## Support

If you encounter any issues or have suggestions for improvements:

- Open an issue on our [GitHub repository](https://github.com/q4ow/ezmod/issues)
- Join the E-Z [Discord server](https://discord.gg/ez) for community support related to the service (NOT THE MOD!!)

## Known Issues

- Command aliases may not work consistently on all servers due to variations in chat handling
- We're actively working to improve alias reliability across different server environments

## License

E-Z Mod is released under the MIT License. See the LICENSE file for details.

## Changelog

### v1.1.0
- Added command alias system for quick access to common commands
- Improved screenshot upload reliability
- Enhanced error handling and logging

### v1.0.1
- Initial public release
- Screenshot capture and upload functionality
- Basic configuration options
