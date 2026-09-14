package com.jorotayo.algorubickrevamped.ui.navigation

import android.content.Intent
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.jorotayo.algorubickrevamped.R
import com.jorotayo.algorubickrevamped.ui.home.Activity_Algorithm
import com.jorotayo.algorubickrevamped.ui.home.Activity_StudyAlgorithm
import com.jorotayo.algorubickrevamped.ui.home.AlgorithmHomeScreen
import com.jorotayo.algorubickrevamped.ui.home.AlgorithmHomeViewModel
import com.jorotayo.algorubickrevamped.ui.notation.NotationScreen
import com.jorotayo.algorubickrevamped.ui.solution_guide.SolutionGuideScreen
import com.jorotayo.algorubickrevamped.ui.theme.AlgorubickTheme
import com.jorotayo.algorubickrevamped.ui.theme.DefaultPreviews
import com.jorotayo.algorubickrevamped.ui.theme.Primary
import com.jorotayo.algorubickrevamped.ui.theme.PrimaryLight
import com.jorotayo.algorubickrevamped.ui.theme.White
import com.jorotayo.algorubickrevamped.ui.timer.StatisticsActivity
import com.jorotayo.algorubickrevamped.ui.timer.TimerScreen
import java.util.ArrayList

private val AppBarColors
    @Composable
    get() = TopAppBarDefaults.topAppBarColors(
        containerColor = Primary,
        titleContentColor = White,
        actionIconContentColor = White,
        navigationIconContentColor = White,
        scrolledContainerColor = Primary,
    )

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlgorubickApp(
    homeViewModel: AlgorithmHomeViewModel = viewModel(),
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val context = LocalContext.current
    val homeState by homeViewModel.uiState.collectAsStateWithLifecycle()
    var searchExpanded by remember { mutableStateOf(false) }

    val currentDestination = TopLevelDestination.entries.firstOrNull { it.route == currentRoute }
        ?: TopLevelDestination.Home

    val onHome = currentRoute == TopLevelDestination.Home.route

    LaunchedEffect(currentRoute) {
        if (!onHome) {
            searchExpanded = false
            if (homeState.selectionMode) homeViewModel.clearSelection()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            when {
                onHome && homeState.selectionMode -> {
                    TopAppBar(
                        title = { Text(stringResource(R.string.navigation_selection_count, homeState.selectedIds.size)) },
                        navigationIcon = {
                            IconButton(onClick = homeViewModel::clearSelection) {
                                Icon(Icons.Default.Close, contentDescription = stringResource(R.string.common_cd_clear_selection))
                            }
                        },
                        actions = {
                            IconButton(
                                onClick = {
                                    val ids = homeViewModel.getSelectedIds()
                                    context.startActivity(
                                        Intent(context, Activity_StudyAlgorithm::class.java)
                                            .putExtra("learn", ArrayList(ids)),
                                    )
                                    homeViewModel.clearSelection()
                                },
                            ) {
                                Icon(
                                    Icons.Default.Lightbulb,
                                    contentDescription = stringResource(R.string.navigation_cd_learn),
                                )
                            }
                            IconButton(
                                onClick = {
                                    val ids = homeViewModel.getSelectedIds()
                                    context.startActivity(
                                        Intent(context, Activity_StudyAlgorithm::class.java)
                                            .putExtra("practice", ArrayList(ids)),
                                    )
                                    homeViewModel.clearSelection()
                                },
                            ) {
                                Icon(
                                    Icons.Default.School,
                                    contentDescription = stringResource(R.string.navigation_cd_practice),
                                )
                            }
                        },
                        colors = AppBarColors,
                    )
                }

                onHome && searchExpanded -> {
                    TopAppBar(
                        title = {
                            BasicTextField(
                                value = homeState.searchQuery,
                                onValueChange = homeViewModel::setSearchQuery,
                                singleLine = true,
                                cursorBrush = SolidColor(White),
                                textStyle = TextStyle(color = White, fontSize = 18.sp),
                                modifier = Modifier.fillMaxWidth(),
                                decorationBox = { inner ->
                                    if (homeState.searchQuery.isEmpty()) {
                                        Text(
                                            text = stringResource(R.string.common_search_hint),
                                            color = White.copy(alpha = 0.7f),
                                            fontSize = 18.sp,
                                        )
                                    }
                                    inner()
                                },
                            )
                        },
                        navigationIcon = {
                            IconButton(
                                onClick = {
                                    searchExpanded = false
                                    homeViewModel.setSearchQuery("")
                                },
                            ) {
                                Icon(Icons.Default.Close, contentDescription = stringResource(R.string.common_cd_close_search))
                            }
                        },
                        actions = {
                            if (homeState.searchQuery.isNotEmpty()) {
                                IconButton(onClick = { homeViewModel.setSearchQuery("") }) {
                                    Icon(Icons.Default.Close, contentDescription = stringResource(R.string.common_cd_clear_query))
                                }
                            }
                        },
                        colors = AppBarColors,
                    )
                }

                else -> {
                    TopAppBar(
                        title = { Text(stringResource(currentDestination.titleRes)) },
                        colors = AppBarColors,
                        actions = {
                            if (onHome) {
                                IconButton(onClick = { searchExpanded = true }) {
                                    Icon(
                                        Icons.Default.Search,
                                        contentDescription = stringResource(R.string.navigation_cd_search),
                                    )
                                }
                            }
                            if (currentRoute == TopLevelDestination.Timer.route) {
                                IconButton(
                                    onClick = {
                                        context.startActivity(
                                            Intent(context, StatisticsActivity::class.java),
                                        )
                                    },
                                ) {
                                    Icon(Icons.Default.BarChart, contentDescription = stringResource(R.string.navigation_cd_statistics))
                                }
                            }
                        },
                    )
                }
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = Primary,
                contentColor = White,
                tonalElevation = 0.dp,
            ) {
                TopLevelDestination.entries.forEach { destination ->
                    val selected = currentRoute == destination.route
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = if (selected) {
                                    destination.selectedIcon
                                } else {
                                    destination.unselectedIcon
                                },
                                contentDescription = stringResource(destination.titleRes),
                            )
                        },
                        label = { Text(stringResource(destination.titleRes)) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = White,
                            selectedTextColor = White,
                            unselectedIconColor = White.copy(alpha = 0.65f),
                            unselectedTextColor = White.copy(alpha = 0.65f),
                            indicatorColor = PrimaryLight,
                        ),
                    )
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = TopLevelDestination.Home.route,
            modifier = Modifier.padding(padding),
        ) {
            composable(TopLevelDestination.Home.route) {
                AlgorithmHomeScreen(
                    viewModel = homeViewModel,
                    onCreate = {
                        context.startActivity(Intent(context, Activity_Algorithm::class.java))
                    },
                    onOpen = { id ->
                        context.startActivity(
                            Intent(context, Activity_Algorithm::class.java).putExtra(
                                "algorithm_id",
                                id,
                            ),
                        )
                    },
                )
            }
            composable(TopLevelDestination.Timer.route) { TimerScreen() }
            composable(TopLevelDestination.Notation.route) { NotationScreen() }
            composable(TopLevelDestination.Solutions.route) { SolutionGuideScreen() }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@DefaultPreviews
@Composable
private fun AlgorubickAppPreview() {
    AlgorubickTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.navigation_title_home)) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Primary,
                        titleContentColor = White,
                        actionIconContentColor = White,
                    ),
                    actions = {
                        IconButton(onClick = {}) {
                            Icon(Icons.Default.Search, contentDescription = stringResource(R.string.navigation_cd_search))
                        }
                    },
                )
            },
            bottomBar = {
                NavigationBar(containerColor = Primary) {
                    NavigationBarItem(
                        selected = true,
                        onClick = {},
                        icon = {
                            Icon(
                                imageVector = TopLevelDestination.Home.selectedIcon,
                                contentDescription = stringResource(R.string.navigation_title_home),
                            )
                        },
                        label = { Text(stringResource(R.string.navigation_title_home)) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.secondary,
                            selectedTextColor = MaterialTheme.colorScheme.secondary,
                            unselectedIconColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.75f),
                            unselectedTextColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.75f),
                            indicatorColor = Primary,
                        ),
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = {},
                        icon = {
                            Icon(
                                imageVector = TopLevelDestination.Timer.unselectedIcon,
                                contentDescription = stringResource(R.string.navigation_title_timer),
                            )
                        },
                        label = { Text(stringResource(R.string.navigation_title_timer)) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.secondary,
                            selectedTextColor = MaterialTheme.colorScheme.secondary,
                            unselectedIconColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.75f),
                            unselectedTextColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.75f),
                            indicatorColor = Primary,
                        ),
                    )
                }
            },
        ) { padding ->
            Text(
                text = stringResource(R.string.navigation_preview_home_content),
                modifier = Modifier.padding(padding).padding(16.dp),
            )
        }
    }
}
