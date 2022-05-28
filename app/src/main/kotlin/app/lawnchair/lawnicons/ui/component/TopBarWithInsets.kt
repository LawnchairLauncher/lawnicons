package app.lawnchair.lawnicons.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.google.accompanist.insets.statusBarsHeight

@Composable
fun TopBarWithInsets(
    navigationIcon: @Composable () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    title: String,
) {
    val topBarColors = TopAppBarDefaults.smallTopAppBarColors()
    val scrollFraction = scrollBehavior.scrollFraction
    val statusBarColor by topBarColors.containerColor(scrollFraction)

    Column {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .background(statusBarColor)
                .statusBarsPadding(),
        )
        SmallTopAppBar(
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
