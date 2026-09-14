package com.jorotayo.algorubickrevamped.ui.timer

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.compose.runtime.Composable
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import com.jorotayo.algorubickrevamped.R
import com.jorotayo.algorubickrevamped.ui.common.ComposeFragment

class TimerFragment : ComposeFragment() {

    @Composable
    override fun ScreenContent() {
        TimerScreen()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menu.clear()
                    menuInflater.inflate(R.menu.actionbar, menu)
                    menu.findItem(R.id.actionbar_search)?.isVisible = false
                    menu.findItem(R.id.actionbar_statistics)?.let { statsItem ->
                        statsItem.isVisible = true
                        statsItem.setOnMenuItemClickListener {
                            startActivity(Intent(requireContext(), StatisticsActivity::class.java))
                            true
                        }
                    }
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean = false
            },
            viewLifecycleOwner,
            Lifecycle.State.RESUMED,
        )
    }
}
