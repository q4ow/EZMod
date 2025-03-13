# E-Z Mod

A Minecraft Forge mod for seamless screenshot capture and instant sharing via e-z.host.

## Features

- **One-Key Screenshot**: Capture screenshots with a single keypress (default: F12)
- **Instant Uploads**: Automatically uploads screenshots to e-z.host
- **Clean UI**: Displays image links with convenient action buttons
- **Clipboard Integration**: Copy image URLs directly to your clipboard
- **Browser Integration**: Open deletion links in your default browser

## Installation

1. Install Minecraft Forge for version 1.8.9
2. Download the latest E-Z Mod release from [Releases](https://github.com/q4ow/EZMod/releases)
3. Place the `.jar` file in your Minecraft `mods` folder
4. Launch Minecraft with the Forge profile

## Configuration

1. Launch Minecraft with E-Z Mod installed
2. Navigate to Options → Mod Options → E-Z Mod
3. Enter your e-z.host API key in the configuration screen
4. Save the configuration

You can obtain an API key by registering at [e-z.host](https://e-z.host).

## Usage

1. In-game, press F12 (configurable in controls) to take a screenshot
2. The mod will automatically upload the screenshot to e-z.host
3. Once uploaded, a message will appear in chat with:
   - The direct image URL
   - Action buttons:
     - **Copy URL**: Copies the image URL to your clipboard
     - **Copy Raw**: Copies the raw image URL to your clipboard
     - **Delete**: Copies the deletion URL to your clipboard

## Keybindings

You can customize the screenshot key in Minecraft's Controls menu under the E-Z Mod category.

## Troubleshooting

### No API Key Provided
If you see "No API Key provided. Screenshot aborted." in chat, you need to set your API key in the mod configuration.

### Upload Failures
If uploads fail, check:
1. Your API key is correct
2. Your internet connection is stable
3. e-z.host services are online

## For Developers

### Building from Source

1. Clone the repository
2. Set up your development environment with Gradle
3. Run `./gradlew build` to compile the mod
4. Find the compiled JAR in `build/libs/`

### Dependencies

- Minecraft Forge 1.8.9
- Java 8 or higher

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Credits

- Developed by Keiran
- Powered by [e-z.host](https://e-z.host) API

## Support

For issues, feature requests, or questions, please open an issue on the [GitHub repository](https://github.com/q4ow/EZMod/issues).
