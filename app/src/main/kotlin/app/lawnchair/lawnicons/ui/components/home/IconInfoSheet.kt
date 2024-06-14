package app.lawnchair.lawnicons.ui.components.home

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
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
        modifier = modifier
            .navigationBarsPadding(),
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
                        url = "https://github.com/LawnchairLauncher/lawnicons/blob/develop/svgs/$githubName.svg",
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
            val result = iconInfo.componentNames
                .groupBy { it.label }
                .map { (key, values) ->
                    Pair(key, values.map { it.componentName })
                }

            itemsIndexed(result) { index, (label, componentName) ->
                val showExpandButton = componentName.size > 1
                var expanded by remember { mutableStateOf(false) }

                val isLast = index == result.lastIndex

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
                    contentModifier = Modifier.padding(vertical = 8.dp),
                    description = {
                        componentName.firstOrNull()?.let {
                            SelectionContainer {
                                Text(
                                    text = it.replace("/", "/\n"),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    },
                    divider = (if (showExpandButton) !expanded else true) && index < result.lastIndex,
                    first = index == 0,
                    last = isLast && !expanded,
                    endIcon = {
                        if (showExpandButton) {
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
                    },
                    height = 90.dp,
                    background = true,
                )
                if (showExpandButton) {
                    AnimatedVisibility(visible = expanded) {
                        SelectionContainer {
                            Surface(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .then(
                                        if (isLast) {
                                            Modifier
                                                .clip(
                                                    RoundedCornerShape(
                                                        topStart = 0.dp,
                                                        topEnd = 0.dp,
                                                        bottomStart = 16.dp,
                                                        bottomEnd = 16.dp,
                                                    ),
                                                )
                                        } else {
                                            Modifier
                                        },
                                    ),
                                color = MaterialTheme.colorScheme.surfaceContainer,
                            ) {
                                Column(
                                    Modifier
                                        .padding(start = 16.dp)
                                        .then(
                                            if (isLast) {
                                                Modifier
                                                    .padding(bottom = 16.dp)
                                            } else {
                                                Modifier
                                            },
                                        )
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                ) {
                                    componentName
                                        .drop(1)
                                        .forEach {
                                            Text(
                                                text = it.replace("/", "/\n"),
                                                maxLines = 2,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            )
                                        }
                                }
                            }
                        }
                    }
                }
            }
            item {
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

private fun copyTextToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(context.getString(R.string.copied_text), text)
    clipboard.setPrimaryClip(clip)
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
