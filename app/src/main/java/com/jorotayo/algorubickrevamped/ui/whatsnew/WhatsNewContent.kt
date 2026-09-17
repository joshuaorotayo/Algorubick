package com.jorotayo.algorubickrevamped.ui.whatsnew

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.jorotayo.algorubickrevamped.R

/**
 * Bump [CONTENT_VERSION] whenever these pages change so returning users see What's New again.
 * "Don't show again" stores this value in DataStore.
 */
object WhatsNewContent {
    const val CONTENT_VERSION = "1.7.0"

    data class Page(
        @StringRes val titleRes: Int,
        @StringRes val bodyRes: Int,
        val icon: ImageVector,
        @DrawableRes val illustrationRes: Int? = null,
    )

    val pages: List<Page> = listOf(
        Page(
            titleRes = R.string.whatsNew_page1_title,
            bodyRes = R.string.whatsNew_page1_body,
            icon = Icons.Outlined.Settings,
        ),
        Page(
            titleRes = R.string.whatsNew_page2_title,
            bodyRes = R.string.whatsNew_page2_body,
            icon = Icons.Outlined.DarkMode,
        ),
        Page(
            titleRes = R.string.whatsNew_page3_title,
            bodyRes = R.string.whatsNew_page3_body,
            icon = Icons.Outlined.School,
        ),
        Page(
            titleRes = R.string.whatsNew_page4_title,
            bodyRes = R.string.whatsNew_page4_body,
            icon = Icons.Outlined.Home,
        ),
    )
}
