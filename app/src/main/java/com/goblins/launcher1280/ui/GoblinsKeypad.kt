package com.goblins.launcher1280.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.goblins.launcher1280.model.KeypadKey
import com.goblins.launcher1280.model.LcdTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GoblinsKeypadAssembly(
    theme: LcdTheme,
    onKeyPress: (KeypadKey) -> Unit,
    onKeyLongPress: (KeypadKey) -> Unit,
    modifier: Modifier = Modifier,
) {
    val rows = listOf(
        listOf(
            Triple(KeypadKey.Digit(1, "APPS"), "1", "APPS"),
            Triple(KeypadKey.Digit(2, "ABC"), "2", "ABC"),
            Triple(KeypadKey.Digit(3, "DEF"), "3", "DEF"),
        ),
        listOf(
            Triple(KeypadKey.Digit(4, "GHI"), "4", "GHI"),
            Triple(KeypadKey.Digit(5, "JKL"), "5", "JKL"),
            Triple(KeypadKey.Digit(6, "MNO"), "6", "MNO"),
        ),
        listOf(
            Triple(KeypadKey.Digit(7, "PQRS"), "7", "PQRS"),
            Triple(KeypadKey.Digit(8, "TUV"), "8", "TUV"),
            Triple(KeypadKey.Digit(9, "WXYZ"), "9", "WXYZ"),
        ),
        listOf(
            Triple(KeypadKey.Star, "⌫", "DEL / CLR"),
            Triple(KeypadKey.Digit(0, "TORCH"), "0", "HOLD TORCH"),
            Triple(KeypadKey.Pound, "↵", "GO / VIB"),
        ),
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFF141816))
            .border(1.dp, theme.bezelRing.copy(alpha = 0.7f), RoundedCornerShape(18.dp))
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        rows.forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                row.forEach { (key, label, sub) ->
                    MinimalT9Pill(
                        key = key,
                        label = label,
                        sublabel = sub,
                        accentColor = theme.lcdLit,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize(),
                        onKeyPress = onKeyPress,
                        onKeyLongPress = onKeyLongPress,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MinimalT9Pill(
    key: KeypadKey,
    label: String,
    sublabel: String,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onKeyPress: (KeypadKey) -> Unit,
    onKeyLongPress: (KeypadKey) -> Unit,
) {
    val haptics = LocalHapticFeedback.current
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF262B28), Color(0xFF1A1E1C)),
                ),
            )
            .border(1.dp, Color(0xFF3A413C), RoundedCornerShape(10.dp))
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
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            color = Color(0xFFF1F4EF),
            fontSize = 18.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Start,
        )
        Text(
            text = sublabel,
            color = accentColor.copy(alpha = 0.85f),
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End,
            maxLines = 1,
        )
    }
}
