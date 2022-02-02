package app.lawnchair.lawnicons.ui.component

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.ContentAlpha
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import app.lawnchair.lawnicons.R
import app.lawnchair.lawnicons.ui.util.Destinations

@Composable
fun SearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    iconCount: Int,
    navController: NavController,
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    var focused by remember { mutableStateOf(value = false) }

    SearchBarBase {
        Box(
            contentAlignment = Alignment.CenterStart,
            modifier = Modifier
                .padding(start = 16.dp)
                .fillMaxHeight()
                .weight(1F),
        ) {
            if (value.isEmpty()) {
                Text(
                    text = stringResource(id = R.string.search_bar_hint, iconCount),
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = ContentAlpha.medium),
                )
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = MaterialTheme.typography.titleLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 20.sp,
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .fillMaxWidth()
                    .onFocusChanged { focused = it.isFocused },
            )
        }
        Crossfade(targetState = focused) {
            if (it) {
                ClickableIcon(
                    imageVector = Icons.Rounded.Clear,
                    size = 52.dp,
                    onClick = {
                        onValueChange("")
                        focusManager.clearFocus()
                    },
                )
            } else {
                OverflowMenu {
                    DropdownMenuItem(onClick = {
                        hideMenu()
                        navController.navigate(Destinations.ACKNOWLEDGEMENTS)
                    }) {
                        Text(text = stringResource(id = R.string.acknowledgements))
                    }
                    DropdownMenuItem(onClick = {
                        hideMenu()
                        navController.navigate(Destinations.ABOUT)
                    }) {
                        Text(text = stringResource(id = R.string.about))
                    }
                }
            }
        }
    }
}
