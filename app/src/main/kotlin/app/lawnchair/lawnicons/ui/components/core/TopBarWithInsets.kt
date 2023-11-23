package app.lawnchair.lawnicons.ui.components.core

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.lawnchair.lawnicons.ui.components.home.ClickableIcon
import app.lawnchair.lawnicons.ui.theme.LawniconsTheme
import app.lawnchair.lawnicons.ui.util.LawniconsPreview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarWithInsets(
    navigationIcon: @Composable () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    title: String,
    isExpandedScreen: Boolean = false,
) {
    if (!isExpandedScreen) {
        LargeTopAppBar(
            modifier = Modifier
                .statusBarsPadding()
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            scrollBehavior = scrollBehavior,
            navigationIcon = navigationIcon,
            title = {
                Text(title)
            },
        )
    } else {
        TopAppBar(
            modifier = Modifier
                .statusBarsPadding()
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            scrollBehavior = scrollBehavior,
            navigationIcon = navigationIcon,
            title = {
                Text(title)
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@LawniconsPreview
@Composable
fun SmallTopBarWithInsetsPreview() {
    LawniconsTheme {
        TopBarWithInsets(
            navigationIcon = {
                ClickableIcon(
                    onClick = {},
                    imageVector = Icons.Rounded.ArrowBack,
                    size = 40.dp,
                    modifier = Modifier.padding(horizontal = 4.dp),
                )
            },
            scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
            title = "Example",
            isExpandedScreen = true,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@LawniconsPreview
@Composable
fun LargeTopBarWithInsetsPreview() {
    LawniconsTheme {
        TopBarWithInsets(
            navigationIcon = {
                ClickableIcon(
                    onClick = {},
                    imageVector = Icons.Rounded.ArrowBack,
                    size = 40.dp,
                    modifier = Modifier.padding(horizontal = 4.dp),
                )
            },
            scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(),
            title = "Example",
            isExpandedScreen = false,
        )
    }
}
