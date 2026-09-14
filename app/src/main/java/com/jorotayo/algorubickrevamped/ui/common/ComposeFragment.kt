package com.jorotayo.algorubickrevamped.ui.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import com.jorotayo.algorubickrevamped.ui.theme.AlgorubickTheme

/**
 * Hosts a Compose screen inside the existing Navigation Fragment graph.
 */
abstract class ComposeFragment : Fragment() {

    @Composable
    protected abstract fun ScreenContent()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AlgorubickTheme {
                    ScreenContent()
                }
            }
        }
    }
}
