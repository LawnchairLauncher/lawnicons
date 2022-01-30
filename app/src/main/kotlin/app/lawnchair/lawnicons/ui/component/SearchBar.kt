package app.lawnchair.lawnicons.ui.component

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.ContentAlpha
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.MoreVert
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
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import app.lawnchair.lawnicons.R
import app.lawnchair.lawnicons.ui.util.Destinations
import app.lawnchair.lawnicons.ui.util.Elevation
import app.lawnchair.lawnicons.ui.util.surfaceColorAtElevation

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
    var dropdownMenuExpanded by remember { mutableStateOf(value = false) }

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
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = ContentAlpha.medium),
                )
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = MaterialTheme.typography.titleLarge.copy(color = MaterialTheme.colorScheme.onSurface),
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
                ClickableIcon(
                    imageVector = Icons.Rounded.MoreVert,
                    size = 52.dp,
                    onClick = { dropdownMenuExpanded = true },
                )
                DropdownMenu(
                    modifier = Modifier.background(MaterialTheme.colorScheme.surfaceColorAtElevation(
                        Elevation.Level2)),
                    expanded = dropdownMenuExpanded,
                    onDismissRequest = { dropdownMenuExpanded = false },
                    offset = DpOffset(
                        x = (16).dp,
                        y = (-64).dp,
                    )
                ) {
                    DropdownMenuItem(onClick = { navController.navigate(Destinations.ACKNOWLEDGEMENTS) }) {
                        Text(text = stringResource(id = R.string.acknowledgements))
                    }
                    DropdownMenuItem(onClick = { navController.navigate(Destinations.ABOUT) }) {
                        Text(text = stringResource(id = R.string.about))
                    }
                }
            }
        }
    }
}
