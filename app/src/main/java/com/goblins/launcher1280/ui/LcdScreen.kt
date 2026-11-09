package com.goblins.launcher1280.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.goblins.launcher1280.backend.RingerProfile
import com.goblins.launcher1280.model.LauncherScreen
import com.goblins.launcher1280.model.LauncherUiState
import com.goblins.launcher1280.model.LcdTheme
import com.goblins.launcher1280.model.MainMenuItem

@Composable
fun GoblinsLcdAssembly(state: LauncherUiState, modifier: Modifier = Modifier) {
    val chassis = Color(0xFF282A29)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(30.dp))
            .background(Brush.verticalGradient(listOf(Color(0xFF454744), chassis, Color(0xFF1E201F))))
            .border(1.dp, Color(0xFF555852), RoundedCornerShape(30.dp))
            .padding(horizontal = 18.dp, vertical = 13.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        SpeakerCameraDeck()
        Spacer(Modifier.height(5.dp))
        GoblinsBadge()
        Spacer(Modifier.height(9.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(19.dp))
                .background(Color(0xFF111411))
                .border(2.dp, state.theme.bezelRing, RoundedCornerShape(19.dp))
                .padding(7.dp),
        ) {
            LcdGlass(state, Modifier.fillMaxSize())
        }
    }
}

@Composable
private fun SpeakerCameraDeck() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(30.dp),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val centerX = size.width / 2f
            val centerY = size.height / 2f
            val slotColor = Color(0xFF111312)
            val slotWidth = size.width * 0.19f
            val gap = size.width * 0.095f
            for (row in -1..1) {
                val y = centerY + row * 4.dp.toPx()
                drawRoundRect(
                    color = slotColor,
                    topLeft = Offset(centerX - gap - slotWidth, y - 1.dp.toPx()),
                    size = Size(slotWidth, 2.dp.toPx()),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx()),
                )
                drawRoundRect(
                    color = slotColor,
                    topLeft = Offset(centerX + gap, y - 1.dp.toPx()),
                    size = Size(slotWidth, 2.dp.toPx()),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx()),
                )
            }
        }
        Box(
            modifier = Modifier
                .size(27.dp)
                .clip(CircleShape)
                .background(Brush.radialGradient(listOf(Color(0xFF555A56), Color(0xFF171A19))))
                .border(2.dp, Color(0xFF858983), CircleShape)
                .padding(4.dp)
                .clip(CircleShape)
                .background(Brush.radialGradient(listOf(Color(0xFF192B31), Color(0xFF050708))))
                .border(1.dp, Color(0xFF303A3C), CircleShape),
        )
    }
}

@Composable
private fun GoblinsBadge() {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(5.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFE2E3D8), Color(0xFF858981), Color(0xFF3A3E3A)),
                ),
            )
            .border(1.dp, Color(0xFFB7B9AF), RoundedCornerShape(5.dp))
            .padding(horizontal = 20.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "GOBLINS",
            color = Color(0xFF20231F),
            fontSize = 13.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 3.sp,
            fontFamily = FontFamily.Monospace,
        )
    }
}

