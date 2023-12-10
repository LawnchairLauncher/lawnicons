package app.lawnchair.lawnicons.ui.util

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import app.lawnchair.lawnicons.model.IconInfo
import kotlinx.collections.immutable.persistentListOf

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
        name = "Email",
        drawableName = "@drawable/email",
        packageName = "com.android.email",
        id = 1,
    )
    val iconInfoList = persistentListOf(
        IconInfo(
            name = "Email",
            drawableName = "@drawable/email",
            packageName = "com.android.email",
            id = 1,
        ),
        IconInfo(
            name = "Search",
            drawableName = "@drawable/search",
            packageName = "com.android.search",
            id = 2,
        ),
        IconInfo(
            name = "Phone",
            drawableName = "@drawable/phone",
            packageName = "com.android.phone",
            id = 3,
        ),
    )
}
