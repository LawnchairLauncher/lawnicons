package app.lawnchair.lawnicons.ui.util

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import app.lawnchair.lawnicons.model.IconInfoAppfilter
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
    val iconInfoSample = IconInfoAppfilter(
        name = "Email",
        drawableName = "@drawable/email",
        componentName = "com.android.email",
        id = 1,
    )
    val iconInfoList = persistentListOf(
        IconInfoAppfilter(
            name = "Email",
            drawableName = "@drawable/email",
            componentName = "com.android.email",
            id = 1,
        ),
        IconInfoAppfilter(
            name = "Search",
            drawableName = "@drawable/search",
            componentName = "com.android.search",
            id = 2,
        ),
        IconInfoAppfilter(
            name = "Phone",
            drawableName = "@drawable/phone",
            componentName = "com.android.phone",
            id = 3,
        ),
    )
}
