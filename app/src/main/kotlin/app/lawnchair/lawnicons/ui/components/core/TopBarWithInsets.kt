package app.lawnchair.lawnicons.ui.components.core

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.lawnchair.lawnicons.ui.components.home.ClickableIcon
import app.lawnchair.lawnicons.ui.theme.LawniconsTheme
import app.lawnchair.lawnicons.ui.util.Elevation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarWithInsets(
    navigationIcon: @Composable () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    title: String,
    isExpandedScreen: Boolean = false,
) {
    val containerColor: Color = MaterialTheme.colorScheme.surface
    val scrolledContainerColor: Color =
        MaterialTheme.colorScheme.surfaceColorAtElevation(Elevation.Level2)

    val backgroundColor: Color = lerp(
        containerColor,
        scrolledContainerColor,
        FastOutLinearInEasing.transform(scrollBehavior.state.overlappedFraction),
    )

    Surface(
        color = backgroundColor,
    ) {
        if (isExpandedScreen) {
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent,
                ),
            )
        } else {
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
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent,
                ),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun TopBarWithInsetsPreview() {
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
        )
    }
}
