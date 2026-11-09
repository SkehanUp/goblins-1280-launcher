# Contributing to GOBLINS 1280 Launcher

Thanks for your interest in contributing to **GOBLINS 1280 Launcher**!

## Ways to Contribute

- **Device Cutout Presets**: Add camera cutout alignments for other phones alongside the Pixel 10a preset.
- **Retro LCD Themes & Fonts**: Propose new monochrome LCD palettes or pixel bitmap typography.
- **Classic Mini-Games**: Add classic monochrome keypad games or tools (e.g., Calculator, Stopwatch, Composer).
- **Bug Fixes & Accessibility**: Improve keypad ergonomics, haptics, or Android version compatibility.

## Development Workflow

1. Fork the repository and create a feature branch from `main`:
   ```bash
   git checkout -b feat/your-feature-name
   ```
2. Make your changes in small, self-contained commits.
3. Keep pure logic in `com.goblins.launcher1280.analysis` tested with JUnit tests under `app/src/test/`.
4. Run unit tests and verify the debug APK builds cleanly before opening a Pull Request:
   ```bash
   ./gradlew testDebugUnitTest assembleDebug
   ```
5. Open a Pull Request describing what you changed and how you verified it (screenshots or screen recordings on device/emulator are appreciated for UI changes).

## Code Style & Guidelines

- Use standard Kotlin conventions (`kotlin.code.style=official`).
- Keep permissions minimal—never add broad permissions when narrow intents (such as `ACTION_DIAL` or `<queries>` for launcher activities) suffice.
- Keep all branding original (`GOBLINS`). Do not include third-party trademarked logos or proprietary assets.
