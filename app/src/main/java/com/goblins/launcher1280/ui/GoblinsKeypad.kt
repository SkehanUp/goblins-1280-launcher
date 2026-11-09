package com.goblins.launcher1280.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.goblins.launcher1280.model.KeypadKey

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GoblinsKeypadAssembly(
    onKeyPress: (KeypadKey) -> Unit,
    onKeyLongPress: (KeypadKey) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(Brush.verticalGradient(listOf(Color(0xFF343735), Color(0xFF202321), Color(0xFF151716))))
            .border(1.dp, Color(0xFF505451), RoundedCornerShape(28.dp))
            .padding(horizontal = 15.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ControlDeck(onKeyPress, onKeyLongPress)
        Spacer(Modifier.height(17.dp))
        NumberPad(onKeyPress, onKeyLongPress, Modifier.fillMaxWidth().weight(1f))
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ControlDeck(onKeyPress: (KeypadKey) -> Unit, onKeyLongPress: (KeypadKey) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().height(151.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SoftAndCallColumn(
            softKey = KeypadKey.SoftLeft,
            callKey = KeypadKey.Call,
            onKeyPress = onKeyPress,
            onKeyLongPress = onKeyLongPress,
        )
        NavigationRing(onKeyPress, onKeyLongPress)
        SoftAndCallColumn(
            softKey = KeypadKey.SoftRight,
            callKey = KeypadKey.EndPower,
            onKeyPress = onKeyPress,
            onKeyLongPress = onKeyLongPress,
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SoftAndCallColumn(
    softKey: KeypadKey,
    callKey: KeypadKey,
    onKeyPress: (KeypadKey) -> Unit,
    onKeyLongPress: (KeypadKey) -> Unit,
) {
    Column(
        modifier = Modifier.width(54.dp).height(133.dp),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TactileKey(
            key = softKey,
            modifier = Modifier.size(width = 51.dp, height = 41.dp),
            lineOnly = true,
            symbol = "━",
            symbolSize = 19,
            onKeyPress = onKeyPress,
            onKeyLongPress = onKeyLongPress,
        )
        TactileKey(
            key = callKey,
            modifier = Modifier.size(width = 51.dp, height = 51.dp),
            symbol = "☎",
            symbolSize = 23,
            symbolRotation = if (callKey == KeypadKey.Call) -35f else 135f,
            accent = if (callKey == KeypadKey.Call) Color(0xFF72D58A) else Color(0xFFE66B64),
            detail = if (callKey == KeypadKey.Call) "CALL" else "END",
            onKeyPress = onKeyPress,
            onKeyLongPress = onKeyLongPress,
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun NavigationRing(onKeyPress: (KeypadKey) -> Unit, onKeyLongPress: (KeypadKey) -> Unit) {
    Box(
        modifier = Modifier
            .size(145.dp)
            .shadow(8.dp, CircleShape)
            .clip(CircleShape)
            .background(Brush.radialGradient(listOf(Color(0xFF727873), Color(0xFF343936), Color(0xFF171A18))))
            .border(2.dp, Color(0xFF929891), CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier
                .size(108.dp)
                .clip(CircleShape)
                .background(Brush.radialGradient(listOf(Color(0xFF242826), Color(0xFF4A504C), Color(0xFF202321))))
                .border(1.dp, Color(0xFF9A9F99), CircleShape),
        )
        TactileKey(
            KeypadKey.NavUp,
            label = "▲",
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 5.dp).size(39.dp, 38.dp),
            metal = true,
            onKeyPress = onKeyPress,
            onKeyLongPress = onKeyLongPress,
        )
        TactileKey(
            KeypadKey.NavDown,
            label = "▼",
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 5.dp).size(39.dp, 38.dp),
            metal = true,
            onKeyPress = onKeyPress,
            onKeyLongPress = onKeyLongPress,
        )
        TactileKey(
            KeypadKey.NavLeft,
            label = "◀",
            modifier = Modifier.align(Alignment.CenterStart).padding(start = 5.dp).size(38.dp, 39.dp),
            metal = true,
            onKeyPress = onKeyPress,
            onKeyLongPress = onKeyLongPress,
        )
        TactileKey(
            KeypadKey.NavRight,
            label = "▶",
            modifier = Modifier.align(Alignment.CenterEnd).padding(end = 5.dp).size(38.dp, 39.dp),
            metal = true,
            onKeyPress = onKeyPress,
            onKeyLongPress = onKeyLongPress,
        )
        TactileKey(
            key = KeypadKey.NavCenter,
            label = "OK",
            modifier = Modifier.size(44.dp),
            metal = true,
            detail = null,
            onKeyPress = onKeyPress,
            onKeyLongPress = onKeyLongPress,
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun NumberPad(
    onKeyPress: (KeypadKey) -> Unit,
    onKeyLongPress: (KeypadKey) -> Unit,
    modifier: Modifier = Modifier,
) {
    val rows = listOf(
        listOf(1 to "OO", 2 to "ABC", 3 to "DEF"),
        listOf(4 to "GHI", 5 to "JKL", 6 to "MNO"),
        listOf(7 to "PQRS", 8 to "TUV", 9 to "WXYZ"),
    )
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        rows.forEach { row ->
            Row(Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                row.forEach { (digit, letters) ->
                    TactileKey(
                        key = KeypadKey.Digit(digit, letters),
                        label = digit.toString(),
                        detail = letters,
                        modifier = Modifier.weight(1f).fillMaxSize(),
                        onKeyPress = onKeyPress,
                        onKeyLongPress = onKeyLongPress,
                    )
                }
            }
        }
        Row(Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            TactileKey(
                key = KeypadKey.Star,
                label = "*",
                detail = "LOCK",
                modifier = Modifier.weight(1f).fillMaxSize(),
                onKeyPress = onKeyPress,
                onKeyLongPress = onKeyLongPress,
            )
            TactileKey(
                key = KeypadKey.Digit(0, ""),
                label = "0",
                detail = "TORCH",
                modifier = Modifier.weight(1f).fillMaxSize(),
                onKeyPress = onKeyPress,
                onKeyLongPress = onKeyLongPress,
            )
            TactileKey(
                key = KeypadKey.Pound,
                label = "#",
                detail = "VIB",
                modifier = Modifier.weight(1f).fillMaxSize(),
                onKeyPress = onKeyPress,
                onKeyLongPress = onKeyLongPress,
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TactileKey(
    key: KeypadKey,
    modifier: Modifier = Modifier,
    label: String? = null,
    detail: String? = null,
    symbol: String? = null,
    symbolSize: Int = 20,
    symbolRotation: Float = 0f,
    accent: Color = Color(0xFFE5E8E4),
    metal: Boolean = false,
    lineOnly: Boolean = false,
    onKeyPress: (KeypadKey) -> Unit,
    onKeyLongPress: (KeypadKey) -> Unit,
) {
    val haptics = LocalHapticFeedback.current
    val shape = if (metal) CircleShape else RoundedCornerShape(50)
    val baseColors = if (metal) {
        listOf(Color(0xFFC5C9C4), Color(0xFF747B75), Color(0xFF3F4541))
    } else {
        listOf(Color(0xFF555A56), Color(0xFF292D2A), Color(0xFF171A18))
    }
    Column(
        modifier = modifier
            .shadow(if (metal) 2.dp else 4.dp, shape)
            .clip(shape)
            .background(Brush.verticalGradient(baseColors))
            .border(1.dp, if (metal) Color(0xFFB9BEB8) else Color(0xFF626762), shape)
            .combinedClickable(
                onClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onKeyPress(key)
                },
                onLongClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    onKeyLongPress(key)
                },
            )
            .padding(horizontal = 4.dp, vertical = 3.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        when {
            lineOnly -> Box(Modifier.width(22.dp).height(2.dp).background(accent, RoundedCornerShape(2.dp)))
            symbol != null -> Text(
                text = symbol,
                modifier = Modifier.graphicsLayer { rotationZ = symbolRotation },
                color = accent,
                fontSize = symbolSize.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = (symbolSize + 1).sp,
            )
            else -> Text(
                text = label.orEmpty(),
                color = if (metal) Color(0xFF161A17) else Color(0xFFF0F1EE),
                fontSize = if (metal) 12.sp else 22.sp,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 23.sp,
            )
        }
        detail?.let {
            Text(
                text = it,
                color = accent.copy(alpha = 0.86f),
                fontSize = 7.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.6.sp,
                textAlign = TextAlign.Center,
                maxLines = 1,
                lineHeight = 8.sp,
            )
        }
    }
}
