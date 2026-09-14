package com.jorotayo.algorubickrevamped.ui.keyboard

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jorotayo.algorubickrevamped.R
import com.jorotayo.algorubickrevamped.ui.theme.Accent
import com.jorotayo.algorubickrevamped.ui.theme.AlgorubickTheme
import com.jorotayo.algorubickrevamped.ui.theme.ColorRed
import com.jorotayo.algorubickrevamped.ui.theme.DefaultPreviews
import com.jorotayo.algorubickrevamped.ui.theme.Primary
import com.jorotayo.algorubickrevamped.ui.theme.White

private val MoveSplitRegex =
    Regex("(?=([A-z][2]?[{'}]?)|([A-z][2]?)|([A-z][{'}]?)|([A-z]))")

/**
 * Formats algorithm input with the same comma-separated regex logic as the Java keyboard `enter()`.
 */
fun formatAlgorithmEnter(current: String, keyPressed: String): String {
    if (current.isEmpty()) return keyPressed
    val wholeAlgorithm = current.replace(",", "") + keyPressed
    val parts = wholeAlgorithm.split(MoveSplitRegex)
    val joined = parts.joinToString(",") { it }
    return joined.replace(",,", "").removePrefix(",")
}

fun backspaceAlgorithm(current: String): String {
    if (current.isEmpty()) return ""
    return if (current.contains(",")) {
        current.substring(0, current.lastIndexOf(','))
    } else {
        ""
    }
}

@Composable
fun AlgorithmKeyboard(
    algorithm: String,
    onAlgorithmChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    onClose: (() -> Unit)? = null,
    showCloseButton: Boolean = onClose != null,
) {
    var modified by remember { mutableStateOf(true) }
    var twoLayer by remember { mutableStateOf(false) }
    val modifiersEnabled = !modified

    fun pressBase(key: String) {
        modified = false
        onAlgorithmChange(formatAlgorithmEnter(algorithm, key))
    }

    fun pressModifier(key: String) {
        if (!modifiersEnabled) return
        modified = true
        onAlgorithmChange(formatAlgorithmEnter(algorithm, key))
    }

    fun pressBackspace() {
        modified = true
        onAlgorithmChange(backspaceAlgorithm(algorithm))
    }

    fun clearAll() {
        modified = true
        onAlgorithmChange("")
    }

    val faces = if (twoLayer) {
        listOf(
            listOf("r", "l"),
            listOf("u", "d"),
            listOf("f", "b"),
        )
    } else {
        listOf(
            listOf("R", "L"),
            listOf("U", "D"),
            listOf("F", "B"),
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp)
            .background(Primary)
            .padding(bottom = 8.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.keyboard_current_alg_hint),
                color = White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(0.8f),
            )
            Text(
                text = algorithm.ifEmpty { stringResource(R.string.keyboard_current_alg_placeholder) },
                color = Accent,
                fontSize = 17.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1.7f),
            )
            if (showCloseButton && onClose != null) {
                TextButton(onClick = onClose, modifier = Modifier.weight(0.5f)) {
                    Text(
                        text = stringResource(R.string.keyboard_close_button),
                        color = White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 6.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            // Row 1: empty | faces | E X '
            KeyboardRow(modifier = Modifier.weight(1f)) {
                Spacer(modifier = Modifier.weight(1f))
                KeyboardKey(label = faces[0][0], onClick = { pressBase(faces[0][0]) }, modifier = Modifier.weight(1f))
                KeyboardKey(label = faces[0][1], onClick = { pressBase(faces[0][1]) }, modifier = Modifier.weight(1f))
                KeyboardKey(label = "E", onClick = { pressBase("E") }, modifier = Modifier.weight(1f))
                KeyboardKey(label = "X", onClick = { pressBase("X") }, modifier = Modifier.weight(1f))
                KeyboardKey(
                    label = stringResource(R.string.keyboard_btn_prime),
                    onClick = { pressModifier("'") },
                    modifier = Modifier.weight(1f),
                    enabled = modifiersEnabled,
                    textColor = if (modifiersEnabled) White else Primary,
                )
            }

            // Row 2: W | faces | S Y 2
            KeyboardRow(modifier = Modifier.weight(1f)) {
                KeyboardKey(
                    label = stringResource(R.string.keyboard_btn_w),
                    onClick = { pressBase("W") },
                    modifier = Modifier.weight(1f),
                )
                KeyboardKey(label = faces[1][0], onClick = { pressBase(faces[1][0]) }, modifier = Modifier.weight(1f))
                KeyboardKey(label = faces[1][1], onClick = { pressBase(faces[1][1]) }, modifier = Modifier.weight(1f))
                KeyboardKey(label = "S", onClick = { pressBase("S") }, modifier = Modifier.weight(1f))
                KeyboardKey(label = "Y", onClick = { pressBase("Y") }, modifier = Modifier.weight(1f))
                KeyboardKey(
                    label = stringResource(R.string.keyboard_btn_2),
                    onClick = { pressModifier("2") },
                    modifier = Modifier.weight(1f),
                    enabled = modifiersEnabled,
                    textColor = if (modifiersEnabled) White else Primary,
                )
            }

            // Row 3: Shift | faces | M Z ⌫
            KeyboardRow(modifier = Modifier.weight(1f)) {
                KeyboardKey(
                    label = stringResource(R.string.keyboard_btn_shift),
                    onClick = { twoLayer = !twoLayer },
                    modifier = Modifier.weight(1f),
                    textColor = if (twoLayer) ColorRed else Accent,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )
                KeyboardKey(label = faces[2][0], onClick = { pressBase(faces[2][0]) }, modifier = Modifier.weight(1f))
                KeyboardKey(label = faces[2][1], onClick = { pressBase(faces[2][1]) }, modifier = Modifier.weight(1f))
                KeyboardKey(label = "M", onClick = { pressBase("M") }, modifier = Modifier.weight(1f))
                KeyboardKey(label = "Z", onClick = { pressBase("Z") }, modifier = Modifier.weight(1f))
                KeyboardKey(
                    label = stringResource(R.string.keyboard_btn_backspace),
                    onClick = { pressBackspace() },
                    modifier = Modifier.weight(1f),
                    textColor = Accent,
                    onLongClick = { clearAll() },
                )
            }
        }
    }
}

@Composable
private fun KeyboardRow(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        content = content,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun KeyboardKey(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    textColor: Color = White,
    fontSize: TextUnit = 22.sp,
    fontWeight: FontWeight = FontWeight.Medium,
    onLongClick: (() -> Unit)? = null,
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .border(1.dp, White.copy(alpha = 0.85f), RoundedCornerShape(8.dp))
            .background(Primary.copy(alpha = 0.9f), RoundedCornerShape(8.dp))
            .combinedClickable(
                enabled = enabled,
                onClick = onClick,
                onLongClick = onLongClick,
            )
            .padding(horizontal = 2.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = fontSize,
            fontWeight = fontWeight,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@DefaultPreviews
@Composable
private fun AlgorithmKeyboardPreview() {
    AlgorubickTheme {
        AlgorithmKeyboard(
            algorithm = "R,U,R'",
            onAlgorithmChange = {},
        )
    }
}
