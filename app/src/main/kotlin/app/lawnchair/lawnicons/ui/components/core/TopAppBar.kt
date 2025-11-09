package app.lawnchair.lawnicons.ui.components.core

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import app.lawnchair.lawnicons.R
import app.lawnchair.lawnicons.ui.components.home.NavigationIconButton
import app.lawnchair.lawnicons.ui.theme.LawniconsTheme
import app.lawnchair.lawnicons.ui.theme.adaptiveSurfaceColor
import app.lawnchair.lawnicons.ui.util.PreviewLawnicons

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TopAppBar(
    navigationIcon: @Composable () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    title: String,
    modifier: Modifier = Modifier,
    isExpandedScreen: Boolean = false,
    containerColor: Color = adaptiveSurfaceColor,
) {
    val topAppBarColors = TopAppBarDefaults.topAppBarColors().copy(
        containerColor = containerColor,
        scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
    )

    if (!isExpandedScreen) {
        LargeFlexibleTopAppBar(
            modifier = modifier,
            scrollBehavior = scrollBehavior,
            navigationIcon = navigationIcon,
            title = {
                Text(title)
            },
            colors = topAppBarColors,
        )
    } else {
        TopAppBar(
            modifier = modifier,
            scrollBehavior = scrollBehavior,
            navigationIcon = navigationIcon,
            title = {
                Text(title)
            },
            colors = topAppBarColors,
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
                    painter = painterResource(R.drawable.ic_back),
                    onClick = {},
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
                    painter = painterResource(R.drawable.ic_back),
                    onClick = {},
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
