package com.jorotayo.algorubickrevamped.ui.theme

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

/**
 * Standard multipreview for Algorubick UI: light, dark, and large font scale on API 36.
 */
@Preview(
    name = "Light",
    group = "Default",
    apiLevel = 36,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Preview(
    name = "Dark",
    group = "Default",
    apiLevel = 36,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Preview(
    name = "Scaled",
    group = "Default",
    apiLevel = 36,
    showBackground = true,
    fontScale = 1.5f,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
annotation class DefaultPreviews
