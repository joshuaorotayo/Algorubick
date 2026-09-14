package com.jorotayo.algorubickrevamped.ui.home

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.fragment.app.viewModels
import com.jorotayo.algorubickrevamped.ui.common.ComposeFragment
import java.util.ArrayList

class AlgorithmHomeFragment : ComposeFragment() {

    private val viewModel: AlgorithmHomeViewModel by viewModels()

    @Composable
    override fun ScreenContent() {
        AlgorithmHomeScreen(
            viewModel = viewModel,
            onCreate = {
                startActivity(Intent(requireContext(), Activity_Algorithm::class.java))
            },
            onOpen = { id ->
                startActivity(
                    Intent(requireContext(), Activity_Algorithm::class.java).apply {
                        putExtra("algorithm_id", id)
                    },
                )
            },
            onLearn = { ids ->
                startActivity(
                    Intent(requireContext(), Activity_StudyAlgorithm::class.java).apply {
                        putExtra("learn", ArrayList(ids))
                    },
                )
            },
            onPractice = { ids ->
                startActivity(
                    Intent(requireContext(), Activity_StudyAlgorithm::class.java).apply {
                        putExtra("practice", ArrayList(ids))
                    },
                )
            },
        )
    }

    override fun onResume() {
        super.onResume()
        viewModel.load()
    }
}
