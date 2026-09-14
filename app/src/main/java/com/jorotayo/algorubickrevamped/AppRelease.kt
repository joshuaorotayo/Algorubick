package com.jorotayo.algorubickrevamped

import java.text.SimpleDateFormat
import java.util.Locale

/**
 * App version + Play release date shown in Settings.
 * Update [BuildConfig.RELEASE_DATE] (yyyy-MM-dd) whenever you ship a store release.
 */
object AppRelease {
    val versionName: String get() = BuildConfig.VERSION_NAME

    val releaseDateIso: String get() = BuildConfig.RELEASE_DATE

    fun formattedReleaseDate(locale: Locale = Locale.getDefault()): String {
        val parsed = runCatching {
            SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(releaseDateIso)
        }.getOrNull()
        val date = parsed ?: runCatching {
            SimpleDateFormat("yyyy-MM-dd", Locale.US).parse("2026-09-14")
        }.getOrNull()
        return if (date != null) {
            SimpleDateFormat("d MMMM yyyy", locale).format(date)
        } else {
            releaseDateIso
        }
    }
}
