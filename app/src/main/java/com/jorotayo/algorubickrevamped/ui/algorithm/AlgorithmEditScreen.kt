package com.jorotayo.algorubickrevamped.ui.algorithm

import android.net.Uri
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.jorotayo.algorubickrevamped.R
import com.jorotayo.algorubickrevamped.ui.keyboard.AlgorithmKeyboardDialog
import com.jorotayo.algorubickrevamped.ui.theme.AlgorubickTheme
import com.jorotayo.algorubickrevamped.ui.theme.DefaultPreviews
import com.jorotayo.algorubickrevamped.ui.theme.PreviewSamples
import com.jorotayo.algorubickrevamped.ui.theme.Primary
import com.jorotayo.algorubickrevamped.utils.UtilMethods

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlgorithmEditScreen(
    viewModel: AlgorithmEditViewModel,
    onBackWithoutSave: () -> Unit,
    onSaved: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var categoryExpanded by remember { mutableStateOf(false) }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri: Uri? ->
        if (uri != null) viewModel.updateImageUri(uri.toString())
    }

    LaunchedEffect(state.saved) {
        if (state.saved) {
            viewModel.consumeNavigation()
            onSaved()
        }
    }

    LaunchedEffect(state.exitWithoutSave) {
        if (state.exitWithoutSave) {
            viewModel.consumeNavigation()
            onBackWithoutSave()
        }
    }

    BackHandler { viewModel.requestExit() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (state.isEdit) stringResource(R.string.algorithmEdit_title_edit) else stringResource(R.string.algorithmEdit_title_create))
                },
                navigationIcon = {
                    IconButton(onClick = viewModel::requestExit) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.common_action_back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(168.dp)
                    .background(Primary),
                contentAlignment = Alignment.TopCenter,
            ) {
                AsyncImage(
                    model = UtilMethods.iconModel(context, state.imageUri),
                    contentDescription = stringResource(R.string.algorithmEdit_cd_image),
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .size(120.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                )
                IconButton(
                    onClick = { imagePicker.launch("image/*") },
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 96.dp)
                        .size(64.dp),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.icons8_camera_90_w_circle),
                        contentDescription = stringResource(R.string.algorithmEdit_cd_pick_image),
                        tint = Color.Unspecified,
                        modifier = Modifier.size(56.dp),
                    )
                }
            }

            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
            OutlinedTextField(
                value = state.name,
                onValueChange = viewModel::updateName,
                label = { Text(stringResource(R.string.algorithmEdit_field_name_label)) },
                isError = state.nameErrorRes != null,
                supportingText = state.nameErrorRes?.let { res -> { Text(stringResource(res)) } },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = state.algorithm,
                    onValueChange = {},
                    readOnly = true,
                    enabled = false,
                    label = { Text(stringResource(R.string.algorithmEdit_field_algorithm_label)) },
                    isError = state.algorithmErrorRes != null,
                    supportingText = state.algorithmErrorRes?.let { res -> { Text(stringResource(res)) } },
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledSupportingTextColor = MaterialTheme.colorScheme.error,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { viewModel.showKeyboard(true) },
                )
            }
            OutlinedTextField(
                value = state.description,
                onValueChange = viewModel::updateDescription,
                label = { Text(stringResource(R.string.algorithmEdit_field_description_label)) },
                isError = state.descriptionErrorRes != null,
                supportingText = state.descriptionErrorRes?.let { res -> { Text(stringResource(res)) } },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
            )

            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = it },
            ) {
                OutlinedTextField(
                    value = state.category,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.algorithmEdit_field_category_label)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(categoryExpanded) },
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                        .fillMaxWidth(),
                )
                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false },
                ) {
                    state.categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.category_name) },
                            onClick = {
                                viewModel.updateCategory(category.category_name)
                                categoryExpanded = false
                            },
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(stringResource(R.string.algorithmEdit_switch_favourite))
                Switch(checked = state.favourite, onCheckedChange = viewModel::updateFavourite)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(stringResource(R.string.algorithmEdit_switch_custom))
                Switch(checked = state.custom, onCheckedChange = viewModel::updateCustom)
            }

            Spacer(Modifier.height(8.dp))
            Button(
                onClick = { viewModel.save() },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.common_action_save))
            }
            }
        }
    }

    if (state.showKeyboard) {
        AlgorithmKeyboardDialog(
            algorithm = state.algorithm,
            onAlgorithmChange = viewModel::updateAlgorithm,
            onDismiss = { viewModel.showKeyboard(false) },
        )
    }

    if (state.showUnsavedDialog) {
        AlertDialog(
            onDismissRequest = viewModel::dismissUnsaved,
            title = { Text(stringResource(R.string.algorithmEdit_dialog_discard_title)) },
            text = {
                Text(stringResource(R.string.algorithmEdit_dialog_discard_message))
            },
            confirmButton = {
                TextButton(onClick = viewModel::confirmDiscard) { Text(stringResource(R.string.common_action_close)) }
            },
            dismissButton = {
                TextButton(onClick = viewModel::dismissUnsaved) { Text(stringResource(R.string.common_action_cancel)) }
            },
        )
    }
}

@DefaultPreviews
@Composable
private fun AlgorithmEditScreenPreview() {
    val sample = PreviewSamples.algorithm
    AlgorubickTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedTextField(
                value = sample.alg_name.orEmpty(),
                onValueChange = {},
                label = { Text(stringResource(R.string.algorithmEdit_field_name_label)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
            OutlinedTextField(
                value = sample.alg.orEmpty(),
                onValueChange = {},
                label = { Text(stringResource(R.string.algorithmEdit_field_algorithm_label)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
            OutlinedTextField(
                value = sample.alg_description.orEmpty(),
                onValueChange = {},
                label = { Text(stringResource(R.string.algorithmEdit_field_description_label)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
            )
            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.common_action_save))
            }
        }
    }
}
