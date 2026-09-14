package com.jorotayo.algorubickrevamped.ui.theme

import com.jorotayo.algorubickrevamped.data.Algorithm
import com.jorotayo.algorubickrevamped.data.Solution
import com.jorotayo.algorubickrevamped.data.Solve
import com.jorotayo.algorubickrevamped.data.Steps

/** Sample entities for Compose previews (no ObjectBox). */
object PreviewSamples {
    val algorithm = Algorithm(
        id = 1,
        alg_name = "Sexy Move",
        alg = "R,U,R',U'",
        alg_description = "Basic 4-move trigger used throughout CFOP.",
        algorithm_icon = "",
        category = "Triggers",
        practiced_correctly_int = 12,
        practiced_number_int = 20,
        custom_alg = false,
        favourite_alg = true,
        selected_alg = false,
        learnt = true,
        createdTime = 1_700_000_000_000L,
    )

    val solution = Solution(
        id = 1,
        solutionName = "CFOP",
        solutionCreator = "Jessica Fridrich",
        solutionDescription = "Cross, F2L, OLL, PLL method for 3x3.",
        solutionIconLocation = "",
    )

    val step = Steps(
        id = 1,
        solutionName = "CFOP",
        stepNumber = 1,
        stepName = "Cross",
        stepDescription = "Solve the white cross on the bottom.",
        stepAlgorithm = "F,R,U,R',U',F'",
        stepImageStart = "",
        stepImageEnd = "",
    )

    val solve = Solve(
        id = 1,
        solve_cube_size = "3x3",
        solve_date = "14:30:00 14-09-2026",
        solve_milliseconds = 12540,
        solve_scramble = "R U R' U' F2 L D2",
        solve_time = "12.54s",
    )
}
