package app.lawnchair.lawnicons.ui.util

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import app.lawnchair.lawnicons.data.model.IconInfo
import app.lawnchair.lawnicons.data.model.LabelAndComponent

@Preview(
    name = "Normal",
    showBackground = true,
    wallpaper = Wallpapers.NONE,
)
@Preview(
    name = "Normal Night",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    wallpaper = Wallpapers.NONE,
)
@Preview(
    name = "Themed",
    showBackground = true,
    wallpaper = Wallpapers.RED_DOMINATED_EXAMPLE,
)
@Preview(
    name = "Themed Night",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    wallpaper = Wallpapers.RED_DOMINATED_EXAMPLE,
)
annotation class PreviewLawnicons

object SampleData {
    val iconInfoSample = IconInfo(
        drawableName = "@drawable/email",
        componentNames = listOf(
            LabelAndComponent("Email", "com.android.email"),
        ),
        drawableId = 1,
    )
    val iconInfoList = listOf(
        IconInfo(
            drawableName = "@drawable/email",
            componentNames = listOf(
                LabelAndComponent("Email", "com.android.email"),
            ),
            drawableId = 1,
        ),
        IconInfo(
            drawableName = "@drawable/search",
            componentNames = listOf(
                LabelAndComponent("Search", "com.android.search"),
            ),
            drawableId = 2,
        ),
        IconInfo(
            drawableName = "@drawable/phone",
            componentNames = listOf(
                LabelAndComponent("Phone", "com.android.phone"),
            ),
            drawableId = 3,
        ),
    )
}