@Composable
private fun LcdGlass(state: LauncherUiState, modifier: Modifier = Modifier) {
    val theme = state.theme
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(theme.lcdLit),
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val spacing = 5.dp.toPx()
            var y = 1.dp.toPx()
            while (y < size.height) {
                var x = 1.dp.toPx()
                while (x < size.width) {
                    drawCircle(
                        color = theme.lcdGhost.copy(alpha = 0.21f),
                        radius = 0.65.dp.toPx(),
                        center = Offset(x, y),
                    )
                    x += spacing
                }
                y += spacing
            }
            var scanY = 3.dp.toPx()
            while (scanY < size.height) {
                drawLine(
                    color = theme.lcdInk.copy(alpha = 0.035f),
                    start = Offset(0f, scanY),
                    end = Offset(size.width, scanY),
                    strokeWidth = 0.6.dp.toPx(),
                )
                scanY += 4.dp.toPx()
            }
        }

        Column(Modifier.fillMaxSize().padding(horizontal = 7.dp, vertical = 5.dp)) {
            StatusRow(state)
            Spacer(Modifier.height(2.dp))
            Row(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SignalTower(state.systemStatus.signalBars, theme)
                ScreenContent(state, Modifier.weight(1f).fillMaxHeight())
                BatteryTower(state.systemStatus.batteryBars, state.systemStatus.isCharging, theme)
            }
            SoftKeyRow(state)
        }

        state.bannerMessage?.let { message ->
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth(0.82f)
                    .clip(RoundedCornerShape(4.dp))
                    .background(theme.lcdInk)
                    .border(2.dp, theme.lcdLit, RoundedCornerShape(4.dp))
                    .padding(horizontal = 10.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center,
            ) {
                LcdText(
                    text = message,
                    color = theme.lcdLit,
                    size = 14,
                    weight = FontWeight.Bold,
                    align = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun StatusRow(state: LauncherUiState) {
    val theme = state.theme
    Row(
        modifier = Modifier.fillMaxWidth().height(21.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        LcdText(
            text = state.systemStatus.carrierLabel.uppercase(),
            color = theme.lcdInk,
            size = 9,
            weight = FontWeight.Bold,
            modifier = Modifier.weight(1f),
            maxLines = 1,
        )
        if (state.systemStatus.torchEnabled) StatusTag("[TORCH]", theme)
        StatusTag(
            text = when (state.systemStatus.ringerProfile) {
                RingerProfile.VIBRATE -> "[VIB]"
                RingerProfile.SILENT -> "[SILENT]"
                RingerProfile.NORMAL -> "[RING]"
            },
            theme = theme,
        )
        if (state.isKeypadLocked) StatusTag("[LOCK]", theme)
    }
}

@Composable
private fun StatusTag(text: String, theme: LcdTheme) {
    LcdText(text, theme.lcdInk, size = 7, weight = FontWeight.Bold, maxLines = 1)
}

@Composable
private fun SignalTower(bars: Int, theme: LcdTheme) {
    Column(
        modifier = Modifier.width(19.dp).fillMaxHeight().padding(vertical = 7.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        repeat(7) { index ->
            val isLit = index >= 7 - bars.coerceIn(0, 7)
            Box(
                Modifier
                    .padding(vertical = 1.dp)
                    .width((8 + index * 1.15f).dp)
                    .height(3.dp)
                    .clip(RoundedCornerShape(1.dp))
                    .background(if (isLit) theme.lcdInk else theme.lcdGhost.copy(alpha = 0.47f)),
            )
        }
        Canvas(Modifier.width(17.dp).height(17.dp).padding(top = 2.dp)) {
            val ink = theme.lcdInk
            drawLine(ink, Offset(size.width * 0.5f, size.height * 0.2f), Offset(size.width * 0.5f, size.height * 0.95f), 1.2.dp.toPx())
            drawLine(ink, Offset(size.width * 0.5f, size.height * 0.25f), Offset(size.width * 0.16f, size.height * 0.67f), 1.2.dp.toPx())
            drawLine(ink, Offset(size.width * 0.5f, size.height * 0.25f), Offset(size.width * 0.84f, size.height * 0.67f), 1.2.dp.toPx())
            drawLine(ink, Offset(size.width * 0.23f, size.height * 0.72f), Offset(size.width * 0.77f, size.height * 0.72f), 1.2.dp.toPx())
        }
    }
}

@Composable
private fun BatteryTower(bars: Int, charging: Boolean, theme: LcdTheme) {
    Column(
        modifier = Modifier.width(20.dp).fillMaxHeight().padding(vertical = 7.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        repeat(7) { index ->
            val isLit = index >= 7 - bars.coerceIn(0, 7)
            Box(
                Modifier
                    .padding(vertical = 1.dp)
                    .width(13.dp)
                    .height(3.dp)
                    .clip(RoundedCornerShape(1.dp))
                    .background(if (isLit) theme.lcdInk else theme.lcdGhost.copy(alpha = 0.47f)),
            )
        }
        if (charging) {
            LcdText("⚡", theme.lcdInk, size = 11, weight = FontWeight.Bold)
        } else {
            Spacer(Modifier.height(13.dp))
        }
    }
}

@Composable
private fun ScreenContent(state: LauncherUiState, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(horizontal = 2.dp, vertical = 1.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        when (val screen = state.screen) {
            LauncherScreen.Home -> HomeContent(state)
            is LauncherScreen.MainMenu -> MainMenuContent(screen, state.theme)
            is LauncherScreen.AppDrawer -> AppDrawerContent(screen, state)
            is LauncherScreen.AppOptions -> AppOptionsContent(screen, state.theme)
            is LauncherScreen.Dialer -> DialerContent(screen.digits, state.theme)
            is LauncherScreen.SnakeGame -> SnakeContent(screen.gameState, state.theme, Modifier.weight(1f).fillMaxWidth())
            is LauncherScreen.ThemePicker -> ThemePickerContent(screen.selectedIndex, state.theme)
            is LauncherScreen.SpeedDialManager -> SpeedDialContent(screen.selectedDigit, state)
            is LauncherScreen.SettingsMenu -> SettingsContent(screen.selectedIndex, state.theme)
        }
    }
}

@Composable
private fun ColumnScope.HomeContent(state: LauncherUiState) {
    val theme = state.theme
    Spacer(Modifier.weight(0.4f))
    LcdText(state.timeText, theme.lcdInk, size = 30, weight = FontWeight.Bold, align = TextAlign.Center)
    LcdText(state.dateText.uppercase(), theme.lcdInk, size = 10, weight = FontWeight.Bold)
    Spacer(Modifier.height(12.dp))
    LcdText("HOLD 0: TORCH | HOLD #: VIB", theme.lcdInk, size = 7, weight = FontWeight.Medium, align = TextAlign.Center)
    Spacer(Modifier.weight(0.6f))
}

@Composable
private fun ColumnScope.MainMenuContent(screen: LauncherScreen.MainMenu, theme: LcdTheme) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        LcdText("MENU 1-${MainMenuItem.entries.size}", theme.lcdInk, size = 10, weight = FontWeight.Bold, modifier = Modifier.weight(1f))
        LcdText(MainMenuItem.entries.getOrNull(screen.selectedIndex)?.code.orEmpty(), theme.lcdInk, size = 8, weight = FontWeight.Bold)
    }
    Column(Modifier.fillMaxWidth().weight(1f).verticalScroll(rememberScrollState())) {
        MainMenuItem.entries.forEachIndexed { index, item ->
            SelectableRow(
                text = "${item.index}. ${item.title}",
                selected = index == screen.selectedIndex,
                theme = theme,
                detail = item.subtitle,
            )
        }
    }
}

@Composable
private fun ColumnScope.AppDrawerContent(screen: LauncherScreen.AppDrawer, state: LauncherUiState) {
    val theme = state.theme
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        val header = screen.assignDigit?.let { "ASSIGN KEY $it" } ?: "APPS (${state.filteredApps.size})"
        LcdText(header, theme.lcdInk, size = 10, weight = FontWeight.Bold, modifier = Modifier.weight(1f), maxLines = 1)
    }
    if (screen.t9Query.isNotEmpty()) {
        LcdText("T9: ${screen.t9Query}_", theme.lcdInk, size = 9, weight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
    }

    val apps = state.filteredApps
    val firstVisible = (screen.selectedIndex - 2).coerceIn(0, (apps.size - 5).coerceAtLeast(0))
    val visibleApps = apps.drop(firstVisible).take(5)
    Column(Modifier.fillMaxWidth().weight(1f)) {
        visibleApps.forEachIndexed { index, app ->
            val actualIndex = firstVisible + index
            val pinned = app.packageName in state.pinnedPackages
            SelectableRow(
                text = "${if (pinned) "*" else " "} ${app.label.take(14)}",
                selected = actualIndex == screen.selectedIndex,
                theme = theme,
            )
        }
        if (visibleApps.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                LcdText("NO APPS FOUND", theme.lcdInk, size = 9, weight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun ColumnScope.AppOptionsContent(screen: LauncherScreen.AppOptions, theme: LcdTheme) {
    LcdText("OPTIONS: ${screen.app.label.take(12).uppercase()}", theme.lcdInk, size = 9, weight = FontWeight.Bold, maxLines = 1)
    val options = listOf("1. OPEN APP", "2. PIN / UNPIN TOP", "3. SPEED DIALS", "4. APP INFO")
    Column(Modifier.fillMaxWidth().weight(1f)) {
        options.forEachIndexed { index, option ->
            SelectableRow(option, index == screen.selectedOptionIndex, theme)
        }
    }
}

@Composable
private fun ColumnScope.DialerContent(digits: String, theme: LcdTheme) {
    Spacer(Modifier.weight(0.7f))
    LcdText("DIAL NUMBER", theme.lcdInk, size = 10, weight = FontWeight.Bold)
    Spacer(Modifier.height(14.dp))
    LcdText(digits.ifEmpty { "_" }, theme.lcdInk, size = 22, weight = FontWeight.Bold, align = TextAlign.Center, maxLines = 1)
    Spacer(Modifier.weight(1f))
}

@Composable
private fun ColumnScope.SnakeContent(gameState: com.goblins.launcher1280.analysis.SnakeState, theme: LcdTheme, modifier: Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        LcdText("SCORE: ${gameState.score}  HI: ${gameState.highScore}", theme.lcdInk, size = 9, weight = FontWeight.Bold, maxLines = 1)
        Box(Modifier.fillMaxSize().padding(vertical = 3.dp), contentAlignment = Alignment.Center) {
            Canvas(Modifier.fillMaxSize()) {
                val cellWidth = size.width / gameState.width.coerceAtLeast(1)
                val cellHeight = size.height / gameState.height.coerceAtLeast(1)
                val cell = minOf(cellWidth, cellHeight)
                val boardWidth = cell * gameState.width
                val boardHeight = cell * gameState.height
                val left = (size.width - boardWidth) / 2f
                val top = (size.height - boardHeight) / 2f
                drawRect(
                    color = theme.lcdInk.copy(alpha = 0.16f),
                    topLeft = Offset(left, top),
                    size = Size(boardWidth, boardHeight),
                )
                drawRect(
                    color = theme.lcdInk,
                    topLeft = Offset(left, top),
                    size = Size(boardWidth, boardHeight),
                    style = Stroke(width = 1.2.dp.toPx()),
                )
                for (x in 1 until gameState.width) {
                    val xPosition = left + x * cell
                    drawLine(theme.lcdInk.copy(alpha = 0.18f), Offset(xPosition, top), Offset(xPosition, top + boardHeight), 0.45.dp.toPx())
                }
                for (y in 1 until gameState.height) {
                    val yPosition = top + y * cell
                    drawLine(theme.lcdInk.copy(alpha = 0.18f), Offset(left, yPosition), Offset(left + boardWidth, yPosition), 0.45.dp.toPx())
                }
                gameState.snake.forEach { point ->
                    drawRoundRect(
                        color = theme.lcdInk,
                        topLeft = Offset(left + point.x * cell + 0.6.dp.toPx(), top + point.y * cell + 0.6.dp.toPx()),
                        size = Size((cell - 1.2.dp.toPx()).coerceAtLeast(1f), (cell - 1.2.dp.toPx()).coerceAtLeast(1f)),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(1.dp.toPx()),
                    )
                }
                drawCircle(
                    color = theme.lcdInk,
                    radius = (cell * 0.33f).coerceAtLeast(1f),
                    center = Offset(left + (gameState.food.x + 0.5f) * cell, top + (gameState.food.y + 0.5f) * cell),
                )
            }
            if (gameState.isGameOver || gameState.isPaused) {
                Box(
                    Modifier
                        .clip(RoundedCornerShape(2.dp))
                        .background(theme.lcdInk)
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                ) {
                    LcdText(if (gameState.isGameOver) "GAME OVER" else "PAUSED", theme.lcdLit, size = 10, weight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun ColumnScope.ThemePickerContent(selectedIndex: Int, theme: LcdTheme) {
    LcdText("LCD DISPLAY", theme.lcdInk, size = 10, weight = FontWeight.Bold)
    Column(Modifier.fillMaxWidth().weight(1f).verticalScroll(rememberScrollState())) {
        LcdTheme.entries.forEachIndexed { index, entry ->
            SelectableRow(entry.displayName.uppercase(), index == selectedIndex, theme)
        }
    }
}

@Composable
private fun ColumnScope.SpeedDialContent(selectedDigit: Int, state: LauncherUiState) {
    LcdText("SPEED DIALS", state.theme.lcdInk, size = 10, weight = FontWeight.Bold)
    Column(Modifier.fillMaxWidth().weight(1f).verticalScroll(rememberScrollState())) {
        (2..9).forEach { digit ->
            val assignedPackage = state.speedDials[digit]
            val appLabel = state.allApps.firstOrNull { it.packageName == assignedPackage }?.label ?: "-- EMPTY --"
            SelectableRow("$digit. ${appLabel.take(12)}", digit == selectedDigit, state.theme)
        }
    }
}

@Composable
private fun ColumnScope.SettingsContent(selectedIndex: Int, theme: LcdTheme) {
    LcdText("PHONE SETUP", theme.lcdInk, size = 10, weight = FontWeight.Bold)
    val settings = listOf("1. SET DEFAULT HOME", "2. RELOAD APPS", "3. CHANGE BANNER", "4. LOCK KEYPAD")
    Column(Modifier.fillMaxWidth().weight(1f)) {
        settings.forEachIndexed { index, setting ->
            SelectableRow(setting, index == selectedIndex, theme)
        }
    }
}

@Composable
private fun SelectableRow(text: String, selected: Boolean, theme: LcdTheme, detail: String? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(2.dp))
            .background(if (selected) theme.lcdInk else Color.Transparent)
            .padding(horizontal = 3.dp, vertical = if (detail == null) 3.dp else 1.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            LcdText(
                text = text,
                color = if (selected) theme.lcdLit else theme.lcdInk,
                size = if (detail == null) 9 else 8,
                weight = if (selected) FontWeight.Bold else FontWeight.Medium,
                maxLines = 1,
            )
            detail?.let {
                LcdText(it, if (selected) theme.lcdLit.copy(alpha = 0.8f) else theme.lcdInk.copy(alpha = 0.75f), size = 6, maxLines = 1)
            }
        }
    }
}

@Composable
private fun SoftKeyRow(state: LauncherUiState) {
    Row(
        modifier = Modifier.fillMaxWidth().height(21.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        LcdText(state.leftSoftKeyLabel.uppercase(), state.theme.lcdInk, size = 9, weight = FontWeight.Bold)
        LcdText(state.rightSoftKeyLabel.uppercase(), state.theme.lcdInk, size = 9, weight = FontWeight.Bold)
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
