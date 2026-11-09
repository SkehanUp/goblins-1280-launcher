package com.goblins.launcher1280.model

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.goblins.launcher1280.analysis.Direction
import com.goblins.launcher1280.analysis.SnakeEngine
import com.goblins.launcher1280.analysis.T9SearchEngine
import com.goblins.launcher1280.backend.AppRepository
import com.goblins.launcher1280.backend.HardwareController
import com.goblins.launcher1280.backend.InstalledApp
import com.goblins.launcher1280.backend.LauncherPreferences
import com.goblins.launcher1280.backend.RingerProfile
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LauncherController(context: Context) {
    private val appRepository = AppRepository(context)
    private val hardwareController = HardwareController(context)
    private val preferences = LauncherPreferences(context)
    private val mainHandler = Handler(Looper.getMainLooper())

    private val timeFormat = SimpleDateFormat("HH:mm", Locale.US)
    private val dateFormat = SimpleDateFormat("EEE dd MMM", Locale.US)

    private var unlockPrimed = false
    private var bannerClearRunnable: Runnable? = null

    var uiState by mutableStateOf(LauncherUiState())
        private set

    init {
        val initialTheme = LcdTheme.fromId(preferences.themeId)
        val pinned = preferences.getPinnedPackages().toSet()
        val speedDials = preferences.getSpeedDials()
        val apps = sortAppsWithPinned(
            appRepository.loadLaunchableApps(T9SearchEngine::encodeLabel),
            pinned,
        )
        uiState = uiState.copy(
            theme = initialTheme,
            allApps = apps,
            filteredApps = apps,
            pinnedPackages = pinned,
            speedDials = speedDials,
        )
        refreshClockAndStatus()
    }

    fun refreshApps() {
        val pinned = preferences.getPinnedPackages().toSet()
        val apps = sortAppsWithPinned(
            appRepository.loadLaunchableApps(T9SearchEngine::encodeLabel),
            pinned,
        )
        val currentQuery = (uiState.screen as? LauncherScreen.AppDrawer)?.t9Query.orEmpty()
        uiState = uiState.copy(
            allApps = apps,
            pinnedPackages = pinned,
            speedDials = preferences.getSpeedDials(),
            filteredApps = filterApps(apps, currentQuery),
        )
    }

    fun refreshClockAndStatus() {
        val now = Date()
        val rawStatus = hardwareController.readSystemStatus()
        val customCarrier = preferences.carrierOverride.ifBlank { rawStatus.carrierLabel }
        uiState = uiState.copy(
            timeText = timeFormat.format(now),
            dateText = dateFormat.format(now).uppercase(Locale.US),
            systemStatus = rawStatus.copy(carrierLabel = customCarrier),
        )
    }

    fun tickSnake() {
        val currentScreen = uiState.screen as? LauncherScreen.SnakeGame ?: return
        val nextState = SnakeEngine.step(currentScreen.gameState)
        if (nextState.highScore > preferences.highScoreSnake) {
            preferences.highScoreSnake = nextState.highScore
        }
        uiState = uiState.copy(screen = LauncherScreen.SnakeGame(nextState))
    }

    // --- Direct Touch-First Actions ---

    fun goHome() {
        uiState = uiState.copy(
            screen = LauncherScreen.Home,
            filteredApps = uiState.allApps,
        )
    }

    fun openDrawerWithQuery(query: String = "", assignDigit: Int? = null) {
        openAppDrawer(query = query, assignDigit = assignDigit)
    }

    fun appendT9Digit(digit: Int) {
        when (val current = uiState.screen) {
            is LauncherScreen.AppDrawer -> {
                val next = (current.t9Query + digit.toString()).take(12)
                updateAppDrawerQuery(current, next)
            }

            is LauncherScreen.Dialer -> {
                uiState = uiState.copy(screen = LauncherScreen.Dialer((current.digits + digit.toString()).take(20)))
            }

            else -> {
                openAppDrawer(query = digit.toString())
            }
        }
    }

    fun backspaceT9() {
        when (val current = uiState.screen) {
            is LauncherScreen.AppDrawer -> {
                if (current.t9Query.isNotEmpty()) {
                    updateAppDrawerQuery(current, current.t9Query.dropLast(1))
                } else {
                    goHome()
                }
            }

            is LauncherScreen.Dialer -> {
                val next = current.digits.dropLast(1)
                uiState = if (next.isEmpty()) {
                    uiState.copy(screen = LauncherScreen.Home)
                } else {
                    uiState.copy(screen = LauncherScreen.Dialer(next))
                }
            }

            else -> Unit
        }
    }

    fun clearT9() {
        when (val current = uiState.screen) {
            is LauncherScreen.AppDrawer -> updateAppDrawerQuery(current, "")
            is LauncherScreen.Dialer -> uiState = uiState.copy(screen = LauncherScreen.Home)
            else -> Unit
        }
    }

    fun launchAppDirect(app: InstalledApp) {
        val current = uiState.screen
        if (current is LauncherScreen.AppDrawer && current.assignDigit != null) {
            preferences.setSpeedDial(current.assignDigit, app.packageName)
            uiState = uiState.copy(
                speedDials = preferences.getSpeedDials(),
                screen = LauncherScreen.SpeedDialManager(current.assignDigit),
            )
            showBanner("KEY ${current.assignDigit} -> ${app.label.uppercase(Locale.US)}")
            return
        }
        val launched = appRepository.launchApp(app.packageName)
        if (!launched) {
            showBanner("CANNOT OPEN APP")
        }
    }

    fun togglePinAppDirect(app: InstalledApp) {
        val isPinned = preferences.togglePinnedPackage(app.packageName)
        refreshApps()
        showBanner(if (isPinned) "PINNED: ${app.label.uppercase(Locale.US)}" else "UNPINNED: ${app.label.uppercase(Locale.US)}")
    }

    fun openAppInfoDirect(app: InstalledApp) {
        appRepository.openAppDetails(app.packageName)
    }

    fun toggleTorchDirect() {
        val on = hardwareController.toggleTorch()
        refreshClockAndStatus()
        showBanner(if (on) "TORCH ON" else "TORCH OFF")
    }

    fun toggleVibrateDirect() {
        val profile = hardwareController.toggleVibrateMode()
        refreshClockAndStatus()
        val text = when (profile) {
            RingerProfile.VIBRATE -> "PROFILE: VIBRATE"
            RingerProfile.SILENT -> "PROFILE: SILENT"
            RingerProfile.NORMAL -> "PROFILE: RING"
        }
        showBanner(text)
    }

    fun cycleThemeDirect() {
        val themes = LcdTheme.entries
        val next = themes[(themes.indexOf(uiState.theme) + 1) % themes.size]
        preferences.themeId = next.id
        uiState = uiState.copy(theme = next)
        showBanner("LCD: ${next.displayName.uppercase(Locale.US)}")
    }

    fun selectThemeDirect(theme: LcdTheme) {
        preferences.themeId = theme.id
        uiState = uiState.copy(theme = theme, screen = LauncherScreen.Home)
        showBanner("LCD: ${theme.displayName.uppercase(Locale.US)}")
    }

    fun cycleCarrierDirect() {
        val carriers = listOf("GOBLINS", "GOBLIN-NET", "PIXEL 10A", "1280-OS")
        val current = preferences.carrierOverride
        val next = carriers[(carriers.indexOf(current) + 1) % carriers.size]
        preferences.carrierOverride = next
        refreshClockAndStatus()
        showBanner("BANNER: $next")
    }

    fun openDialerDirect(number: String = "") {
        appRepository.openDialer(number)
    }

    fun openDialerScreen(initialDigits: String = "") {
        uiState = uiState.copy(screen = LauncherScreen.Dialer(initialDigits))
    }

    fun openSnakeDirect() {
        startSnakeGame()
    }

    fun steerSnakeDirect(direction: Direction) {
        val game = (uiState.screen as? LauncherScreen.SnakeGame)?.gameState ?: return
        steerSnake(game, direction)
    }

    fun toggleSnakePauseOrRestart() {
        val game = (uiState.screen as? LauncherScreen.SnakeGame)?.gameState ?: return
        if (game.isGameOver) {
            startSnakeGame()
        } else {
            uiState = uiState.copy(screen = LauncherScreen.SnakeGame(game.copy(isPaused = !game.isPaused)))
        }
    }

    fun openSettingsDirect() {
        uiState = uiState.copy(screen = LauncherScreen.SettingsMenu(0))
    }

    fun openDefaultHomeDirect() {
        appRepository.openDefaultHomeSettings()
    }

    fun onKeyPress(key: KeypadKey) {
        if (uiState.isKeypadLocked) {
            handleLockedKeyPress(key)
            return
        }
        unlockPrimed = false

        when (val screen = uiState.screen) {
            is LauncherScreen.Home -> handleHomeKey(key)
            is LauncherScreen.MainMenu -> handleMainMenuKey(screen, key)
            is LauncherScreen.AppDrawer -> handleAppDrawerKey(screen, key)
            is LauncherScreen.AppOptions -> handleAppOptionsKey(screen, key)
            is LauncherScreen.Dialer -> handleDialerKey(screen, key)
            is LauncherScreen.SnakeGame -> handleSnakeKey(screen, key)
            is LauncherScreen.ThemePicker -> handleThemePickerKey(screen, key)
            is LauncherScreen.SpeedDialManager -> handleSpeedDialManagerKey(screen, key)
            is LauncherScreen.SettingsMenu -> handleSettingsKey(screen, key)
        }
    }

    fun onKeyLongPress(key: KeypadKey) {
        if (uiState.isKeypadLocked) {
            if (key is KeypadKey.Star) {
                uiState = uiState.copy(isKeypadLocked = false)
                showBanner("KEYPAD UNLOCKED")
            } else {
                showBanner("HOLD * TO UNLOCK")
            }
            return
        }

        when (key) {
            is KeypadKey.Digit -> {
                if (key.digit == 0) {
                    toggleTorchDirect()
                } else if (key.digit == 1) {
                    openAppDrawer()
                } else if (key.digit in 2..9) {
                    handleSpeedDialLongPress(key.digit)
                }
            }

            KeypadKey.Pound -> {
                toggleVibrateDirect()
            }

            KeypadKey.Star -> {
                val drawer = uiState.screen as? LauncherScreen.AppDrawer
                if (drawer != null && drawer.t9Query.isNotEmpty()) {
                    clearT9()
                } else {
                    openDialerScreen()
                }
            }

            KeypadKey.NavCenter, KeypadKey.SoftLeft -> {
                val drawer = uiState.screen as? LauncherScreen.AppDrawer
                if (drawer != null) {
                    val selectedApp = uiState.filteredApps.getOrNull(drawer.selectedIndex)
                    if (selectedApp != null) {
                        uiState = uiState.copy(
                            screen = LauncherScreen.AppOptions(
                                app = selectedApp,
                                selectedOptionIndex = 0,
                                returnQuery = drawer.t9Query,
                            )
                        )
                    }
                }
            }

            KeypadKey.EndPower -> {
                goHome()
            }

            else -> onKeyPress(key)
        }
    }

    fun onBackPressed() {
        if (uiState.isKeypadLocked) {
            uiState = uiState.copy(isKeypadLocked = false)
            return
        }
        when (val screen = uiState.screen) {
            is LauncherScreen.Home -> Unit
            is LauncherScreen.AppOptions -> openAppDrawer(query = screen.returnQuery)
            is LauncherScreen.AppDrawer -> {
                if (screen.assignDigit != null) {
                    uiState = uiState.copy(screen = LauncherScreen.SpeedDialManager(screen.assignDigit))
                } else if (screen.t9Query.isNotEmpty()) {
                    updateAppDrawerQuery(screen, screen.t9Query.dropLast(1))
                } else {
                    goHome()
                }
            }

            else -> goHome()
        }
    }

    fun release() {
        bannerClearRunnable?.let(mainHandler::removeCallbacks)
        hardwareController.release()
    }

    private fun handleLockedKeyPress(key: KeypadKey) {
        if (key is KeypadKey.SoftLeft) {
            unlockPrimed = true
            showBanner("NOW PRESS *")
        } else if (key is KeypadKey.Star && unlockPrimed) {
            unlockPrimed = false
            uiState = uiState.copy(isKeypadLocked = false)
            showBanner("KEYPAD UNLOCKED")
        } else {
            unlockPrimed = false
            showBanner("PRESS UNLOCK THEN *")
        }
    }

    private fun handleHomeKey(key: KeypadKey) {
        when (key) {
            KeypadKey.SoftLeft, KeypadKey.NavCenter -> {
                uiState = uiState.copy(screen = LauncherScreen.MainMenu(0))
            }

            KeypadKey.SoftRight, KeypadKey.NavUp, KeypadKey.NavDown -> {
                openAppDrawer()
            }

            KeypadKey.NavLeft -> {
                startSnakeGame()
            }

            KeypadKey.NavRight -> {
                cycleThemeDirect()
            }

            KeypadKey.Call -> {
                appRepository.openDialer("")
            }

            is KeypadKey.Digit -> {
                openAppDrawer(query = key.digit.toString())
            }

            KeypadKey.Star -> {
                openDialerScreen()
            }

            KeypadKey.Pound -> {
                openAppDrawer()
            }

            KeypadKey.EndPower -> Unit
        }
    }

    private fun handleMainMenuKey(screen: LauncherScreen.MainMenu, key: KeypadKey) {
        val items = MainMenuItem.entries
        when (key) {
            KeypadKey.NavUp, KeypadKey.NavLeft -> {
                val next = if (screen.selectedIndex - 1 < 0) items.lastIndex else screen.selectedIndex - 1
                uiState = uiState.copy(screen = screen.copy(selectedIndex = next))
            }

            KeypadKey.NavDown, KeypadKey.NavRight -> {
                val next = (screen.selectedIndex + 1) % items.size
                uiState = uiState.copy(screen = screen.copy(selectedIndex = next))
            }

            KeypadKey.NavCenter, KeypadKey.SoftLeft -> {
                activateMenuItem(items[screen.selectedIndex])
            }

            is KeypadKey.Digit -> {
                val target = items.firstOrNull { it.index == key.digit }
                if (target != null) activateMenuItem(target)
            }

            KeypadKey.SoftRight, KeypadKey.EndPower -> {
                goHome()
            }

            else -> Unit
        }
    }

    private fun activateMenuItem(item: MainMenuItem) {
        when (item) {
            MainMenuItem.APPS -> openAppDrawer()
            MainMenuItem.SNAKE -> startSnakeGame()
            MainMenuItem.FLASHLIGHT -> toggleTorchDirect()
            MainMenuItem.THEMES -> cycleThemeDirect()
            MainMenuItem.SPEED_DIAL -> uiState = uiState.copy(screen = LauncherScreen.SpeedDialManager(2))
            MainMenuItem.SETTINGS -> uiState = uiState.copy(screen = LauncherScreen.SettingsMenu(0))
        }
    }

    private fun handleAppDrawerKey(screen: LauncherScreen.AppDrawer, key: KeypadKey) {
        val list = uiState.filteredApps
        when (key) {
            KeypadKey.NavUp, KeypadKey.NavLeft -> {
                if (list.isNotEmpty()) {
                    val prev = if (screen.selectedIndex - 1 < 0) list.lastIndex else screen.selectedIndex - 1
                    uiState = uiState.copy(screen = screen.copy(selectedIndex = prev))
                }
            }

            KeypadKey.NavDown, KeypadKey.NavRight -> {
                if (list.isNotEmpty()) {
                    val next = (screen.selectedIndex + 1) % list.size
                    uiState = uiState.copy(screen = screen.copy(selectedIndex = next))
                }
            }

            KeypadKey.NavCenter, KeypadKey.SoftLeft, KeypadKey.Call -> {
                val selected = list.getOrNull(screen.selectedIndex) ?: return
                launchAppDirect(selected)
            }

            KeypadKey.SoftRight -> {
                backspaceT9()
            }

            KeypadKey.EndPower -> {
                goHome()
            }

            is KeypadKey.Digit -> {
                val nextQuery = (screen.t9Query + key.digit.toString()).take(12)
                updateAppDrawerQuery(screen, nextQuery)
            }

            KeypadKey.Star -> {
                if (screen.t9Query.isNotEmpty()) {
                    backspaceT9()
                } else {
                    goHome()
                }
            }

            KeypadKey.Pound -> {
                val selected = list.getOrNull(0) ?: return
                launchAppDirect(selected)
            }
        }
    }

    private fun handleAppOptionsKey(screen: LauncherScreen.AppOptions, key: KeypadKey) {
        val optionsCount = 4
        when (key) {
            KeypadKey.NavUp, KeypadKey.NavLeft -> {
                val prev = if (screen.selectedOptionIndex - 1 < 0) optionsCount - 1 else screen.selectedOptionIndex - 1
                uiState = uiState.copy(screen = screen.copy(selectedOptionIndex = prev))
            }

            KeypadKey.NavDown, KeypadKey.NavRight -> {
                val next = (screen.selectedOptionIndex + 1) % optionsCount
                uiState = uiState.copy(screen = screen.copy(selectedOptionIndex = next))
            }

            KeypadKey.NavCenter, KeypadKey.SoftLeft -> {
                when (screen.selectedOptionIndex) {
                    0 -> {
                        appRepository.launchApp(screen.app.packageName)
                        goHome()
                    }

                    1 -> {
                        togglePinAppDirect(screen.app)
                        openAppDrawer(query = screen.returnQuery)
                    }

                    2 -> {
                        uiState = uiState.copy(screen = LauncherScreen.SpeedDialManager(2))
                    }

                    3 -> {
                        appRepository.openAppDetails(screen.app.packageName)
                    }
                }
            }

            KeypadKey.SoftRight -> openAppDrawer(query = screen.returnQuery)
            KeypadKey.EndPower -> goHome()
            else -> Unit
        }
    }

    private fun handleDialerKey(screen: LauncherScreen.Dialer, key: KeypadKey) {
        when (key) {
            is KeypadKey.Digit -> {
                uiState = uiState.copy(screen = LauncherScreen.Dialer((screen.digits + key.digit).take(20)))
            }

            KeypadKey.Star -> {
                backspaceT9()
            }

            KeypadKey.Pound -> {
                appRepository.openDialer(screen.digits)
            }

            KeypadKey.SoftLeft, KeypadKey.NavCenter, KeypadKey.Call -> {
                appRepository.openDialer(screen.digits)
            }

            KeypadKey.SoftRight -> {
                backspaceT9()
            }

            KeypadKey.EndPower -> {
                goHome()
            }

            else -> Unit
        }
    }

    private fun handleSnakeKey(screen: LauncherScreen.SnakeGame, key: KeypadKey) {
        val game = screen.gameState
        when (key) {
            KeypadKey.NavUp -> steerSnake(game, Direction.UP)
            KeypadKey.NavDown -> steerSnake(game, Direction.DOWN)
            KeypadKey.NavLeft -> steerSnake(game, Direction.LEFT)
            KeypadKey.NavRight -> steerSnake(game, Direction.RIGHT)
            is KeypadKey.Digit -> {
                when (key.digit) {
                    2 -> steerSnake(game, Direction.UP)
                    8 -> steerSnake(game, Direction.DOWN)
                    4 -> steerSnake(game, Direction.LEFT)
                    6 -> steerSnake(game, Direction.RIGHT)
                }
            }

            KeypadKey.NavCenter, KeypadKey.SoftLeft, KeypadKey.Pound -> {
                toggleSnakePauseOrRestart()
            }

            KeypadKey.SoftRight, KeypadKey.Star, KeypadKey.EndPower -> {
                goHome()
            }

            else -> Unit
        }
    }

    private fun steerSnake(game: com.goblins.launcher1280.analysis.SnakeState, direction: Direction) {
        val next = SnakeEngine.steer(game, direction)
        uiState = uiState.copy(screen = LauncherScreen.SnakeGame(next))
    }

    private fun handleThemePickerKey(screen: LauncherScreen.ThemePicker, key: KeypadKey) {
        val themes = LcdTheme.entries
        when (key) {
            KeypadKey.NavUp, KeypadKey.NavLeft -> {
                val prev = if (screen.selectedIndex - 1 < 0) themes.lastIndex else screen.selectedIndex - 1
                val preview = themes[prev]
                uiState = uiState.copy(theme = preview, screen = screen.copy(selectedIndex = prev))
            }

            KeypadKey.NavDown, KeypadKey.NavRight -> {
                val next = (screen.selectedIndex + 1) % themes.size
                val preview = themes[next]
                uiState = uiState.copy(theme = preview, screen = screen.copy(selectedIndex = next))
            }

            KeypadKey.NavCenter, KeypadKey.SoftLeft -> {
                selectThemeDirect(themes[screen.selectedIndex])
            }

            KeypadKey.SoftRight, KeypadKey.EndPower, KeypadKey.Star -> {
                val saved = LcdTheme.fromId(preferences.themeId)
                uiState = uiState.copy(theme = saved, screen = LauncherScreen.Home)
            }

            else -> Unit
        }
    }

    private fun handleSpeedDialManagerKey(screen: LauncherScreen.SpeedDialManager, key: KeypadKey) {
        when (key) {
            KeypadKey.NavUp, KeypadKey.NavLeft -> {
                val prev = if (screen.selectedDigit <= 2) 9 else screen.selectedDigit - 1
                uiState = uiState.copy(screen = screen.copy(selectedDigit = prev))
            }

            KeypadKey.NavDown, KeypadKey.NavRight -> {
                val next = if (screen.selectedDigit >= 9) 2 else screen.selectedDigit + 1
                uiState = uiState.copy(screen = screen.copy(selectedDigit = next))
            }

            is KeypadKey.Digit -> {
                if (key.digit in 2..9) {
                    openAppDrawer(assignDigit = key.digit)
                }
            }

            KeypadKey.NavCenter, KeypadKey.SoftLeft -> {
                openAppDrawer(assignDigit = screen.selectedDigit)
            }

            KeypadKey.SoftRight, KeypadKey.Star, KeypadKey.EndPower -> {
                goHome()
            }

            else -> Unit
        }
    }

    private fun handleSettingsKey(screen: LauncherScreen.SettingsMenu, key: KeypadKey) {
        when (key) {
            is KeypadKey.Digit -> {
                when (key.digit) {
                    1 -> appRepository.openDefaultHomeSettings()
                    2 -> {
                        refreshApps()
                        showBanner("RELOADED ${uiState.allApps.size} APPS")
                    }

                    3 -> cycleCarrierDirect()
                    4 -> cycleThemeDirect()
                }
            }

            KeypadKey.SoftRight, KeypadKey.Star, KeypadKey.EndPower -> goHome()
            else -> Unit
        }
    }

    private fun handleSpeedDialLongPress(digit: Int) {
        val pkg = uiState.speedDials[digit]
        if (pkg != null) {
            val launched = appRepository.launchApp(pkg)
            if (!launched) {
                showBanner("APP NOT FOUND")
            }
        } else {
            openAppDrawer(assignDigit = digit)
            showBanner("TAP APP FOR KEY $digit")
        }
    }

    private fun openAppDrawer(query: String = "", assignDigit: Int? = null) {
        val filtered = filterApps(uiState.allApps, query)
        uiState = uiState.copy(
            filteredApps = filtered,
            screen = LauncherScreen.AppDrawer(
                t9Query = query,
                selectedIndex = 0,
                assignDigit = assignDigit,
            ),
        )
    }

    private fun updateAppDrawerQuery(screen: LauncherScreen.AppDrawer, nextQuery: String) {
        val filtered = filterApps(uiState.allApps, nextQuery)
        uiState = uiState.copy(
            filteredApps = filtered,
            screen = screen.copy(
                t9Query = nextQuery,
                selectedIndex = 0,
            ),
        )
    }

    private fun startSnakeGame() {
        val initial = SnakeEngine.initialState(
            width = 20,
            height = 12,
            highScore = preferences.highScoreSnake,
            randomSeed = System.currentTimeMillis(),
        )
        uiState = uiState.copy(screen = LauncherScreen.SnakeGame(initial))
    }

    private fun filterApps(apps: List<InstalledApp>, query: String): List<InstalledApp> =
        T9SearchEngine.filterAndRank(
            items = apps,
            queryDigits = query,
            labelSelector = { it.label },
            digitsSelector = { it.t9Digits },
        )

    private fun sortAppsWithPinned(apps: List<InstalledApp>, pinned: Set<String>): List<InstalledApp> =
        apps.sortedWith(
            compareByDescending<InstalledApp> { it.packageName in pinned }
                .thenBy(String.CASE_INSENSITIVE_ORDER) { it.label }
        )

    private fun showBanner(message: String) {
        bannerClearRunnable?.let(mainHandler::removeCallbacks)
        uiState = uiState.copy(bannerMessage = message)
        val clearTask = Runnable {
            uiState = uiState.copy(bannerMessage = null)
        }
        bannerClearRunnable = clearTask
        mainHandler.postDelayed(clearTask, 1500L)
    }
}
