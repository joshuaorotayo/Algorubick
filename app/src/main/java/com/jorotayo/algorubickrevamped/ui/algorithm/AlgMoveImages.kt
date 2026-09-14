package com.jorotayo.algorubickrevamped.ui.algorithm

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.jorotayo.algorubickrevamped.R
import com.jorotayo.algorubickrevamped.ui.theme.AlgorubickTheme
import com.jorotayo.algorubickrevamped.ui.theme.DefaultPreviews

object AlgMoveImages {
    private val map: Map<String, Int> = mapOf(
        "R" to R.drawable.clockwise_r,
        "L" to R.drawable.clockwise_l,
        "F" to R.drawable.clockwise_f,
        "B" to R.drawable.clockwise_b,
        "U" to R.drawable.clockwise_u,
        "D" to R.drawable.clockwise_d,
        "R'" to R.drawable.anticlockwise_r,
        "L'" to R.drawable.anticlockwise_l,
        "F'" to R.drawable.anticlockwise_f,
        "B'" to R.drawable.anticlockwise_b,
        "U'" to R.drawable.anticlockwise_u,
        "D'" to R.drawable.anticlockwise_d,
        "R2" to R.drawable.double_r,
        "L2" to R.drawable.double_l,
        "F2" to R.drawable.double_f,
        "B2" to R.drawable.double_b,
        "U2" to R.drawable.double_u,
        "D2" to R.drawable.double_d,
        "r" to R.drawable.two_right,
        "l" to R.drawable.two_left,
        "f" to R.drawable.two_front,
        "b" to R.drawable.two_back,
        "u" to R.drawable.two_up,
        "d" to R.drawable.two_down,
        "r'" to R.drawable.dbl_r_prime,
        "l'" to R.drawable.dbl_l_prime,
        "f'" to R.drawable.dbl_f_prime,
        "b'" to R.drawable.dbl_b_prime,
        "u'" to R.drawable.dbl_u_prime,
        "d'" to R.drawable.dbl_d_prime,
        "r2" to R.drawable.dbl_r_two,
        "l2" to R.drawable.dbl_l_two,
        "f2" to R.drawable.dbl_f_two,
        "b2" to R.drawable.dbl_b_two,
        "u2" to R.drawable.dbl_u_two,
        "d2" to R.drawable.dbl_d_two,
        "X" to R.drawable.x_rotation,
        "Y" to R.drawable.y_rotation,
        "Z" to R.drawable.z_rotation,
        "X'" to R.drawable.x_prime_rotation,
        "Y'" to R.drawable.y_prime_rotation,
        "Z'" to R.drawable.z_prime_rotation,
        "E" to R.drawable.e_slice,
        "S" to R.drawable.s_slice,
        "M" to R.drawable.m_slice,
        "E2" to R.drawable.e2_slice,
        "S2" to R.drawable.s2_slice,
        "M2" to R.drawable.m2_slice,
        "E'" to R.drawable.e_prime,
        "S'" to R.drawable.s_prime,
        "M'" to R.drawable.m_prime,
    )

    @DrawableRes
    fun drawableFor(move: String): Int? = map[move.trim()]

    fun stepDrawables(algorithm: String?): List<Int> {
        if (algorithm.isNullOrBlank()) return emptyList()
        return algorithm.split(",")
            .mapNotNull { drawableFor(it) }
    }
}

@Composable
fun AlgMoveImagesRow(modifier: Modifier = Modifier) {
    val moves = listOf("R", "U", "R'", "U'")
    Row(
        modifier = modifier.padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        moves.forEach { move ->
            AlgMoveImages.drawableFor(move)?.let { resId ->
                Image(
                    painter = painterResource(resId),
                    contentDescription = move,
                    modifier = Modifier.size(48.dp),
                )
            }
        }
    }
}

@DefaultPreviews
@Composable
private fun AlgMoveImagesRowPreview() {
    AlgorubickTheme {
        AlgMoveImagesRow()
    }
}
