package app.lawnchair.lawnicons.ui.component

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarWithInsets(
    navigationIcon: @Composable () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    title: String,
) {
    val containerColor: Color = MaterialTheme.colorScheme.surface
    val scrolledContainerColor: Color = MaterialTheme.colorScheme.surfaceColorAtElevation(3.0.dp)

    val statusBarColor: Color = lerp(
        containerColor,
        scrolledContainerColor,
        FastOutLinearInEasing.transform(scrollBehavior.state.overlappedFraction)
    )

    Column {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .background(statusBarColor)
                .statusBarsPadding(),
        )
        TopAppBar(
            scrollBehavior = scrollBehavior,
            navigationIcon = navigationIcon,
            title = {
                Text(
                    text = title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
        )
    }
}
