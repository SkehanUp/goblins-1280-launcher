package com.goblins.launcher1280.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.goblins.launcher1280.analysis.Direction
import com.goblins.launcher1280.backend.InstalledApp
import com.goblins.launcher1280.backend.RingerProfile
import com.goblins.launcher1280.model.LauncherController
import com.goblins.launcher1280.model.LauncherScreen
import com.goblins.launcher1280.model.LauncherUiState
import com.goblins.launcher1280.model.LcdTheme
import kotlin.math.abs

@Composable
fun GoblinsLcdAssembly(
    state: LauncherUiState,
    controller: LauncherController,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 34.dp, topEnd = 34.dp, bottomStart = 18.dp, bottomEnd = 18.dp))
            .background(Brush.verticalGradient(listOf(Color(0xFF232824), Color(0xFF141816))))
            .border(
                width = 1.5.dp,
                color = state.theme.bezelRing,
                shape = RoundedCornerShape(topStart = 34.dp, topEnd = 34.dp, bottomStart = 18.dp, bottomEnd = 18.dp),
            )
            .padding(horizontal = 10.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Pixel10aCameraSpeakerHeader(
            onCycleTheme = controller::cycleThemeDirect,
            onOpenSettings = controller::openSettingsDirect,
        )
        Spacer(Modifier.height(6.dp))
        LcdTouchGlass(
            state = state,
            controller = controller,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        )
    }
}

@Composable
private fun Pixel10aCameraSpeakerHeader(
    onCycleTheme: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(34.dp)
            .padding(horizontal = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        // Left: GOBLINS metallic mini-badge (tap to cycle LCD backlight theme)
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(Brush.verticalGradient(listOf(Color(0xFFD5D8CE), Color(0xFF7B8077))))
                .clickable(onClick = onCycleTheme)
                .padding(horizontal = 8.dp, vertical = 2.dp),
        ) {
            Text(
                text = "GOBLINS",
                color = Color(0xFF141715),
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.8.sp,
                fontFamily = FontFamily.Monospace,
            )
        }

        // Center: Pixel 10a circular camera punch-hole ring + speaker slots
        Box(
            modifier = Modifier
                .weight(1f)
                .height(32.dp),
            contentAlignment = Alignment.Center,
        ) {
            Canvas(Modifier.fillMaxSize()) {
                val centerX = size.width / 2f
                val centerY = size.height / 2f
                val slotColor = Color(0xFF0A0C0B)
                val slotWidth = size.width * 0.20f
                val gap = 22.dp.toPx()
                for (row in -1..1) {
                    val y = centerY + row * 4.dp.toPx()
                    drawRoundRect(
                        color = slotColor,
                        topLeft = Offset(centerX - gap - slotWidth, y - 1.dp.toPx()),
                        size = Size(slotWidth, 2.dp.toPx()),
                        cornerRadius = CornerRadius(2.dp.toPx()),
                    )
                    drawRoundRect(
                        color = slotColor,
                        topLeft = Offset(centerX + gap, y - 1.dp.toPx()),
                        size = Size(slotWidth, 2.dp.toPx()),
                        cornerRadius = CornerRadius(2.dp.toPx()),
                    )
                }
            }
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(Brush.radialGradient(listOf(Color(0xFF4B524D), Color(0xFF121513))))
                    .border(1.5.dp, Color(0xFF7C837C), CircleShape)
                    .padding(3.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF050606)),
            )
        }

        // Right: Model tag (tap for setup/settings)
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .border(1.dp, Color(0xFF565C56), RoundedCornerShape(4.dp))
                .clickable(onClick = onOpenSettings)
                .padding(horizontal = 8.dp, vertical = 2.dp),
        ) {
            Text(
                text = "1280·OS",
                color = Color(0xFFB4BBB2),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp,
                fontFamily = FontFamily.Monospace,
            )
        }
    }
}

