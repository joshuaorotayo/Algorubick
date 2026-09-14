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
        if (icon.startsWith("file:///")) return 0
        val name = icon.replace("R.drawable.", "")
        val resId = context.resources.getIdentifier(name, "drawable", context.packageName)
        return if (resId != 0) resId else R.drawable.cfop
    }

    fun iconModel(context: Context, icon: String?): Any {
        if (icon.isNullOrEmpty()) return R.drawable.cfop
        if (icon.startsWith("file:///") || icon.startsWith("content://")) {
            return Uri.parse(icon)
        }
        icon.toIntOrNull()?.takeIf { it != 0 }?.let { return it }
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
