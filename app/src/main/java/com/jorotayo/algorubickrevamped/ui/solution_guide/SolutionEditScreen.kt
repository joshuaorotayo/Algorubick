package com.jorotayo.algorubickrevamped.ui.solution_guide

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.github.dhaval2404.imagepicker.ImagePicker
import com.jorotayo.algorubickrevamped.R
import com.jorotayo.algorubickrevamped.ui.keyboard.AlgorithmKeyboardDialog
import com.jorotayo.algorubickrevamped.ui.theme.Accent
import com.jorotayo.algorubickrevamped.ui.theme.AlgorubickTheme
import com.jorotayo.algorubickrevamped.ui.theme.DefaultPreviews
import com.jorotayo.algorubickrevamped.ui.theme.PreviewSamples
import com.jorotayo.algorubickrevamped.ui.theme.Primary
import com.jorotayo.algorubickrevamped.ui.theme.White
import com.jorotayo.algorubickrevamped.utils.rememberIconModel

private sealed class ImagePickTarget {
    data object SolutionIcon : ImagePickTarget()
    data class StepStart(val localId: Long) : ImagePickTarget()
    data class StepEnd(val localId: Long) : ImagePickTarget()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolutionEditScreen(
    solutionId: Long?,
    onSaved: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: SolutionEditViewModel = viewModel(
        key = "edit-${solutionId ?: "new"}",
        factory = SolutionEditViewModel.factory(solutionId),
    ),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val activity = context as Activity
    var pendingPick by remember { mutableStateOf<ImagePickTarget?>(null) }
    val scrollState = rememberScrollState()

    val imagePickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        val target = pendingPick
        pendingPick = null
        if (result.resultCode != Activity.RESULT_OK) return@rememberLauncherForActivityResult
        val uri = result.data?.data?.toString().orEmpty()
        if (uri.isEmpty()) return@rememberLauncherForActivityResult
        when (target) {
            is ImagePickTarget.SolutionIcon -> viewModel.updateIcon(uri)
            is ImagePickTarget.StepStart -> viewModel.updateStepStartImage(target.localId, uri)
            is ImagePickTarget.StepEnd -> viewModel.updateStepEndImage(target.localId, uri)
            null -> Unit
        }
    }

    fun launchImagePicker(target: ImagePickTarget) {
        pendingPick = target
        ImagePicker.with(activity)
            .crop()
            .compress(1024)
            .galleryMimeTypes(arrayOf("image/png", "image/jpg", "image/jpeg"))
            .createIntent { intent ->
                imagePickerLauncher.launch(intent)
            }
    }

    BackHandler {
        viewModel.showDiscardDialog(true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (uiState.isEdit) stringResource(R.string.solutionEdit_title_edit) else stringResource(R.string.solutionEdit_title_create))
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.showDiscardDialog(true) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Primary,
                    titleContentColor = White,
                    navigationIconContentColor = White,
                ),
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(156.dp)
                    .background(Primary),
                contentAlignment = Alignment.TopCenter,
            ) {
                SolutionEditIcon(
                    iconUri = uiState.iconUri,
                    contentDescription = stringResource(R.string.solutionEdit_cd_add_icon),
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .size(112.dp),
                )
                IconButton(
                    onClick = { launchImagePicker(ImagePickTarget.SolutionIcon) },
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 84.dp)
                        .size(64.dp),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.icons8_camera_90_w_circle),
                        contentDescription = stringResource(R.string.solutionEdit_cd_pick_image),
                        tint = Color.Unspecified,
                        modifier = Modifier.size(56.dp),
                    )
                }
            }

            Column(
                modifier = Modifier.offset(y = (-28).dp),
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        OutlinedTextField(
                            value = uiState.name,
                            onValueChange = viewModel::updateName,
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text(stringResource(R.string.solutionEdit_field_name_label)) },
                            isError = uiState.nameError,
                            supportingText = if (uiState.nameError) {
                                { Text(stringResource(R.string.solutionEdit_field_name_error)) }
                            } else {
                                null
                            },
                            singleLine = true,
                        )
                        OutlinedTextField(
                            value = uiState.creator,
                            onValueChange = viewModel::updateCreator,
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text(stringResource(R.string.solutionEdit_field_creator_label)) },
                            isError = uiState.creatorError,
                            supportingText = if (uiState.creatorError) {
                                { Text(stringResource(R.string.solutionEdit_field_creator_error)) }
                            } else {
                                null
                            },
                            singleLine = true,
                        )
                        OutlinedTextField(
                            value = uiState.description,
                            onValueChange = viewModel::updateDescription,
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text(stringResource(R.string.solutionEdit_field_description_label)) },
                            isError = uiState.descriptionError,
                            supportingText = if (uiState.descriptionError) {
                                { Text(stringResource(R.string.solutionEdit_field_description_error)) }
                            } else {
                                null
                            },
                            minLines = 2,
                            maxLines = 3,
                        )
                    }
                }

                uiState.steps.forEach { step ->
                    StepEditCard(
                        step = step,
                        onNameChange = { viewModel.updateStepName(step.localId, it) },
                        onDescriptionChange = { viewModel.updateStepDescription(step.localId, it) },
                        onAlgorithmClick = { viewModel.openKeyboard(step.localId) },
                        onPickStart = { launchImagePicker(ImagePickTarget.StepStart(step.localId)) },
                        onPickEnd = { launchImagePicker(ImagePickTarget.StepEnd(step.localId)) },
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Button(
                        onClick = viewModel::addStep,
                        colors = ButtonDefaults.buttonColors(containerColor = Accent),
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(stringResource(R.string.solutionEdit_action_add_step))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = {
                            if (viewModel.save()) onSaved()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(stringResource(R.string.solutionEdit_action_save))
                    }
                }
            }
            Spacer(modifier = Modifier.height(28.dp))
        }
    }

    val keyboardStep = uiState.steps.find { it.localId == uiState.keyboardStepLocalId }
    if (keyboardStep != null) {
        AlgorithmKeyboardDialog(
            algorithm = keyboardStep.algorithm,
            onAlgorithmChange = { viewModel.updateStepAlgorithm(keyboardStep.localId, it) },
            onDismiss = viewModel::closeKeyboard,
        )
    }

    if (uiState.showValidationDialog) {
        AlertDialog(
            onDismissRequest = viewModel::dismissValidationDialog,
            title = { Text(stringResource(R.string.solutionEdit_dialog_validation_title)) },
            text = { Text(stringResource(R.string.solutionEdit_dialog_validation_message)) },
            confirmButton = {
                TextButton(onClick = viewModel::dismissValidationDialog) {
                    Text(stringResource(R.string.common_action_ok))
                }
            },
        )
    }

    if (uiState.showDiscardDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.showDiscardDialog(false) },
            title = { Text(stringResource(R.string.solutionEdit_dialog_discard_title)) },
            text = {
                Text(stringResource(R.string.solutionEdit_dialog_discard_message))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.showDiscardDialog(false)
                        onNavigateBack()
                    },
                ) {
                    Text(stringResource(R.string.common_action_close))
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.showDiscardDialog(false) }) {
                    Text(stringResource(R.string.common_action_cancel))
                }
            },
        )
    }
}

