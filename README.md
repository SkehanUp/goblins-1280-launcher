# GOBLINS 1280 Launcher

A free, open-source retro monochrome feature-phone home launcher for Android, custom-built for the **Pixel 10a** and its centered circular punch-hole camera.

Built with **Kotlin** and **Jetpack Compose**, GOBLINS 1280 turns your modern Android phone into a tactile 1280-style keypad device with an authentic dot-matrix LCD display, predictive T9 app search, hardware shortcuts, and a classic Snake game.

## Features

- **Pixel 10a Camera-Integrated Chassis**: The top speaker grille and circular metallic housing ring align with the Pixel 10a's top-center punch-hole camera so the lens blends naturally into the retro earpiece.
- **Custom GOBLINS Branding**: Embossed metallic `GOBLINS` badge and custom carrier banners (`GOBLINS`, `GOBLIN-NET`, `PIXEL 10A`, `1280-OS`).
- **Dot-Matrix Monochrome LCD**:
  - Subtle pixel grid and scanline canvas overlay.
  - Classic 7-bar vertical signal and battery towers on the left and right edges.
  - 4 selectable backlight themes: **Goblin Green**, **Amber Backlight**, **Ice Cyan**, and **Paper Gray**.
- **Tactile Keypad & Haptics**:
  - Left/Right softkeys, Call/End buttons, raised 4-way Navi-Key ring with center `OK`, and `0-9` / `*` / `#` T9 keys with click and long-press haptic feedback.
- **T9 Predictive App Drawer**:
  - Browse all installed launchable Android apps.
  - Filter apps instantly using T9 digits (matches prefixes, word initials like `46` for Google Maps, word boundaries, and substrings).
  - Pin favorite apps to the top (`#` or long-press `OK`).
- **Hardware Shortcuts & Speed Dials**:
  - **Hold `0`**: Toggle rear LED flashlight (`CameraManager` torch).
  - **Hold `#`**: Toggle Vibrate / Normal ringer profile.
  - **Hold `*`**: Lock or unlock the keypad.
  - **Hold `2`–`9`**: Launch or assign speed-dial apps.
  - **Type digits on Home**: Enter the retro dialer screen and press Call to open the system dialer with the number pre-filled.
- **Snake Xenzia**: Built-in monochrome grid Snake game (`D-Pad` or `2`/`4`/`6`/`8`) with persistent high scores.

## Building & Installing

### Prerequisites

- JDK 17+ (or Android Studio's bundled JBR)
- Android SDK Platform 35

### Build Debug APK

```bash
./gradlew testDebugUnitTest assembleDebug
```

The compiled APK will be generated at:

```text
app/build/outputs/apk/debug/app-debug.apk
```

### Install via ADB

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

To set GOBLINS 1280 as your default home screen, open the launcher and navigate to:
`Menu -> 6. PHONE SETUP -> 1. SET DEFAULT HOME`

## Project Structure

- `app/src/main/java/com/goblins/launcher1280/ui/`: Jetpack Compose LCD screen (`LcdScreen.kt`) and tactile keypad (`GoblinsKeypad.kt`).
- `app/src/main/java/com/goblins/launcher1280/model/`: UI state, theme definitions, and `LauncherController`.
- `app/src/main/java/com/goblins/launcher1280/backend/`: Android `PackageManager` app queries, torch/battery/audio hardware controller, and `SharedPreferences` storage.
- `app/src/main/java/com/goblins/launcher1280/analysis/`: Pure Kotlin `T9SearchEngine` and `SnakeEngine` with JUnit tests.

## Contributing

Contributions, bug reports, and feature ideas are welcome! Please read [CONTRIBUTING.md](CONTRIBUTING.md) before opening an issue or pull request.

## License

Released under the [MIT License](LICENSE).
