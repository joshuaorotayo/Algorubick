package com.jorotayo.algorubickrevamped.ui.algorithm

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.jorotayo.algorubickrevamped.R
import com.jorotayo.algorubickrevamped.data.Algorithm
import com.jorotayo.algorubickrevamped.ui.theme.AlgorubickTheme
import com.jorotayo.algorubickrevamped.ui.theme.DefaultPreviews
import com.jorotayo.algorubickrevamped.ui.theme.PreviewSamples
import com.jorotayo.algorubickrevamped.utils.rememberAlgorithmIconModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlgorithmViewScreen(
    viewModel: AlgorithmViewViewModel,
    onBack: () -> Unit,
    onEdit: (Long) -> Unit,
    onLearn: (ArrayList<Long>) -> Unit,
    onPractice: (ArrayList<Long>) -> Unit,
    onDeleted: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.deleted) {
        if (state.deleted) onDeleted()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(stringResource(R.string.algorithmView_title))
                        state.algorithm?.alg_name?.let {
                            Text(it, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.common_action_back))
                    }
                },
                actions = {
                    IconButton(onClick = {
                        state.algorithm?.id?.let(onEdit)
                    }) {
                        Icon(Icons.Filled.Edit, contentDescription = stringResource(R.string.common_action_edit))
                    }
                    IconButton(onClick = viewModel::requestDelete) {
                        Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.common_action_delete))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            )
        },
    ) { padding ->
        when {
            state.notFound -> {
                Text(
                    text = stringResource(R.string.algorithmView_not_found),
                    modifier = Modifier.padding(padding).padding(16.dp),
                )
            }
            state.algorithm != null -> {
                AlgorithmViewContent(
                    algorithm = state.algorithm!!,
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    onLearn = { onLearn(viewModel.studyIdList()) },
                    onPractice = { onPractice(viewModel.studyIdList()) },
                )
            }
        }
    }

    if (state.showDeleteDialog) {
        AlertDialog(
            onDismissRequest = viewModel::dismissDelete,
            title = { Text(stringResource(R.string.algorithmView_dialog_delete_title)) },
            text = {
                Text(
                    stringResource(
                        R.string.algorithmView_dialog_delete_message,
                        state.algorithm?.alg_name.orEmpty(),
                    ),
                )
            },
            confirmButton = {
                TextButton(onClick = viewModel::confirmDelete) { Text(stringResource(R.string.common_action_yes)) }
            },
            dismissButton = {
                TextButton(onClick = viewModel::dismissDelete) { Text(stringResource(R.string.common_action_no)) }
            },
        )
    }
}

@Composable
private fun AlgorithmViewContent(
    algorithm: Algorithm,
    modifier: Modifier = Modifier,
    onLearn: () -> Unit,
    onPractice: () -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AsyncImage(
            model = rememberAlgorithmIconModel(algorithm),
            contentDescription = algorithm.alg_name,
            modifier = Modifier.size(120.dp),
            contentScale = ContentScale.Fit,
        )
        Text(algorithm.alg_name.orEmpty(), style = MaterialTheme.typography.headlineSmall)
        Text(algorithm.alg.orEmpty(), style = MaterialTheme.typography.titleMedium)
        Text(algorithm.category.orEmpty(), style = MaterialTheme.typography.bodyLarge)
        Text(
            stringResource(R.string.algorithmView_correct_practiced_format, algorithm.practiced_correctly_int, algorithm.practiced_number_int),
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(
            algorithm.alg_description.orEmpty(),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.fillMaxWidth(),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(R.string.algorithmView_label_favourite))
                Icon(
                    imageVector = if (algorithm.favourite_alg) {
                        Icons.Filled.Favorite
                    } else {
                        Icons.Outlined.FavoriteBorder
                    },
                    contentDescription = null,
                    tint = if (algorithm.favourite_alg) {
                        Color(0xFFE11D48)
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    modifier = Modifier.size(24.dp),
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(R.string.algorithmView_label_custom))
                Icon(
                    imageVector = if (algorithm.custom_alg) {
                        Icons.Filled.ThumbUp
                    } else {
                        Icons.Outlined.ThumbUp
                    },
                    contentDescription = null,
                    tint = if (algorithm.custom_alg) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    modifier = Modifier.size(24.dp),
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Button(onClick = onLearn, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.algorithmView_action_learn))
        }
        Button(onClick = onPractice, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.algorithmView_action_practice))
        }
    }
}

@DefaultPreviews
@Composable
private fun AlgorithmViewScreenPreview() {
    AlgorubickTheme {
        AlgorithmViewContent(
            algorithm = PreviewSamples.algorithm,
            onLearn = {},
            onPractice = {},
        )
    }
}
