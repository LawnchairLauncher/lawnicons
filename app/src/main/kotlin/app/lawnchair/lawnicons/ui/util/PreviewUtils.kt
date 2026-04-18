package app.lawnchair.lawnicons.ui.util

import android.content.ComponentName
import android.content.res.Configuration
import android.graphics.drawable.ColorDrawable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import app.lawnchair.lawnicons.data.model.Announcement
import app.lawnchair.lawnicons.data.model.AnnouncementLocation
import app.lawnchair.lawnicons.data.model.IconInfo
import app.lawnchair.lawnicons.data.model.LabelAndComponent
import app.lawnchair.lawnicons.data.model.OssLibrary
import app.lawnchair.lawnicons.data.model.SystemIconInfo
import app.lawnchair.lawnicons.ui.LawniconsActions
import app.lawnchair.lawnicons.ui.LocalLawniconsActions
import app.lawnchair.lawnicons.ui.theme.LawniconsTheme

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

@Composable
fun PreviewProviders(content: @Composable () -> Unit) {
    LawniconsTheme {
        CompositionLocalProvider(
            LocalLawniconsActions provides LawniconsActions(false, {}),
        ) {
            content()
        }
    }
}

object SampleData {
    val iconInfoSample = IconInfo(
        drawableName = "@drawable/email",
        componentNames = listOf(
            LabelAndComponent("Email", "com.android.email/.ExampleActivity"),
        ),
        drawableId = 1,
    )

    val iconInfoList = listOf(
        IconInfo(
            drawableName = "@drawable/email",
            componentNames = listOf(
                LabelAndComponent("Email", "com.android.email/.ExampleActivity"),
            ),
            drawableId = 1,
        ),
        IconInfo(
            drawableName = "@drawable/search",
            componentNames = listOf(
                LabelAndComponent("Search", "com.android.search/.ExampleActivity"),
            ),
            drawableId = 2,
        ),
        IconInfo(
            drawableName = "@drawable/phone",
            componentNames = listOf(
                LabelAndComponent("Phone", "com.android.phone/.ExampleActivity"),
            ),
            drawableId = 3,
        ),
    )

    val announcements = listOf(
        Announcement(
            title = "Example",
            description = "Example description",
            icon = "ic_example.svg",
            url = "https://example.com",
            location = AnnouncementLocation.Home,
        ),
        Announcement(
            title = "Example",
            description = "Example description",
            icon = "ic_example.svg",
            url = "https://example.com",
            location = AnnouncementLocation.IconRequest,
        ),
    )

    val iconRequestList = listOf(
        SystemIconInfo(
            label = "Email",
            componentName = ComponentName(
                "com.android.email",
                "com.android.email/.ExampleActivity",
            ),
            drawable = ColorDrawable(),
        ),
        SystemIconInfo(
            label = "Search",
            componentName = ComponentName(
                "com.android.search",
                "com.android.search/.ExampleActivity",
            ),
            drawable = ColorDrawable(),
        ),
    )

    val ossLibraries = listOf(
        OssLibrary(
            groupId = "group-1",
            artifactId = "example-library",
            name = "Example Library",
            version = "1",
            scm = OssLibrary.Scm(
                url = "https://example.com",
            ),
            spdxLicenses = listOf(
                OssLibrary.License(
                    name = "Example License",
                ),
            ),
        ),
        OssLibrary(
            groupId = "group-2",
            artifactId = "example-library-2",
            name = "Example Library 2",
            version = "2",
            scm = OssLibrary.Scm(
                url = "https://example.com",
            ),
            spdxLicenses = listOf(
                OssLibrary.License(
                    name = "Example License",
                ),
            ),
        ),
    )
}
