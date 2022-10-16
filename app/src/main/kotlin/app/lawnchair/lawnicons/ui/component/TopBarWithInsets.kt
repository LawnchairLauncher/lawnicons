package app.lawnchair.lawnicons.ui.component

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.lawnchair.lawnicons.ui.util.Elevation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarWithInsets(
    navigationIcon: @Composable () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    title: String,
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
        LargeTopAppBar(
            modifier = Modifier
                .statusBarsPadding()
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            scrollBehavior = scrollBehavior,
            navigationIcon = navigationIcon,
            title = {
                Text(
                    text = title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
            colors = TopAppBarDefaults.largeTopAppBarColors(
                containerColor = Color.Transparent,
                scrolledContainerColor = Color.Transparent,
            ),
        )
    }
}
