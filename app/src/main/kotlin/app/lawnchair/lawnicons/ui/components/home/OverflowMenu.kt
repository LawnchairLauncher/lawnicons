package app.lawnchair.lawnicons.ui.components.home

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp

@Composable
fun OverflowMenu(block: @Composable OverflowMenuScope.() -> Unit) {
    val showMenu = remember { mutableStateOf(false) }
    val overflowMenuScope = remember { OverflowMenuScopeImpl(showMenu) }

    Box {
        ClickableIcon(
            imageVector = Icons.Rounded.MoreVert,
            size = 52.dp,
            onClick = { showMenu.value = true },
        )
        DropdownMenu(
            expanded = showMenu.value,
            onDismissRequest = { showMenu.value = false },
            offset = DpOffset(x = 16.dp, y = (-36).dp),
        ) {
            CompositionLocalProvider(
                LocalContentColor
                    provides MaterialTheme.colorScheme.onSurface,
            ) {
                block(overflowMenuScope)
            }
        }
    }
}

interface OverflowMenuScope {
    fun hideMenu()
}

private class OverflowMenuScopeImpl(private val showState: MutableState<Boolean>) :
    OverflowMenuScope {
    override fun hideMenu() {
        showState.value = false
    }
}
