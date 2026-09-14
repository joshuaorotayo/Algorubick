package com.jorotayo.algorubickrevamped.ui.timer

import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jorotayo.algorubickrevamped.R
import com.jorotayo.algorubickrevamped.ui.theme.AlgorubickTheme
import com.jorotayo.algorubickrevamped.ui.theme.ColorRed
import com.jorotayo.algorubickrevamped.ui.theme.DefaultPreviews

@Composable
fun TimerScreen(viewModel: TimerViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val activity = context as? ComponentActivity

    LaunchedEffect(uiState.toastMessage) {
        uiState.toastMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.onToastShown()
        }
    }

    DisposableEffect(uiState.running) {
        val window = activity?.window
        if (uiState.running) {
            window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        onDispose {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    TimerContent(
        uiState = uiState,
        onToggleTimer = viewModel::toggleTimer,
        onSelectCubeSize = viewModel::selectCubeSize,
        onDeleteCubeSize = viewModel::requestDeleteCubeSize,
        onScrambleClick = viewModel::onScrambleClick,
        onSave = viewModel::saveSolve,
        onDeleteSolve = viewModel::requestDeleteSolve,
        onDnf = viewModel::requestDnf,
        onPlus2 = viewModel::requestPlus2,
        onConfirmDeleteSolve = viewModel::confirmDeleteSolve,
        onDismissDeleteSolve = viewModel::dismissDeleteSolve,
        onConfirmDnf = viewModel::confirmDnf,
        onDismissDnf = viewModel::dismissDnf,
        onConfirmPlus2 = viewModel::confirmPlus2,
        onDismissPlus2 = viewModel::dismissPlus2,
        onConfirmDeleteCubeSize = viewModel::confirmDeleteCubeSize,
        onDismissDeleteCubeSize = viewModel::dismissDeleteCubeSize,
        onAddCubeSize = viewModel::addCubeSize,
        onDismissAddCubeSize = viewModel::dismissAddCubeSize,
    )
}

@Composable
private fun TimerContent(
    uiState: TimerUiState,
    onToggleTimer: () -> Unit = {},
    onSelectCubeSize: (Int) -> Unit = {},
    onDeleteCubeSize: (Int) -> Unit = {},
    onScrambleClick: () -> Unit = {},
    onSave: () -> Unit = {},
    onDeleteSolve: () -> Unit = {},
    onDnf: () -> Unit = {},
    onPlus2: () -> Unit = {},
    onConfirmDeleteSolve: () -> Unit = {},
    onDismissDeleteSolve: () -> Unit = {},
    onConfirmDnf: () -> Unit = {},
    onDismissDnf: () -> Unit = {},
    onConfirmPlus2: () -> Unit = {},
    onDismissPlus2: () -> Unit = {},
    onConfirmDeleteCubeSize: () -> Unit = {},
    onDismissDeleteCubeSize: () -> Unit = {},
    onAddCubeSize: (String) -> Unit = {},
    onDismissAddCubeSize: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        if (!uiState.overlayHidden) {
            CubeSizeBar(
                cubeSizes = uiState.cubeSizes,
                selectedIndex = uiState.selectedCubeSizeIndex,
                onSelect = onSelectCubeSize,
                onDelete = onDeleteCubeSize,
            )
            ScrambleSection(
                scramble = uiState.scramble,
                onScrambleClick = onScrambleClick,
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = onToggleTimer,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = uiState.timeDisplay,
                    fontSize = if (uiState.running) 64.sp else 52.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = if (uiState.running) {
                        Color(0xFF22C55E)
                    } else {
                        MaterialTheme.colorScheme.onBackground
                    },
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                )
                if (uiState.running) {
                    Text(
                        text = stringResource(R.string.timer_label_tap_to_stop),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 12.dp),
                    )
                }
            }
        }

        if (!uiState.overlayHidden) {
            if (uiState.commandBarVisible) {
                CommandBar(
                    dnfVisible = uiState.dnfButtonVisible,
                    plus2Visible = uiState.plus2ButtonVisible,
                    deleteVisible = uiState.deleteButtonVisible,
                    onSave = onSave,
                    onDelete = onDeleteSolve,
                    onDnf = onDnf,
                    onPlus2 = onPlus2,
                )
            }
            StatisticsSection(stats = uiState.stats)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }

    if (uiState.showDeleteConfirm) {
        ConfirmDialog(
            title = stringResource(R.string.timer_dialog_delete_solve_title),
            message = stringResource(R.string.timer_dialog_delete_solve_message),
            onConfirm = onConfirmDeleteSolve,
            onDismiss = onDismissDeleteSolve,
        )
    }
    if (uiState.showDnfConfirm) {
        ConfirmDialog(
            title = stringResource(R.string.timer_dialog_dnf_title),
            message = stringResource(R.string.timer_dialog_dnf_message),
            onConfirm = onConfirmDnf,
            onDismiss = onDismissDnf,
        )
    }
    if (uiState.showPlus2Confirm) {
        ConfirmDialog(
            title = stringResource(R.string.timer_dialog_plus2_title),
            message = stringResource(R.string.timer_dialog_plus2_message),
            onConfirm = onConfirmPlus2,
            onDismiss = onDismissPlus2,
        )
    }
    if (uiState.showDeleteCubeSizeConfirm) {
        val index = uiState.pendingDeleteCubeSizeIndex
        val name = uiState.cubeSizes.getOrNull(index).orEmpty()
        ConfirmDialog(
            title = stringResource(R.string.timer_dialog_delete_cube_size_title),
            message = stringResource(R.string.timer_dialog_delete_cube_size_message, name),
            onConfirm = onConfirmDeleteCubeSize,
            onDismiss = onDismissDeleteCubeSize,
        )
    }
    if (uiState.showAddCubeSize) {
        AddCubeSizeDialog(
            onConfirm = onAddCubeSize,
            onDismiss = onDismissAddCubeSize,
        )
    }
}