@Composable
private fun LcdTouchGlass(
    state: LauncherUiState,
    controller: LauncherController,
    modifier: Modifier = Modifier,
) {
    val theme = state.theme
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(theme.lcdLit)
            .border(2.dp, theme.lcdInk.copy(alpha = 0.35f), RoundedCornerShape(14.dp)),
    ) {
        // Subtle monochrome LCD scanline & dot texture
        Canvas(Modifier.fillMaxSize()) {
            val step = 6.dp.toPx()
            var y = 2.dp.toPx()
            while (y < size.height) {
                drawLine(
                    color = theme.lcdInk.copy(alpha = 0.04f),
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 0.7.dp.toPx(),
                )
                y += step
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 8.dp),
        ) {
            TopStatusStrip(state, controller)
            Spacer(Modifier.height(6.dp))

            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SignalTower(state.systemStatus.signalBars, theme)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(horizontal = 6.dp),
                ) {
                    when (val screen = state.screen) {
                        LauncherScreen.Home -> TouchHomeContent(state, controller)
                        is LauncherScreen.AppDrawer -> TouchAppDrawerContent(screen, state, controller)
                        is LauncherScreen.Dialer -> TouchDialerContent(screen.digits, state, controller)
                        is LauncherScreen.SnakeGame -> TouchSnakeContent(screen.gameState, theme, controller)
                        is LauncherScreen.SettingsMenu -> TouchSettingsContent(state, controller)
                        is LauncherScreen.SpeedDialManager -> TouchSpeedDialContent(state, controller)
                        else -> TouchHomeContent(state, controller)
                    }
                }
                BatteryTower(
                    bars = state.systemStatus.batteryBars,
                    percent = state.systemStatus.batteryPercent,
                    charging = state.systemStatus.isCharging,
                    theme = theme,
                )
            }

            Spacer(Modifier.height(6.dp))
            BottomTouchActionStrip(state, controller)
        }

        state.bannerMessage?.let { message ->
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 36.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(theme.lcdInk)
                    .border(1.5.dp, theme.lcdLit, RoundedCornerShape(4.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center,
            ) {
                LcdText(
                    text = message,
                    color = theme.lcdLit,
                    size = 12,
                    weight = FontWeight.Bold,
                    align = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun TopStatusStrip(
    state: LauncherUiState,
    controller: LauncherController,
) {
    val theme = state.theme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, theme.lcdInk.copy(alpha = 0.25f), RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        LcdText(
            text = "⌁ ${state.systemStatus.carrierLabel.uppercase()}",
            color = theme.lcdInk,
            size = 11,
            weight = FontWeight.Bold,
            modifier = Modifier.clickable { controller.cycleCarrierDirect() },
            maxLines = 1,
        )

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
            LcdChip(
                text = if (state.systemStatus.torchEnabled) "TORCH:ON" else "TORCH",
                inverted = state.systemStatus.torchEnabled,
                theme = theme,
                onClick = controller::toggleTorchDirect,
            )
            LcdChip(
                text = when (state.systemStatus.ringerProfile) {
                    RingerProfile.VIBRATE -> "VIB"
                    RingerProfile.SILENT -> "SILENT"
                    RingerProfile.NORMAL -> "RING"
                },
                inverted = state.systemStatus.ringerProfile != RingerProfile.NORMAL,
                theme = theme,
                onClick = controller::toggleVibrateDirect,
            )
            LcdChip(
                text = "THEME",
                inverted = false,
                theme = theme,
                onClick = controller::cycleThemeDirect,
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ColumnScope.TouchHomeContent(
    state: LauncherUiState,
    controller: LauncherController,
) {
    val theme = state.theme
    val haptics = LocalHapticFeedback.current

    // Big Minimal Retro Clock & Date Banner (Tap clock -> All Apps, Long-press -> Settings)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .combinedClickable(
                onClick = { controller.openDrawerWithQuery("") },
                onLongClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    controller.openSettingsDirect()
                },
            )
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        LcdText(
            text = state.timeText,
            color = theme.lcdInk,
            size = 48,
            weight = FontWeight.Black,
            align = TextAlign.Center,
            maxLines = 1,
        )
        LcdText(
            text = "${state.dateText}  ·  ${state.systemStatus.batteryPercent}%",
            color = theme.lcdInk.copy(alpha = 0.85f),
            size = 12,
            weight = FontWeight.Bold,
            align = TextAlign.Center,
            maxLines = 1,
        )
    }

    // Divider line
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.5.dp)
            .background(theme.lcdInk.copy(alpha = 0.3f)),
    )

    Spacer(Modifier.height(6.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LcdText(
            text = if (state.pinnedPackages.isEmpty()) "QUICK APPS (HOLD TO PIN)" else "PINNED APPS",
            color = theme.lcdInk.copy(alpha = 0.75f),
            size = 10,
            weight = FontWeight.Bold,
        )
        LcdText(
            text = "ALL APPS (${state.allApps.size}) ▶",
            color = theme.lcdInk,
            size = 10,
            weight = FontWeight.Bold,
            modifier = Modifier.clickable { controller.openDrawerWithQuery("") },
        )
    }

    Spacer(Modifier.height(4.dp))

    // Direct touch favorite/top apps list right on the home screen
    val homeApps = state.allApps.take(7)
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        itemsIndexed(homeApps, key = { _, app -> app.packageName }) { idx, app ->
            val isPinned = app.packageName in state.pinnedPackages
            TouchAppRow(
                indexNumber = idx + 1,
                app = app,
                isPinned = isPinned,
                isHighlighted = false,
                theme = theme,
                onTap = {
                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    controller.launchAppDirect(app)
                },
                onLongTap = {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    controller.togglePinAppDirect(app)
                },
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ColumnScope.TouchAppDrawerContent(
    screen: LauncherScreen.AppDrawer,
    state: LauncherUiState,
    controller: LauncherController,
) {
    val theme = state.theme
    val haptics = LocalHapticFeedback.current

    // Top T9 Search Filter Header
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(theme.lcdInk)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        val title = when {
            screen.assignDigit != null -> "ASSIGN SPEED KEY ${screen.assignDigit}"
            screen.t9Query.isEmpty() -> "ALL APPS (${state.filteredApps.size}) · TYPE T9 BELOW"
            else -> "T9: ${screen.t9Query}_  (${state.filteredApps.size} MATCH)"
        }
        LcdText(
            text = title,
            color = theme.lcdLit,
            size = 12,
            weight = FontWeight.Bold,
            modifier = Modifier.weight(1f),
            maxLines = 1,
        )
        if (screen.t9Query.isNotEmpty()) {
            LcdText(
                text = "[CLR]",
                color = theme.lcdLit,
                size = 11,
                weight = FontWeight.Bold,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .clickable { controller.clearT9() },
            )
        }
    }

    Spacer(Modifier.height(6.dp))

    val apps = state.filteredApps
    if (apps.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                LcdText("NO APPS MATCH '${screen.t9Query}'", theme.lcdInk, size = 13, weight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                LcdChip(
                    text = "DIAL ${screen.t9Query} INSTEAD",
                    inverted = true,
                    theme = theme,
                    onClick = { controller.openDialerDirect(screen.t9Query) },
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            itemsIndexed(apps, key = { _, app -> app.packageName }) { index, app ->
                val isPinned = app.packageName in state.pinnedPackages
                TouchAppRow(
                    indexNumber = index + 1,
                    app = app,
                    isPinned = isPinned,
                    isHighlighted = index == 0 && screen.t9Query.isNotEmpty(),
                    theme = theme,
                    onTap = {
                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        controller.launchAppDirect(app)
                    },
                    onLongTap = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        controller.togglePinAppDirect(app)
                    },
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TouchAppRow(
    indexNumber: Int,
    app: InstalledApp,
    isPinned: Boolean,
    isHighlighted: Boolean,
    theme: LcdTheme,
    onTap: () -> Unit,
    onLongTap: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(if (isHighlighted) theme.lcdInk else theme.lcdInk.copy(alpha = 0.08f))
            .border(
                width = 1.dp,
                color = if (isHighlighted) theme.lcdInk else theme.lcdInk.copy(alpha = 0.22f),
                shape = RoundedCornerShape(4.dp),
            )
            .combinedClickable(
                onClick = onTap,
                onLongClick = onLongTap,
            )
            .padding(horizontal = 10.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        val fg = if (isHighlighted) theme.lcdLit else theme.lcdInk
        val pinMark = if (isPinned) "★ " else ""
        LcdText(
            text = "${indexNumber.toString().padStart(2, '0')}. $pinMark${app.label.uppercase()}",
            color = fg,
            size = 14,
            weight = FontWeight.Bold,
            modifier = Modifier.weight(1f),
            maxLines = 1,
        )
        LcdText(
            text = if (isHighlighted) "OPEN ↵" else "›",
            color = fg.copy(alpha = 0.8f),
            size = 12,
            weight = FontWeight.Bold,
        )
    }
}

@Composable
private fun ColumnScope.TouchDialerContent(
    digits: String,
    state: LauncherUiState,
    controller: LauncherController,
) {
    val theme = state.theme
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        LcdText("GOBLINS DIALER", theme.lcdInk.copy(alpha = 0.7f), size = 12, weight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .border(1.5.dp, theme.lcdInk, RoundedCornerShape(6.dp))
                .padding(horizontal = 12.dp, vertical = 16.dp),
            contentAlignment = Alignment.Center,
        ) {
            LcdText(
                text = digits.ifEmpty { "TYPE NUMBER BELOW" },
                color = theme.lcdInk,
                size = if (digits.isEmpty()) 14 else 28,
                weight = FontWeight.Black,
                align = TextAlign.Center,
                maxLines = 1,
            )
        }
        Spacer(Modifier.height(14.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            LcdChip(
                text = "☎ CALL NUMBER",
                inverted = true,
                theme = theme,
                onClick = { controller.openDialerDirect(digits) },
            )
            LcdChip(
                text = "⌫ DEL",
                inverted = false,
                theme = theme,
                onClick = controller::backspaceT9,
            )
            LcdChip(
                text = "✕ HOME",
                inverted = false,
                theme = theme,
                onClick = controller::goHome,
            )
        }
    }
}

@Composable
private fun ColumnScope.TouchSnakeContent(
    gameState: com.goblins.launcher1280.analysis.SnakeState,
    theme: LcdTheme,
    controller: LauncherController,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            LcdText(
                text = "SNAKE · SCORE ${gameState.score} (HI ${gameState.highScore})",
                color = theme.lcdInk,
                size = 11,
                weight = FontWeight.Bold,
            )
            LcdChip(
                text = if (gameState.isGameOver) "RETRY" else if (gameState.isPaused) "RESUME" else "PAUSE",
                inverted = true,
                theme = theme,
                onClick = controller::toggleSnakePauseOrRestart,
            )
        }
        Spacer(Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        val (dx, dy) = dragAmount
                        if (abs(dx) > 12f || abs(dy) > 12f) {
                            if (abs(dx) > abs(dy)) {
                                controller.steerSnakeDirect(if (dx > 0) Direction.RIGHT else Direction.LEFT)
                            } else {
                                controller.steerSnakeDirect(if (dy > 0) Direction.DOWN else Direction.UP)
                            }
                        }
                    }
                },
            contentAlignment = Alignment.Center,
        ) {
            Canvas(Modifier.fillMaxSize()) {
                val cellWidth = size.width / gameState.width.coerceAtLeast(1)
                val cellHeight = size.height / gameState.height.coerceAtLeast(1)
                val cell = minOf(cellWidth, cellHeight)
                val boardWidth = cell * gameState.width
                val boardHeight = cell * gameState.height
                val left = (size.width - boardWidth) / 2f
                val top = (size.height - boardHeight) / 2f

                drawRect(
                    color = theme.lcdInk.copy(alpha = 0.10f),
                    topLeft = Offset(left, top),
                    size = Size(boardWidth, boardHeight),
                )
                drawRect(
                    color = theme.lcdInk,
                    topLeft = Offset(left, top),
                    size = Size(boardWidth, boardHeight),
                    style = Stroke(width = 1.5.dp.toPx()),
                )
                gameState.snake.forEach { point ->
                    drawRoundRect(
                        color = theme.lcdInk,
                        topLeft = Offset(left + point.x * cell + 1f, top + point.y * cell + 1f),
                        size = Size((cell - 2f).coerceAtLeast(2f), (cell - 2f).coerceAtLeast(2f)),
                        cornerRadius = CornerRadius(2f),
                    )
                }
                drawCircle(
                    color = theme.lcdInk,
                    radius = (cell * 0.38f).coerceAtLeast(2f),
                    center = Offset(left + (gameState.food.x + 0.5f) * cell, top + (gameState.food.y + 0.5f) * cell),
                )
            }
            if (gameState.isGameOver || gameState.isPaused) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(theme.lcdInk)
                        .clickable { controller.toggleSnakePauseOrRestart() }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                ) {
                    LcdText(
                        text = if (gameState.isGameOver) "GAME OVER · TAP TO RETRY" else "PAUSED · TAP TO RESUME",
                        color = theme.lcdLit,
                        size = 12,
                        weight = FontWeight.Bold,
                    )
                }
            }
        }
        Spacer(Modifier.height(4.dp))
        LcdText(
            text = "SWIPE BOARD OR TAP 2 / 4 / 6 / 8 TO STEER",
            color = theme.lcdInk.copy(alpha = 0.75f),
            size = 9,
            weight = FontWeight.Bold,
            align = TextAlign.Center,
        )
    }
}

@Composable
private fun ColumnScope.TouchSettingsContent(
    state: LauncherUiState,
    controller: LauncherController,
) {
    val theme = state.theme
    LcdText("GOBLINS 1280 SETUP", theme.lcdInk, size = 14, weight = FontWeight.Black)
    Spacer(Modifier.height(8.dp))
    val actions: List<Pair<String, () -> Unit>> = listOf(
        "1. SET AS DEFAULT HOME LAUNCHER" to controller::openDefaultHomeDirect,
        "2. CYCLE LCD BACKLIGHT (${theme.displayName.uppercase()})" to controller::cycleThemeDirect,
        "3. CYCLE CARRIER BANNER (${state.systemStatus.carrierLabel})" to controller::cycleCarrierDirect,
        "4. MANAGE SPEED DIAL KEYS (2-9)" to { controller.onKeyPress(com.goblins.launcher1280.model.KeypadKey.Digit(5, "")) },
        "5. PLAY SNAKE XENZIA" to controller::openSnakeDirect,
        "6. RELOAD INSTALLED APPS (${state.allApps.size})" to controller::refreshApps,
    )
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        itemsIndexed(actions) { _, (label, action) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(theme.lcdInk.copy(alpha = 0.1f))
                    .border(1.dp, theme.lcdInk.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                    .clickable(onClick = action)
                    .padding(horizontal = 10.dp, vertical = 11.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                LcdText(label, theme.lcdInk, size = 13, weight = FontWeight.Bold, maxLines = 1)
            }
        }
    }
}

@Composable
private fun ColumnScope.TouchSpeedDialContent(
    state: LauncherUiState,
    controller: LauncherController,
) {
    val theme = state.theme
    LcdText("SPEED DIAL KEYS (HOLD 2-9)", theme.lcdInk, size = 13, weight = FontWeight.Black)
    Spacer(Modifier.height(6.dp))
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        itemsIndexed((2..9).toList()) { _, digit ->
            val pkg = state.speedDials[digit]
            val appName = state.allApps.firstOrNull { it.packageName == pkg }?.label ?: "TAP TO ASSIGN APP"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(theme.lcdInk.copy(alpha = 0.09f))
                    .border(1.dp, theme.lcdInk.copy(alpha = 0.25f), RoundedCornerShape(4.dp))
                    .clickable { controller.openDrawerWithQuery(assignDigit = digit) }
                    .padding(horizontal = 10.dp, vertical = 9.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                LcdText("KEY $digit  →  ${appName.uppercase()}", theme.lcdInk, size = 13, weight = FontWeight.Bold, maxLines = 1)
                LcdText("SET ›", theme.lcdInk, size = 11, weight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun BottomTouchActionStrip(
    state: LauncherUiState,
    controller: LauncherController,
) {
    val theme = state.theme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, theme.lcdInk.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LcdChip(
            text = if (state.screen == LauncherScreen.Home) "☰ APPS" else "⌂ HOME",
            inverted = state.screen != LauncherScreen.Home,
            theme = theme,
            onClick = {
                if (state.screen == LauncherScreen.Home) controller.openDrawerWithQuery("") else controller.goHome()
            },
        )
        LcdChip(
            text = "☎ DIAL",
            inverted = state.screen is LauncherScreen.Dialer,
            theme = theme,
            onClick = { controller.openDialerScreen() },
        )
        LcdChip(
            text = "🐍 SNAKE",
            inverted = state.screen is LauncherScreen.SnakeGame,
            theme = theme,
            onClick = controller::openSnakeDirect,
        )
        LcdChip(
            text = "⚙ SETUP",
            inverted = state.screen is LauncherScreen.SettingsMenu,
            theme = theme,
            onClick = controller::openSettingsDirect,
        )
    }
}

@Composable
private fun LcdChip(
    text: String,
    inverted: Boolean,
    theme: LcdTheme,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(3.dp))
            .background(if (inverted) theme.lcdInk else Color.Transparent)
            .border(1.dp, theme.lcdInk, RoundedCornerShape(3.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 7.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center,
    ) {
        LcdText(
            text = text,
            color = if (inverted) theme.lcdLit else theme.lcdInk,
            size = 10,
            weight = FontWeight.Bold,
            maxLines = 1,
        )
    }
}

@Composable
private fun SignalTower(bars: Int, theme: LcdTheme) {
    Column(
        modifier = Modifier
            .width(14.dp)
            .fillMaxHeight()
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        repeat(7) { index ->
            val isLit = index >= 7 - bars.coerceIn(0, 7)
            Box(
                Modifier
                    .weight(1f)
                    .padding(vertical = 2.dp)
                    .width((6 + (6 - index)).dp)
                    .clip(RoundedCornerShape(1.dp))
                    .background(if (isLit) theme.lcdInk else theme.lcdInk.copy(alpha = 0.16f)),
            )
        }
    }
}

@Composable
private fun BatteryTower(bars: Int, percent: Int, charging: Boolean, theme: LcdTheme) {
    Column(
        modifier = Modifier
            .width(14.dp)
            .fillMaxHeight()
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        if (charging) {
            LcdText("⚡", theme.lcdInk, size = 10, weight = FontWeight.Bold)
        }
        repeat(7) { index ->
            val isLit = index >= 7 - bars.coerceIn(0, 7)
            Box(
                Modifier
                    .weight(1f)
                    .padding(vertical = 2.dp)
                    .width(10.dp)
                    .clip(RoundedCornerShape(1.dp))
                    .background(if (isLit) theme.lcdInk else theme.lcdInk.copy(alpha = 0.16f)),
            )
        }
    }
}

@Composable
private fun LcdText(
    text: String,
    color: Color,
    size: Int,
    weight: FontWeight = FontWeight.Normal,
    modifier: Modifier = Modifier,
    align: TextAlign = TextAlign.Start,
    maxLines: Int = 2,
) {
    Text(
        text = text,
        modifier = modifier,
        color = color,
        fontSize = size.sp,
        fontFamily = FontFamily.Monospace,
        fontWeight = weight,
        textAlign = align,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
        lineHeight = (size + 2).sp,
    )
}