@Composable
private fun StepEditCard(
    step: StepDraft,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onAlgorithmClick: () -> Unit,
    onPickStart: () -> Unit,
    onPickEnd: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedTextField(
                value = step.name,
                onValueChange = onNameChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.solutionEdit_step_name_label)) },
                singleLine = true,
            )
            OutlinedTextField(
                value = step.description,
                onValueChange = onDescriptionChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.solutionEdit_step_instruction_label)) },
                supportingText = { Text("${step.description.length}/150") },
                minLines = 2,
                maxLines = 2,
            )
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = step.algorithm,
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.solutionEdit_step_algorithm_label)) },
                    readOnly = true,
                    singleLine = true,
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable(onClick = onAlgorithmClick),
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                StepImageColumn(
                    imageUri = step.startImage,
                    buttonLabel = stringResource(R.string.solutionEdit_step_image_start_btn),
                    onPick = onPickStart,
                    modifier = Modifier.weight(1f),
                )
                StepImageColumn(
                    imageUri = step.endImage,
                    buttonLabel = stringResource(R.string.solutionEdit_step_image_end_btn),
                    onPick = onPickEnd,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun SolutionEditIcon(
    iconUri: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val model = rememberIconModel(iconUri.ifBlank { null })
    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(model)
            .placeholder(R.drawable.cfop)
            .error(R.drawable.cfop)
            .crossfade(true)
            .build(),
        contentDescription = contentDescription,
        contentScale = ContentScale.Crop,
        modifier = modifier.clip(CircleShape),
    )
}

@Composable
private fun StepImageColumn(
    imageUri: String,
    buttonLabel: String,
    onPick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val model = rememberIconModel(imageUri.ifBlank { null })
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(model)
                .placeholder(R.drawable.cfop)
                .error(R.drawable.cfop)
                .crossfade(true)
                .build(),
            contentDescription = stringResource(R.string.solutionEdit_step_image_description),
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
        )
        Button(
            onClick = onPick,
            colors = ButtonDefaults.buttonColors(containerColor = Primary),
            modifier = Modifier.padding(top = 8.dp),
        ) {
            Text(buttonLabel, color = White)
        }
    }
}

@DefaultPreviews
@Composable
private fun SolutionEditScreenPreview() {
    val step = PreviewSamples.step
    AlgorubickTheme {
        Column {
            SolutionEditIcon(
                iconUri = "",
                contentDescription = null,
                modifier = Modifier.size(130.dp),
            )
            StepEditCard(
                step = StepDraft(
                    localId = 1,
                    name = step.stepName,
                    description = step.stepDescription,
                    algorithm = step.stepAlgorithm,
                ),
                onNameChange = {},
                onDescriptionChange = {},
                onAlgorithmClick = {},
                onPickStart = {},
                onPickEnd = {},
            )
        }
    }
}
