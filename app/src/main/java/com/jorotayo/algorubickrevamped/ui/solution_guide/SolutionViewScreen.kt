package com.jorotayo.algorubickrevamped.ui.solution_guide

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.jorotayo.algorubickrevamped.R
import com.jorotayo.algorubickrevamped.data.Steps
import com.jorotayo.algorubickrevamped.ui.theme.AlgorubickTheme
import com.jorotayo.algorubickrevamped.ui.theme.DefaultPreviews
import com.jorotayo.algorubickrevamped.ui.theme.PreviewSamples
import com.jorotayo.algorubickrevamped.ui.theme.Primary
import com.jorotayo.algorubickrevamped.utils.UtilMethods

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolutionViewScreen(
    solutionId: Long,
    onEdit: () -> Unit,
    onDeleted: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: SolutionViewViewModel = viewModel(
        key = "view-$solutionId",
        factory = SolutionViewViewModel.factory(solutionId),
    ),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    LaunchedEffect(state.notFound) {
        if (state.notFound) onNavigateBack()
    }

    BackHandler(onBack = onNavigateBack)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(stringResource(R.string.solutionView_title))
                        state.solution?.let {
                            Text(it.solutionName, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.common_action_back))
                    }
                },
                actions = {
                    IconButton(onClick = onEdit) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = stringResource(R.string.solutionView_cd_edit),
                        )
                    }
                    IconButton(onClick = { viewModel.showDeleteDialog(true) }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = stringResource(R.string.solutionView_cd_delete),
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            )
        },
    ) { padding ->
        val solution = state.solution ?: return@Scaffold

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(UtilMethods.iconModel(context, solution.solutionIconLocation))
                            .placeholder(R.drawable.cfop)
                            .error(R.drawable.cfop)
                            .build(),
                        contentDescription = stringResource(R.string.solutionView_cd_image),
                        modifier = Modifier.size(80.dp),
                        contentScale = ContentScale.Crop,
                    )
                    Column(modifier = Modifier.padding(start = 16.dp)) {
                        Text(solution.solutionName, style = MaterialTheme.typography.headlineSmall)
                        Text(solution.solutionCreator, style = MaterialTheme.typography.bodyMedium)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(solution.solutionDescription, style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text(stringResource(R.string.solutionView_steps_header), style = MaterialTheme.typography.titleMedium, color = Primary)
            }

            items(state.steps, key = { it.id }) { step ->
                StepCard(step)
            }
        }
    }

    if (state.showDeleteDialog) {
        val name = state.solution?.solutionName.orEmpty()
        AlertDialog(
            onDismissRequest = { viewModel.showDeleteDialog(false) },
            title = { Text(stringResource(R.string.solutionView_dialog_delete_title)) },
            text = { Text(stringResource(R.string.solutionView_dialog_delete_message, name)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (viewModel.deleteSolution()) onDeleted()
                    },
                ) { Text(stringResource(R.string.common_action_yes)) }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.showDeleteDialog(false) }) { Text(stringResource(R.string.common_action_no)) }
            },
        )
    }
}

@Composable
private fun StepCard(step: Steps) {
    val context = LocalContext.current
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(stringResource(R.string.solutionView_step_title_format, step.stepNumber + 1, step.stepName), style = MaterialTheme.typography.titleMedium)
            Text(step.stepDescription, style = MaterialTheme.typography.bodyMedium)
            if (step.stepAlgorithm.isNotBlank()) {
                Text(step.stepAlgorithm, style = MaterialTheme.typography.bodyLarge, color = Primary)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                if (step.stepImageStart.isNotBlank()) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(UtilMethods.iconModel(context, step.stepImageStart))
                            .error(R.drawable.cfop)
                            .build(),
                        contentDescription = stringResource(R.string.solutionView_cd_step_start),
                        modifier = Modifier.size(56.dp),
                        contentScale = ContentScale.Crop,
                    )
                }
                if (step.stepImageEnd.isNotBlank()) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(UtilMethods.iconModel(context, step.stepImageEnd))
                            .error(R.drawable.cfop)
                            .build(),
                        contentDescription = stringResource(R.string.solutionView_cd_step_end),
                        modifier = Modifier.size(56.dp),
                        contentScale = ContentScale.Crop,
                    )
                }
            }
        }
    }
}

@DefaultPreviews
@Composable
private fun SolutionViewScreenPreview() {
    val solution = PreviewSamples.solution
    AlgorubickTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(solution.solutionName, style = MaterialTheme.typography.headlineSmall)
            Text(solution.solutionCreator, style = MaterialTheme.typography.bodyMedium)
            Text(solution.solutionDescription, style = MaterialTheme.typography.bodyLarge)
            Text(stringResource(R.string.solutionView_steps_header), style = MaterialTheme.typography.titleMedium, color = Primary)
            StepCard(PreviewSamples.step)
        }
    }
}
