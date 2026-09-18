package com.jorotayo.algorubickrevamped.ui.whatsnew

import com.jorotayo.algorubickrevamped.AppRelease

/**
 * What's New is shown after splash when the user has not dismissed it for the
 * **current app version**. Shipping a new [AppRelease.versionName] automatically
 * resets that so users see the tour again after updates.
 */
object WhatsNewPrefs {
    val currentVersion: String get() = AppRelease.versionName

    fun shouldShowAfterSplash(dismissedVersion: String): Boolean =
        dismissedVersion != currentVersion
}
