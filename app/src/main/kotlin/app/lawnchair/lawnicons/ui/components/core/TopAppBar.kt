package app.lawnchair.lawnicons.ui.components.core

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.lawnchair.lawnicons.ui.components.home.NavigationIconButton
import app.lawnchair.lawnicons.ui.theme.LawniconsTheme
import app.lawnchair.lawnicons.ui.util.PreviewLawnicons

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    navigationIcon: @Composable () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    title: String,
    modifier: Modifier = Modifier,
    isExpandedScreen: Boolean = false,
) {
    if (!isExpandedScreen) {
        LargeTopAppBar(
            modifier = modifier,
            scrollBehavior = scrollBehavior,
            navigationIcon = navigationIcon,
            title = {
                Text(title)
            },
        )
    } else {
        TopAppBar(
            modifier = modifier,
            scrollBehavior = scrollBehavior,
            navigationIcon = navigationIcon,
            title = {
                Text(title)
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLawnicons
@Composable
private fun SmallTopAppBarPreview() {
    LawniconsTheme {
        TopAppBar(
            navigationIcon = {
                NavigationIconButton(
                    onClick = {},
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
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
@PreviewLawnicons
@Composable
private fun LargeTopAppBarPreview() {
    LawniconsTheme {
        TopAppBar(
            navigationIcon = {
                NavigationIconButton(
                    onClick = {},
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
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
