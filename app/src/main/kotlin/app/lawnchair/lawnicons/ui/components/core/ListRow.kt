package app.lawnchair.lawnicons.ui.components.core

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ListItemShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import app.lawnchair.lawnicons.ui.components.core.ListRowDefaults.basePadding

object ListRowDefaults {
    val basePadding = 16.dp

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    val singleItemShapes: ListItemShapes
        @Composable get() = ListItemDefaults.shapes(
            shape = MaterialTheme.shapes.large,
            pressedShape = MaterialTheme.shapes.extraLarge,
            focusedShape = MaterialTheme.shapes.extraLarge,
        )
}

@Composable
fun ListRow(
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    contentModifier: Modifier = Modifier,
    description: (@Composable () -> Unit)? = null,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .semantics(
                mergeDescendants = true,
            ) {},
    ) {
        Content(
            label = label,
            description = description,
            modifier = contentModifier,
        )
    }
}

@Composable
private fun Content(
    label: @Composable () -> Unit,
    description: (@Composable () -> Unit)?,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = basePadding),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            label()
            if (description != null) {
                description()
            }
        }
    }
}
