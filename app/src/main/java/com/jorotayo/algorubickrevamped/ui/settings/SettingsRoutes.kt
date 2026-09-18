package com.jorotayo.algorubickrevamped.ui.settings

object SettingsRoutes {
    const val Hub = "settings"
    const val Display = "settings/display"
    const val Practice = "settings/practice"
    const val Miscellaneous = "settings/miscellaneous"

    fun isSettingsRoute(route: String?): Boolean =
        route == Hub || route == Display || route == Practice || route == Miscellaneous
}
