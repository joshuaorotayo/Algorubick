package com.jorotayo.algorubickrevamped.ui.algorithm

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.jorotayo.algorubickrevamped.R
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jorotayo.algorubickrevamped.ui.keyboard.AlgorithmKeyboard
import com.jorotayo.algorubickrevamped.ui.theme.AlgorubickTheme
import com.jorotayo.algorubickrevamped.ui.theme.DefaultPreviews
import com.jorotayo.algorubickrevamped.ui.theme.PreviewSamples

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun StudyAlgorithmScreen(
    viewModel: StudyAlgorithmViewModel,
    onBack: () -> Unit,
    onSessionFinished: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.sessionFinished) {
        if (state.sessionFinished) onSessionFinished()
    }

    val title = when (state.mode) {
        StudyMode.Learn -> stringResource(R.string.algorithmStudy_title_learn)
        StudyMode.Practice -> stringResource(R.string.algorithmStudy_title_practice)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
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
        if (state.empty) {
            Text(
                text = stringResource(R.string.algorithmStudy_empty),
                modifier = Modifier.padding(padding).padding(16.dp),
            )
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = state.current?.alg_name.orEmpty(),
                    style = MaterialTheme.typography.headlineSmall,
                )
                if (state.mode == StudyMode.Learn) {
                    Text(
                        text = state.current?.alg.orEmpty(),
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
                if (state.mode == StudyMode.Practice) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text("${state.correctCount} / ${state.practicedCount}")
                        Text(state.timerText, style = MaterialTheme.typography.titleMedium)
                    }
                }

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    state.stepIcons.forEach { resId ->
                        Image(
                            painter = painterResource(resId),
                            contentDescription = null,
                            modifier = Modifier
                                .size(56.dp)
                                .padding(4.dp),
                        )
                    }
                }

                OutlinedTextField(
                    value = state.input,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.algorithmStudy_input_label)) },
                    modifier = Modifier.fillMaxWidth(),
                )

                Button(
                    onClick = viewModel::checkAnswer,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(stringResource(R.string.algorithmStudy_action_check))
                }
                Spacer(Modifier.height(8.dp))
            }

            AlgorithmKeyboard(
                algorithm = state.input,
                onAlgorithmChange = viewModel::updateInput,
            )
        }
    }

    state.feedbackTitleRes?.let { titleRes ->
        AlertDialog(
            onDismissRequest = viewModel::clearFeedback,
            title = { Text(stringResource(titleRes)) },
            text = {
                Text(stringResource(state.feedbackMessageRes ?: R.string.algorithmStudy_feedback_incorrect_message))
            },
            confirmButton = {
                TextButton(onClick = viewModel::clearFeedback) { Text(stringResource(R.string.common_action_ok)) }
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@DefaultPreviews
@Composable
private fun StudyAlgorithmScreenPreview() {
    val sample = PreviewSamples.algorithm
    val moveIcons = AlgMoveImages.stepDrawables(sample.alg)
    AlgorubickTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.algorithmStudy_title_learn)) },
                    navigationIcon = {
                        IconButton(onClick = {}) {
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
                    .fillMaxSize(),
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = sample.alg_name.orEmpty(),
                        style = MaterialTheme.typography.headlineSmall,
                    )
                    Text(
                        text = sample.alg.orEmpty(),
                        style = MaterialTheme.typography.titleMedium,
                    )
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        moveIcons.forEach { resId ->
                            Image(
                                painter = painterResource(resId),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(56.dp)
                                    .padding(4.dp),
                            )
                        }
                    }
                    OutlinedTextField(
                        value = "R,U",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.algorithmStudy_input_label)) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                AlgorithmKeyboard(
                    algorithm = "R,U",
                    onAlgorithmChange = {},
                )
            }
        }
    }
}
