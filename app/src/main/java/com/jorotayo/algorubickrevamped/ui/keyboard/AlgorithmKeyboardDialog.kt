package com.jorotayo.algorubickrevamped.ui.keyboard

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.jorotayo.algorubickrevamped.ui.theme.AlgorubickTheme
import com.jorotayo.algorubickrevamped.ui.theme.DefaultPreviews
import com.jorotayo.algorubickrevamped.ui.theme.Primary

/**
 * Bottom-sheet wrapper for [AlgorithmKeyboard], matching the gravity-bottom Java KeyboardDialog.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlgorithmKeyboardDialog(
    algorithm: String,
    onAlgorithmChange: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Primary,
        contentColor = Color.White,
        shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp),
        dragHandle = null,
    ) {
        AlgorithmKeyboard(
            algorithm = algorithm,
            onAlgorithmChange = onAlgorithmChange,
            onClose = onDismiss,
            showCloseButton = true,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@DefaultPreviews
@Composable
private fun AlgorithmKeyboardDialogPreview() {
    AlgorubickTheme {
        AlgorithmKeyboard(
            algorithm = "R,U,R'",
            onAlgorithmChange = {},
            showCloseButton = true,
            onClose = {},
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
