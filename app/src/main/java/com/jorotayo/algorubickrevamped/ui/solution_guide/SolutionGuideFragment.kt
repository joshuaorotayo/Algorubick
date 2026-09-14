package com.jorotayo.algorubickrevamped.ui.solution_guide

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.compose.runtime.Composable
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import com.jorotayo.algorubickrevamped.R
import com.jorotayo.algorubickrevamped.ui.common.ComposeFragment

class SolutionGuideFragment : ComposeFragment(), MenuProvider {

    override fun onStart() {
        super.onStart()
        requireActivity().addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.STARTED)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.findItem(R.id.actionbar_statistics)?.isVisible = false
        menu.findItem(R.id.actionbar_search)?.isVisible = false
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean = false

    @Composable
    override fun ScreenContent() {
        SolutionGuideScreen()
    }
}
