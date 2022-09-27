package app.lawnchair.lawnicons.ui.component

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import app.lawnchair.lawnicons.ui.util.Elevation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarWithInsets(
    navigationIcon: @Composable () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    title: String,
) {
    val containerColor: Color = MaterialTheme.colorScheme.surface
    val scrolledContainerColor: Color = MaterialTheme.colorScheme.surfaceColorAtElevation(Elevation.Level2)

    val backgroundColor: Color = lerp(
        containerColor,
        scrolledContainerColor,
        FastOutLinearInEasing.transform(scrollBehavior.state.overlappedFraction)
    )

    Surface (
        color = backgroundColor
    ) {
        TopAppBar(
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
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color.Transparent,
                scrolledContainerColor = Color.Transparent
            )
        )
    }
}
