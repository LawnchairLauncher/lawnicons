package app.lawnchair.lawnicons.ui.components.home

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun IconRequestFAB(
    iconRequestModel: IconRequestModel?,
    lazyGridState: LazyGridState,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    RequestHandler(
        iconRequestModel = iconRequestModel,
        snackbarHostState = snackbarHostState,
    ) { interactionSource ->
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
    }
}

@Composable
fun IconRequestIconButton(
    iconRequestModel: IconRequestModel?,
    snackbarHostState: SnackbarHostState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    RequestHandler(
        iconRequestModel = iconRequestModel,
        snackbarHostState = snackbarHostState,
        onClick = onClick,
    ) { interactionSource ->
        IconButton(
            onClick = {},
            interactionSource = interactionSource,
            modifier = modifier,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.icon_request_app),
                contentDescription = stringResource(R.string.request_icons),
                modifier = Modifier.requiredSize(24.dp),
            )
        }
    }
}

@Composable
fun RequestHandler(
    iconRequestModel: IconRequestModel?,
    snackbarHostState: SnackbarHostState,
    onClick: () -> Unit = {},
    content: @Composable ((interactionSource: MutableInteractionSource) -> Unit),
) {
    if (iconRequestModel != null) {
        RequestHandler(
            iconRequestList = iconRequestModel.list,
            snackbarHostState = snackbarHostState,
            onClick = onClick,
        ) {
            content(it)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestHandler(
    iconRequestList: List<IconRequest>,
    snackbarHostState: SnackbarHostState,
    onClick: () -> Unit,
    content: @Composable ((interactionSource: MutableInteractionSource) -> Unit),
) {
    val context = LocalContext.current
    val viewConfiguration = LocalViewConfiguration.current

    val onClickEffect = rememberUpdatedState(onClick)

    val requestList = formatIconRequestList(iconRequestList)
    val encodedRequestList = buildForm(requestList.replace("\n", "%20"))

    val sheetExpanded = remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )

    val coroutineScope = rememberCoroutineScope()
    val interactionSource = remember { MutableInteractionSource() }

    val directLinkEnabled = encodedRequestList.length < Constants.DIRECT_LINK_MAX_LENGTH

    LaunchedEffect(interactionSource) {
        var isLongClick = false

        interactionSource.interactions.collectLatest { interaction ->
            when (interaction) {
                is PressInteraction.Press -> {
                    onClickEffect.value()
                    isLongClick = false
                    delay(viewConfiguration.longPressTimeoutMillis)
                    isLongClick = true
                    coroutineScope.launch {
                        sheetExpanded.value = true
                        sheetState.show()
                    }
                }

                is PressInteraction.Release -> {
                    if (!isLongClick) {
                        if (iconRequestList.isEmpty()) {
                            openLink(context, Constants.ICON_REQUEST_FORM)
                        } else if (directLinkEnabled) {
                            openLink(context, encodedRequestList)
                        } else {
                            openSnackbarContent(context, requestList, coroutineScope, snackbarHostState)
                        }
                    }
                }

                is PressInteraction.Cancel -> {
                    isLongClick = false
                }
            }
        }
    }

    content(interactionSource)

    AnimatedVisibility(visible = sheetExpanded.value) {
        ModalBottomSheet(
            onDismissRequest = { sheetExpanded.value = false },
            sheetState = sheetState,
        ) {
            IconRequestSheet(requestList, context)
        }
    }
}

@Composable
private fun IconRequestSheet(list: String, context: Context) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
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
                }
            }
        }
    }
}

private fun formatIconRequestList(iconRequestList: List<IconRequest>) =
    iconRequestList.joinToString("\n") { "${it.label}\n${it.componentName}" }

private fun copyTextToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(context.getString(R.string.copied_text), text)
    clipboard.setPrimaryClip(clip)
}

private fun openSnackbarContent(
    context: Context,
    list: String,
    coroutineScope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
) {
    copyTextToClipboard(context, list)
    coroutineScope.launch {
        val result = snackbarHostState
            .showSnackbar(
                message = context.getString(R.string.snackbar_request_too_large),
                actionLabel = context.getString(R.string.snackbar_use_fallback_link),
                withDismissAction = true,
                duration = SnackbarDuration.Indefinite,
            )
        when (result) {
            SnackbarResult.ActionPerformed -> {
                /* Handle snackbar action performed */
                openLink(context, Constants.ICON_REQUEST_FORM)
            }

            SnackbarResult.Dismissed -> {
                snackbarHostState.currentSnackbarData?.dismiss()
            }
        }
    }
}

private fun openLink(context: Context, link: String) {
    val website = Uri.parse(link)
    val intent = Intent(Intent.ACTION_VIEW, website)
    context.startActivity(intent)
}

private fun buildForm(string: String): String {
    return "https://docs.google.com/forms/d/e/1FAIpQLSe8ItNYse9f4z2aT1QgXkKeueVTucRdUYNhUpys5ShHPyRijg/viewform?entry.1759726669=$string"
}
