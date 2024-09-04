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
import androidx.compose.material3.SheetState
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.platform.ViewConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import app.lawnchair.lawnicons.R
import app.lawnchair.lawnicons.model.IconRequest
import app.lawnchair.lawnicons.model.IconRequestModel
import app.lawnchair.lawnicons.repository.preferenceManager
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
    val list = iconRequestModel?.list ?: emptyList()
    RequestHandler(
        iconRequestList = list,
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
    snackbarHostState: SnackbarHostState,
    iconRequestModel: IconRequestModel?,
    modifier: Modifier = Modifier,
) {
    val list = iconRequestModel?.list ?: emptyList()

    RequestHandler(
        iconRequestList = list,
        snackbarHostState = snackbarHostState,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestHandler(
    iconRequestList: List<IconRequest>,
    snackbarHostState: SnackbarHostState,
    content: @Composable ((interactionSource: MutableInteractionSource) -> Unit),
) {
    val prefs = preferenceManager()
    val showFirstLaunchSnackbar by prefs.showFirstLaunchSnackbar.asState()
    val context = LocalContext.current

    val requestList = formatIconRequestList(iconRequestList)
    val encodedRequestList = buildForm(requestList.replace("\n", "%20"))
    val directLinkEnabled = encodedRequestList.length < Constants.DIRECT_LINK_MAX_LENGTH

    var sheetExpanded by rememberSaveable { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )

    val scope = rememberCoroutineScope()
    val interactionSource = remember { MutableInteractionSource() }

    HandleTouchInteractions(
        interactionSource = interactionSource,
        viewConfiguration = LocalViewConfiguration.current,
        context = context,
        coroutineScope = scope,
        onExpandSheet = { sheetExpanded = it },
        sheetState = sheetState,
        iconRequestList = iconRequestList,
        directLinkEnabled = directLinkEnabled,
        encodedRequestList = encodedRequestList,
        requestList = requestList,
        snackbarHostState = snackbarHostState,
    )

    content(interactionSource)

    if (showFirstLaunchSnackbar && iconRequestList.isNotEmpty()) {
        openSnackbarFirstLaunchContent(
            context,
            scope,
            prefs.showFirstLaunchSnackbar::onChange,
            snackbarHostState,
        )
    }

    AnimatedVisibility(visible = sheetExpanded) {
        ModalBottomSheet(
            onDismissRequest = { sheetExpanded = false },
            sheetState = sheetState,
        ) {
            IconRequestSheet(requestList, context)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HandleTouchInteractions(
    interactionSource: MutableInteractionSource,
    viewConfiguration: ViewConfiguration,
    context: Context,
    coroutineScope: CoroutineScope,
    onExpandSheet: (Boolean) -> Unit,
    sheetState: SheetState,
    iconRequestList: List<IconRequest>,
    directLinkEnabled: Boolean,
    requestList: String,
    encodedRequestList: String,
    snackbarHostState: SnackbarHostState,
) {
    val prefs = preferenceManager()
    val lastestOnExpandSheet by rememberUpdatedState(newValue = onExpandSheet)
    LaunchedEffect(interactionSource) {
        var isLongClick = false

        interactionSource.interactions.collectLatest { interaction ->
            when (interaction) {
                is PressInteraction.Press -> {
                    isLongClick = false
                    delay(viewConfiguration.longPressTimeoutMillis)
                    isLongClick = true
                    coroutineScope.launch {
                        lastestOnExpandSheet(true)
                        sheetState.show()
                    }
                }

                is PressInteraction.Release -> {
                    if (!isLongClick) {
                        prefs.showFirstLaunchSnackbar.set(false)
                        handleRequestClick(
                            iconRequestList,
                            context,
                            directLinkEnabled,
                            encodedRequestList,
                            requestList,
                            coroutineScope,
                            snackbarHostState,
                        )
                    }
                }

                is PressInteraction.Cancel -> {
                    isLongClick = false
                }
            }
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

private fun handleRequestClick(
    iconRequestList: List<IconRequest>,
    context: Context,
    directLinkEnabled: Boolean,
    encodedRequestList: String,
    requestList: String,
    coroutineScope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
) {
    if (iconRequestList.isEmpty()) {
        openLink(context, Constants.ICON_REQUEST_FORM)
    } else if (directLinkEnabled) {
        openLink(context, encodedRequestList)
    } else {
        openSnackbarWarningContent(
            context,
            requestList,
            coroutineScope,
            snackbarHostState,
        )
    }
}

private fun openSnackbarFirstLaunchContent(
    context: Context,
    coroutineScope: CoroutineScope,
    onActionPerformed: () -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    coroutineScope.launch {
        val result = snackbarHostState
            .showSnackbar(
                message = context.getString(R.string.snackbar_request_icons_hint),
                duration = SnackbarDuration.Short,
            )
        if (result == SnackbarResult.Dismissed) {
            onActionPerformed()
            snackbarHostState.currentSnackbarData?.dismiss()
        }
    }
}

private fun openSnackbarWarningContent(
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
