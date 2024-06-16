package app.lawnchair.lawnicons.ui.components.home

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import app.lawnchair.lawnicons.R
import app.lawnchair.lawnicons.model.IconRequest
import app.lawnchair.lawnicons.model.IconRequestModel
import app.lawnchair.lawnicons.ui.components.core.Card
import app.lawnchair.lawnicons.ui.util.Constants
import app.lawnchair.lawnicons.ui.util.isScrollingUp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun IconRequestFAB(
    iconRequestModel: IconRequestModel?,
    lazyGridState: LazyGridState,
    modifier: Modifier = Modifier,
) {
    if (iconRequestModel != null) {
        if (iconRequestModel.iconCount > 0) {
            IconRequestFAB(
                iconRequestList = iconRequestModel.list,
                iconCount = iconRequestModel.iconCount,
                lazyGridState = lazyGridState,
                modifier = modifier,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun IconRequestFAB(
    iconRequestList: List<IconRequest>,
    iconCount: Int,
    lazyGridState: LazyGridState,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    val list = iconRequestList.joinToString("\n") { "${it.name}\n${it.componentName}" }
    val request = buildForm(list.replace("\n", "%20"))

    val sheetExpanded = remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )

    val coroutineScope = rememberCoroutineScope()
    val interactionSource = remember { MutableInteractionSource() }
    val viewConfiguration = LocalViewConfiguration.current

    val directLinkEnabled = request.length < Constants.DIRECT_LINK_MAX_LENGTH

    LaunchedEffect(interactionSource) {
        var isLongClick = false

        interactionSource.interactions.collectLatest { interaction ->
            when (interaction) {
                is PressInteraction.Press -> {
                    isLongClick = false
                    delay(viewConfiguration.longPressTimeoutMillis)
                    isLongClick = true
                    if (directLinkEnabled) {
                        openLink(context, request)
                    } else {
                        coroutineScope.launch {
                            sheetExpanded.value = true
                            sheetState.show()
                        }
                    }
                }
                is PressInteraction.Release -> {
                    if (!isLongClick) {
                        coroutineScope.launch {
                            sheetExpanded.value = true
                            sheetState.show()
                        }
                    }
                }
                is PressInteraction.Cancel -> {
                    isLongClick = false
                }
            }
        }
    }

    ExtendedFloatingActionButton(
        text = {
            Text(stringResource(R.string.request_icons))
        },
        icon = {
            Icon(
                painter = painterResource(id = R.drawable.icon_request_app),
                contentDescription = null,
            )
        },
        onClick = {},
        expanded = lazyGridState.isScrollingUp(),
        interactionSource = interactionSource,
        modifier = modifier,
    )
    AnimatedVisibility(visible = sheetExpanded.value) {
        ModalBottomSheet(
            onDismissRequest = { sheetExpanded.value = false },
            sheetState = sheetState,
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                val detailsExpanded = remember { mutableStateOf(false) }

                Text(
                    text = stringResource(R.string.unthemed_icons_info_title, iconCount),
                    style = MaterialTheme.typography.headlineSmall,
                )
                if (!directLinkEnabled) {
                    detailsExpanded.value = true
                    Card {
                        Row(
                            modifier = Modifier
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Warning,
                                contentDescription = null,
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.unthemed_icons_info_text),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                } else {
                    Text(
                        text = stringResource(R.string.icon_request_hold_tip),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Row {
                        Button(
                            onClick = {
                                openLink(context, request)
                            },
                        ) {
                            Text(stringResource(R.string.request_all_unthemed_icons))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        FilledTonalButton(
                            onClick = {
                                detailsExpanded.value = !detailsExpanded.value
                            },
                        ) {
                            Text(stringResource(R.string.more_information))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                AnimatedVisibility(visible = detailsExpanded.value) {
                    Card {
                        Column(
                            modifier = Modifier
                                .verticalScroll(rememberScrollState())
                                .padding(16.dp),
                        ) {
                            Text(
                                text = list,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier
                                    .horizontalScroll(rememberScrollState()),
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                            ) {
                                TextButton(
                                    onClick = {
                                        copyTextToClipboard(context, list)
                                    },
                                ) {
                                    Text(stringResource(R.string.copy_to_clipboard))
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                TextButton(
                                    onClick = {
                                        openLink(context, Constants.ICON_REQUEST_FORM)
                                    },
                                ) {
                                    Text(stringResource(R.string.open_request_form))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun copyTextToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(context.getString(R.string.copied_text), text)
    clipboard.setPrimaryClip(clip)
}

private fun openLink(context: Context, link: String) {
    val website = Uri.parse(link)
    val intent = Intent(Intent.ACTION_VIEW, website)
    context.startActivity(intent)
}

private fun buildForm(string: String): String {
    return "https://docs.google.com/forms/d/e/1FAIpQLSe8ItNYse9f4z2aT1QgXkKeueVTucRdUYNhUpys5ShHPyRijg/viewform?entry.1759726669=$string"
}
