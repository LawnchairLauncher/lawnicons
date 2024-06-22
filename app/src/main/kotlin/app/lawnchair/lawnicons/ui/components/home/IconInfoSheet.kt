package app.lawnchair.lawnicons.ui.components.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.lawnchair.lawnicons.R
import app.lawnchair.lawnicons.model.IconInfo
import app.lawnchair.lawnicons.ui.components.IconLink
import app.lawnchair.lawnicons.ui.components.core.Card
import app.lawnchair.lawnicons.ui.components.core.ListRow
import app.lawnchair.lawnicons.ui.components.core.SimpleListRow
import app.lawnchair.lawnicons.ui.theme.LawniconsTheme
import app.lawnchair.lawnicons.ui.util.Constants
import app.lawnchair.lawnicons.ui.util.PreviewLawnicons
import app.lawnchair.lawnicons.ui.util.SampleData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IconInfoSheet(
    iconInfo: IconInfo,
    modifier: Modifier = Modifier,
    isPopupShown: (Boolean) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )

    ModalBottomSheet(
        onDismissRequest = {
            isPopupShown(false)
        },
        sheetState = sheetState,
        contentWindowInsets = {
            WindowInsets(0.dp)
        },
        modifier = modifier,
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
        ) {
            val githubName = iconInfo.drawableName.replace(
                oldValue = "_foreground",
                newValue = "",
            )

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    if (LocalInspectionMode.current) {
                        val icon = when (iconInfo.id) {
                            1 -> Icons.Rounded.Email
                            2 -> Icons.Rounded.Search
                            3 -> Icons.Rounded.Call
                            else -> Icons.Rounded.Warning
                        }
                        Icon(
                            icon,
                            contentDescription = iconInfo.drawableName,
                            modifier = Modifier.size(250.dp),
                            tint = MaterialTheme.colorScheme.onBackground,
                        )
                    } else {
                        Icon(
                            painterResource(id = iconInfo.id),
                            contentDescription = iconInfo.drawableName,
                            modifier = Modifier.size(250.dp),
                            tint = MaterialTheme.colorScheme.onBackground,
                        )
                    }
                }
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    IconLink(
                        iconResId = R.drawable.github_foreground,
                        label = stringResource(id = R.string.view_on_github),
                        url = "${Constants.GITHUB}/blob/develop/svgs/$githubName.svg",
                    )
                }
            }
            item {
                Card(
                    label = stringResource(id = R.string.drawable),
                ) {
                    SimpleListRow(
                        label = githubName,
                        description = stringResource(R.string.icon_info_outdated_warning),
                        divider = false,
                    )
                }
            }
            item {
                Spacer(Modifier.height(16.dp))
            }
            item {
                Text(
                    text = stringResource(id = R.string.mapped_components),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 32.dp, bottom = 6.dp),
                )
            }

            val groupedComponents = iconInfo.componentNames
                .groupBy { it.label }
                .map { (label, components) ->
                    label to components.map { it.componentName }
                }

            itemsIndexed(groupedComponents) { index, (label, componentName) ->
                IconInfoListRow(label, componentName, index, groupedComponents.lastIndex)
            }
            item {
                Spacer(Modifier.height(16.dp))
            }
            item {
                Spacer(Modifier.navigationBarsPadding())
            }
        }
    }
}

@Composable
private fun IconInfoListRow(
    label: String,
    componentNames: List<String>,
    currentIndex: Int,
    lastIndex: Int,
) {
    val showExpandButton = componentNames.size > 1
    var expanded by remember { mutableStateOf(false) }

    var fullHeight by remember { mutableStateOf(0.dp) }
    val animatedHeight by animateDpAsState(
        targetValue = fullHeight,
        label = "On component name expansion or contraction",
    )

    val density = LocalDensity.current

    ListRow(
        label = {
            SelectionContainer {
                Text(
                    text = label,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        },
        description = {
            SelectionContainer {
                Column(
                    modifier = Modifier
                        .onGloballyPositioned { coordinates ->
                            fullHeight = with(density) {
                                coordinates.size.height.toDp()
                            }
                        }
                        .horizontalScroll(rememberScrollState()),
                ) {
                    componentNames.firstOrNull()?.let {
                        Text(
                            text = it,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(end = 48.dp),
                        )
                    }
                    AnimatedVisibility(visible = showExpandButton && expanded) {
                        Column {
                            componentNames.forEach {
                                Text(
                                    text = it,
                                    maxLines = 2,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }
            }
        },
        divider = currentIndex < lastIndex,
        first = currentIndex == 0,
        last = currentIndex == lastIndex,
        endIcon = if (showExpandButton) {
            @Composable {
                IconButton(
                    modifier = Modifier.requiredSize(48.dp),
                    onClick = {
                        expanded = !expanded
                    },
                ) {
                    val angle by animateFloatAsState(
                        targetValue = (if (expanded) 180 else 0).toFloat(),
                        label = "Expand/collapse chevron rotation",
                    )

                    Icon(
                        imageVector = Icons.Rounded.ArrowDropDown,
                        contentDescription = stringResource(R.string.toggle_visibility_of_contents),
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.rotate(angle),
                    )
                }
            }
        } else {
            null
        },
        height = if (expanded) 48.dp + animatedHeight else 72.dp,
        background = true,
    )
}

@PreviewLawnicons
@Composable
private fun IconInfoPopupPreview() {
    val showPopup = remember { mutableStateOf(true) }
    LawniconsTheme {
        IconInfoSheet(
            iconInfo = SampleData.iconInfoSample,
        ) {
            showPopup.value = it
        }
    }
}
