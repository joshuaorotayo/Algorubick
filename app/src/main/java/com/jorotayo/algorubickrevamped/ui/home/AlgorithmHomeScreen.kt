package com.jorotayo.algorubickrevamped.ui.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.widthIn
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.jorotayo.algorubickrevamped.R
import com.jorotayo.algorubickrevamped.data.Algorithm
import com.jorotayo.algorubickrevamped.ui.theme.AlgorubickTheme
import com.jorotayo.algorubickrevamped.ui.theme.DefaultPreviews
import com.jorotayo.algorubickrevamped.ui.theme.PreviewSamples
import com.jorotayo.algorubickrevamped.ui.theme.Primary
import com.jorotayo.algorubickrevamped.ui.theme.White
import com.jorotayo.algorubickrevamped.utils.UtilMethods

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlgorithmHomeScreen(
    viewModel: AlgorithmHomeViewModel = viewModel(),
    onCreate: () -> Unit,
    onOpen: (Long) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        viewModel.load()
    }

    LaunchedEffect(uiState.sortMode) {
        listState.animateScrollToItem(0)
    }

    BackHandler(enabled = uiState.selectionMode) {
        viewModel.clearSelection()
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            if (!uiState.selectionMode) {
                FloatingActionButton(
                    onClick = onCreate,
                    containerColor = Primary,
                    contentColor = White,
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.home_fab_create_algorithm),
                    )
                }
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            if (!uiState.selectionMode) {
                HomeCommandBar(
                    totalCount = uiState.totalCount,
                    sortMode = uiState.sortMode,
                    onSortChange = viewModel::setSort,
                )
            }

            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(
                    items = uiState.algorithms,
                    key = { it.id },
                ) { algorithm ->
                    AlgorithmCard(
                        algorithm = algorithm,
                        selected = algorithm.id in uiState.selectedIds,
                        selectionMode = uiState.selectionMode,
                        onClick = {
                            if (uiState.selectionMode) {
                                viewModel.toggleSelection(algorithm.id)
                            } else {
                                onOpen(algorithm.id)
                            }
                        },
                        onLongClick = {
                            if (uiState.selectionMode) {
                                viewModel.toggleSelection(algorithm.id)
                            } else {
                                viewModel.enterSelection(algorithm.id)
                            }
                        },
                        onFavourite = { viewModel.toggleFavourite(algorithm.id) },
                        onLearnt = { viewModel.toggleLearnt(algorithm.id) },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeCommandBar(
    totalCount: Int,
    sortMode: AlgorithmSortMode,
    onSortChange: (AlgorithmSortMode) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.home_total_algs_label),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = totalCount.toString(),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.weight(1f))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
            modifier = Modifier.widthIn(min = 188.dp),
        ) {
            TextField(
                value = stringResource(sortMode.labelRes),
                onValueChange = {},
                readOnly = true,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    .widthIn(min = 188.dp),
                colors = ExposedDropdownMenuDefaults.textFieldColors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                ),
                shape = RoundedCornerShape(12.dp),
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                AlgorithmSortMode.entries.forEach { mode ->
                    DropdownMenuItem(
                        text = { Text(stringResource(mode.labelRes)) },
                        onClick = {
                            onSortChange(mode)
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AlgorithmCard(
    algorithm: Algorithm,
    selected: Boolean,
    selectionMode: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onFavourite: () -> Unit,
    onLearnt: () -> Unit,
) {
    val background = if (selected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surface
    }
    val contentColor = if (selected) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurface
    }
    val mutedColor = if (selected) {
        MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.75f)
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = background),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = onLongClick,
                )
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(UtilMethods.algorithmIconModel(context, algorithm))
                    .error(R.drawable.cfop)
                    .fallback(R.drawable.cfop)
                    .placeholder(R.drawable.cfop)
                    .build(),
                contentDescription = stringResource(R.string.home_cd_alg_icon),
                modifier = Modifier.size(52.dp),
                contentScale = ContentScale.Fit,
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = algorithm.alg_name,
                    style = MaterialTheme.typography.titleMedium,
                    color = contentColor,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    text = algorithm.category,
                    style = MaterialTheme.typography.bodySmall,
                    color = mutedColor,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    text = algorithm.alg,
                    style = MaterialTheme.typography.bodyLarge,
                    color = contentColor,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    text = stringResource(R.string.home_card_correct_practiced_format, algorithm.practiced_correctly_int, algorithm.practiced_number_int),
                    style = MaterialTheme.typography.bodySmall,
                    color = mutedColor,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            if (selectionMode) {
                Checkbox(
                    checked = selected,
                    onCheckedChange = { onClick() },
                )
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onFavourite) {
                        Icon(
                            imageVector = if (algorithm.favourite_alg) {
                                Icons.Filled.Favorite
                            } else {
                                Icons.Outlined.FavoriteBorder
                            },
                            contentDescription = stringResource(R.string.home_cd_favourite),
                            tint = if (algorithm.favourite_alg) {
                                Color(0xFFE11D48)
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            modifier = Modifier.size(24.dp),
                        )
                    }
                    IconButton(onClick = onLearnt) {
                        Icon(
                            imageVector = if (algorithm.learnt) {
                                Icons.Filled.ThumbUp
                            } else {
                                Icons.Outlined.ThumbUp
                            },
                            contentDescription = stringResource(R.string.home_cd_learnt),
                            tint = if (algorithm.learnt) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            modifier = Modifier.size(24.dp),
                        )
                    }
                }
            }
        }
    }
}

@DefaultPreviews
@Composable
private fun AlgorithmHomeScreenPreview() {
    AlgorubickTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            HomeCommandBar(
                totalCount = 12,
                sortMode = AlgorithmSortMode.CreatedDate,
                onSortChange = {},
            )
            AlgorithmCard(
                algorithm = PreviewSamples.algorithm,
                selected = false,
                selectionMode = false,
                onClick = {},
                onLongClick = {},
                onFavourite = {},
                onLearnt = {},
            )
        }
    }
}
