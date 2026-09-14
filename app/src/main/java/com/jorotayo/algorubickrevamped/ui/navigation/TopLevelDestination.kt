package com.jorotayo.algorubickrevamped.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.ui.graphics.vector.ImageVector
import com.jorotayo.algorubickrevamped.R

sealed class TopLevelDestination(
    val route: String,
    val titleRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
) {
    data object Home : TopLevelDestination(
        route = "home",
        titleRes = R.string.navigation_title_home,
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
    )

    data object Timer : TopLevelDestination(
        route = "timer",
        titleRes = R.string.navigation_title_timer,
        selectedIcon = Icons.Filled.Timer,
        unselectedIcon = Icons.Outlined.Timer,
    )

    data object Notation : TopLevelDestination(
        route = "notation",
        titleRes = R.string.navigation_title_notation,
        selectedIcon = Icons.Filled.Info,
        unselectedIcon = Icons.Outlined.Info,
    )

    data object Solutions : TopLevelDestination(
        route = "solutions",
        titleRes = R.string.navigation_title_solutions,
        selectedIcon = Icons.Filled.MenuBook,
        unselectedIcon = Icons.Outlined.MenuBook,
    )

    companion object {
        val entries = listOf(Home, Timer, Notation, Solutions)
    }
}