@Composable
private fun CubeSizeBar(
    cubeSizes: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    onDelete: (Int) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedLabel = cubeSizes.getOrNull(selectedIndex).orEmpty()
    val addCubeSizeLabel = stringResource(R.string.timer_add_cube_size_option)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
        shadowElevation = 1.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(R.string.timer_label_select_cube_size),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
            )
            Box {
                TextButton(onClick = { expanded = true }) {
                    Text(selectedLabel.ifEmpty { stringResource(R.string.common_em_dash) })
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                    )
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    cubeSizes.forEachIndexed { index, size ->
                        val canDelete = size != addCubeSizeLabel && cubeSizes.size > 2
                        DropdownMenuItem(
                            text = { Text(text = size) },
                            onClick = {
                                expanded = false
                                onSelect(index)
                            },
                            trailingIcon = if (canDelete) {
                                {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = stringResource(R.string.timer_cd_cube_size_delete),
                                        tint = ColorRed,
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clickable {
                                                expanded = false
                                                onDelete(index)
                                            },
                                    )
                                }
                            } else {
                                null
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ScrambleSection(
    scramble: String,
    onScrambleClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
    ) {
        Text(
            text = stringResource(R.string.timer_scramble_header),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
        )
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onScrambleClick),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceContainerLow,
        ) {
            Text(
                text = scramble,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            )
        }
        Text(
            text = stringResource(R.string.timer_label_hint),
            style = MaterialTheme.typography.bodySmall,
            color = ColorRed,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp, bottom = 2.dp),
        )
    }
}

