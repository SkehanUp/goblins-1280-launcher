package com.goblins.launcher1280.model

import androidx.compose.ui.graphics.Color
import com.goblins.launcher1280.analysis.Direction
import com.goblins.launcher1280.analysis.SnakeState
import com.goblins.launcher1280.backend.InstalledApp
import com.goblins.launcher1280.backend.RingerProfile
import com.goblins.launcher1280.backend.SystemStatus

enum class LcdTheme(
    val id: String,
    val displayName: String,
    val lcdLit: Color,
    val lcdInk: Color,
    val lcdGhost: Color,
    val bezelRing: Color,
) {
    GOBLIN_GREEN(
        id = "GOBLIN_GREEN",
        displayName = "Goblin Green",
        lcdLit = Color(0xFF9BBC0F),
        lcdInk = Color(0xFF0F2A0F),
        lcdGhost = Color(0xFF8BAC0F),
        bezelRing = Color(0xFF3E4B39),
    ),
    AMBER_GOLD(
        id = "AMBER_GOLD",
        displayName = "Amber Backlight",
        lcdLit = Color(0xFFD99B26),
        lcdInk = Color(0xFF231604),
        lcdGhost = Color(0xFFC4881B),
        bezelRing = Color(0xFF4A3D28),
    ),
    ICE_BLUE(
        id = "ICE_BLUE",
        displayName = "Ice Cyan",
        lcdLit = Color(0xFF9CC5D9),
        lcdInk = Color(0xFF0D212C),
        lcdGhost = Color(0xFF89B3C7),
        bezelRing = Color(0xFF33444D),
    ),
    MONOCHROME_PAPER(
        id = "MONOCHROME_PAPER",
        displayName = "Paper Gray",
        lcdLit = Color(0xFFB8C0B4),
        lcdInk = Color(0xFF171A16),
        lcdGhost = Color(0xFFA5AEA1),
        bezelRing = Color(0xFF424740),
    );

    companion object {
        fun fromId(id: String): LcdTheme = entries.firstOrNull { it.id == id } ?: GOBLIN_GREEN
    }
}

enum class MainMenuItem(val index: Int, val title: String, val code: String, val subtitle: String) {
    APPS(1, "APPS", "Menu 1", "Installed Android Apps"),
    SNAKE(2, "SNAKE XENZIA", "Menu 2", "Classic Goblin Snake"),
    FLASHLIGHT(3, "TORCH LIGHT", "Menu 3", "Toggle Rear LED Flash"),
    THEMES(4, "LCD DISPLAY", "Menu 4", "Backlight & Contrast"),
    SPEED_DIAL(5, "SPEED DIALS", "Menu 5", "Assign Keys 2-9"),
    SETTINGS(6, "PHONE SETUP", "Menu 6", "Default Home & Info"),
}

sealed interface LauncherScreen {
    data object Home : LauncherScreen
    data class MainMenu(val selectedIndex: Int = 0) : LauncherScreen
    data class AppDrawer(
        val t9Query: String = "",
        val selectedIndex: Int = 0,
        val assignDigit: Int? = null,
    ) : LauncherScreen
    data class AppOptions(
        val app: InstalledApp,
        val selectedOptionIndex: Int = 0,
        val returnQuery: String = "",
    ) : LauncherScreen
    data class Dialer(val digits: String) : LauncherScreen
    data class SnakeGame(val gameState: SnakeState) : LauncherScreen
    data class ThemePicker(val selectedIndex: Int = 0) : LauncherScreen
    data class SpeedDialManager(val selectedDigit: Int = 2) : LauncherScreen
    data class SettingsMenu(val selectedIndex: Int = 0) : LauncherScreen
}

sealed interface KeypadKey {
    data object SoftLeft : KeypadKey
    data object SoftRight : KeypadKey
    data object NavUp : KeypadKey
    data object NavDown : KeypadKey
    data object NavLeft : KeypadKey
    data object NavRight : KeypadKey
    data object NavCenter : KeypadKey
    data object Call : KeypadKey
    data object EndPower : KeypadKey
    data class Digit(val digit: Int, val letters: String) : KeypadKey
    data object Star : KeypadKey
    data object Pound : KeypadKey
}

data class LauncherUiState(
    val screen: LauncherScreen = LauncherScreen.Home,
    val theme: LcdTheme = LcdTheme.GOBLIN_GREEN,
    val systemStatus: SystemStatus = SystemStatus(
        batteryPercent = 100,
        batteryBars = 7,
        isCharging = false,
        signalBars = 7,
        carrierLabel = "GOBLINS",
        torchEnabled = false,
        ringerProfile = RingerProfile.NORMAL,
    ),
    val timeText: String = "12:00",
    val dateText: String = "MON 09 NOV",
    val allApps: List<InstalledApp> = emptyList(),
    val filteredApps: List<InstalledApp> = emptyList(),
    val pinnedPackages: Set<String> = emptySet(),
    val speedDials: Map<Int, String> = emptyMap(),
    val isKeypadLocked: Boolean = false,
    val bannerMessage: String? = null,
) {
    val leftSoftKeyLabel: String
        get() = if (isKeypadLocked) {
            "Unlock"
        } else {
            when (val current = screen) {
                is LauncherScreen.Home -> "Menu"
                is LauncherScreen.MainMenu -> "Select"
                is LauncherScreen.AppDrawer -> if (current.assignDigit != null) "Assign" else "Open"
                is LauncherScreen.AppOptions -> "OK"
                is LauncherScreen.Dialer -> "Call"
                is LauncherScreen.SnakeGame -> if (current.gameState.isGameOver) "Retry" else if (current.gameState.isPaused) "Resume" else "Pause"
                is LauncherScreen.ThemePicker -> "Apply"
                is LauncherScreen.SpeedDialManager -> "Assign"
                is LauncherScreen.SettingsMenu -> "Select"
            }
        }

    val rightSoftKeyLabel: String
        get() = if (isKeypadLocked) {
            "*"
        } else {
            when (val current = screen) {
                is LauncherScreen.Home -> "Apps"
                is LauncherScreen.MainMenu -> "Back"
                is LauncherScreen.AppDrawer -> if (current.t9Query.isNotEmpty()) "Clear" else "Opts"
                is LauncherScreen.AppOptions -> "Back"
                is LauncherScreen.Dialer -> "Delete"
                is LauncherScreen.SnakeGame -> "Exit"
                is LauncherScreen.ThemePicker -> "Back"
                is LauncherScreen.SpeedDialManager -> "Clear"
                is LauncherScreen.SettingsMenu -> "Back"
            }
        }
}

data class SnakeControls(
    val onSteer: (Direction) -> Unit,
)
