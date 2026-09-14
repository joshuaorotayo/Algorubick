package com.jorotayo.algorubickrevamped.utils

import android.content.Context
import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.jorotayo.algorubickrevamped.R
import com.jorotayo.algorubickrevamped.data.Algorithm

object UtilMethods {

    @DrawableRes
    fun resolveDrawableRes(context: Context, icon: String?): Int {
        if (icon.isNullOrEmpty()) return R.drawable.cfop
        if (icon.startsWith("file:///") || icon.startsWith("content://")) return 0
        // Never treat stored integers as drawable IDs — they break across builds with non-final R ids.
        if (icon.toIntOrNull() != null) return R.drawable.cfop
        val name = icon.removePrefix("R.drawable.")
        val resId = context.resources.getIdentifier(name, "drawable", context.packageName)
        return if (resId != 0) resId else R.drawable.cfop
    }

    fun iconModel(context: Context, icon: String?): Any {
        if (icon.isNullOrEmpty()) return R.drawable.cfop
        if (icon.startsWith("file:///") || icon.startsWith("content://")) {
            return Uri.parse(icon)
        }
        return resolveDrawableRes(context, icon)
    }

    fun algorithmIconModel(context: Context, algorithm: Algorithm): Any {
        return iconModel(context, algorithm.algorithm_icon)
    }
}

@Composable
fun rememberAlgorithmIconModel(algorithm: Algorithm): Any {
    val context = LocalContext.current
    return UtilMethods.algorithmIconModel(context, algorithm)
}

@Composable
fun rememberIconModel(icon: String?): Any {
    val context = LocalContext.current
    return UtilMethods.iconModel(context, icon)
}