@Composable
private fun CommandBar(
    dnfVisible: Boolean,
    plus2Visible: Boolean,
    deleteVisible: Boolean,
    onSave: () -> Unit,
    onDelete: () -> Unit,
    onDnf: () -> Unit,
    onPlus2: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CircleActionButton(
            drawableRes = R.drawable.icons8_save_52_w_blue_circle,
            contentDescription = stringResource(R.string.timer_cd_save_solve),
            onClick = onSave,
        )
        if (deleteVisible) {
            CircleActionButton(
                drawableRes = R.drawable.icons8_delete_bin_48_w_blue_circle,
                contentDescription = stringResource(R.string.timer_cd_delete_solve),
                onClick = onDelete,
            )
        }
        if (dnfVisible) {
            CircleActionButton(
                drawableRes = R.drawable.dnf_48_w_filled,
                contentDescription = stringResource(R.string.timer_cd_dnf_solve),
                onClick = onDnf,
            )
        }
        if (plus2Visible) {
            CircleActionButton(
                drawableRes = R.drawable.icons8_plus_64_w_blue_circle,
                contentDescription = stringResource(R.string.timer_cd_plus2),
                onClick = onPlus2,
            )
        }
    }
}

@Composable
private fun CircleActionButton(
    drawableRes: Int,
    contentDescription: String,
    onClick: () -> Unit,
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(56.dp),
    ) {
        Icon(
            painter = painterResource(drawableRes),
            contentDescription = contentDescription,
            tint = Color.Unspecified,
            modifier = Modifier.size(44.dp),
        )
    }
}

@Composable
private fun StatisticsSection(stats: TimerStats) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            StatColumn(
                rows = listOf(
                    stringResource(R.string.timer_stat_best) to stats.best,
                    stringResource(R.string.timer_stat_worst) to stats.worst,
                    stringResource(R.string.timer_stat_mean) to stats.mean,
                    stringResource(R.string.timer_stat_count) to stats.count,
                ),
                modifier = Modifier.padding(end = 16.dp),
            )
            StatColumn(
                rows = listOf(
                    stringResource(R.string.timer_stat_avg5) to stats.avg5,
                    stringResource(R.string.timer_stat_avg12) to stats.avg12,
                    stringResource(R.string.timer_stat_avg50) to stats.avg50,
                    stringResource(R.string.timer_stat_avg100) to stats.avg100,
                ),
            )
        }
    }
}

@Composable
private fun StatColumn(
    rows: List<Pair<String, String>>,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        rows.forEach { (label, value) ->
            Row(
                modifier = Modifier.padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.width(100.dp),
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}

@Composable
private fun ConfirmDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text(stringResource(R.string.common_action_yes)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.common_action_no)) }
        },
    )
}

@Composable
private fun AddCubeSizeDialog(
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var text by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.timer_dialog_add_cube_size_title)) },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { if (it.length <= 30) text = it },
                label = { Text(stringResource(R.string.timer_dialog_add_cube_size_label)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(text) },
                enabled = text.isNotBlank(),
            ) {
                Text(stringResource(R.string.timer_dialog_add_cube_size_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.common_action_cancel))
            }
        },
    )
}

@DefaultPreviews
@Composable
private fun TimerScreenIdlePreview() {
    AlgorubickTheme {
        TimerContent(
            uiState = TimerUiState(
                running = false,
                timeDisplay = "00:12.54",
                scramble = "R U R' U' F2 L D2 B' U2",
                cubeSizes = listOf("3x3", "2x2", "Add Cube Size +"),
                selectedCubeSizeIndex = 0,
                commandBarVisible = true,
                overlayHidden = false,
                stats = TimerStats(
                    count = "12 Solves",
                    best = "00:08.12",
                    worst = "00:24.90",
                    mean = "00:14.33",
                    avg5 = "00:13.10",
                    avg12 = "00:14.33",
                    avg50 = "NA",
                    avg100 = "NA",
                ),
            ),
        )
    }
}

@DefaultPreviews
@Composable
private fun TimerScreenRunningPreview() {
    AlgorubickTheme {
        TimerContent(
            uiState = TimerUiState(
                running = true,
                timeDisplay = "00:08.37",
                scramble = "R U R' U' F2 L D2 B' U2",
                cubeSizes = listOf("3x3", "2x2", "Add Cube Size +"),
                selectedCubeSizeIndex = 0,
                commandBarVisible = false,
                overlayHidden = true,
            ),
        )
    }
}
