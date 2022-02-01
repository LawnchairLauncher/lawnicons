package app.lawnchair.lawnicons.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder

@Preview(showBackground = true)
@Composable
@OptIn(ExperimentalMaterialApi::class)
fun ContributorRowPlaceholder() {
    ListItem(
        icon = {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .placeholder(
                        visible = true,
                        shape = CircleShape,
                        highlight = PlaceholderHighlight.fade()
                    ),
            )
        }
    ) {
        Box(
            modifier = Modifier
                .width(96.dp)
                .height(18.dp)
                .placeholder(
                    visible = true,
                    highlight = PlaceholderHighlight.fade()
                )
        )
    }
}
