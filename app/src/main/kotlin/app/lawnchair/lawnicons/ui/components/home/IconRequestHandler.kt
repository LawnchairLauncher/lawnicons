package app.lawnchair.lawnicons.ui.components.home

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.platform.ViewConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.lawnchair.lawnicons.R
import app.lawnchair.lawnicons.model.IconRequest
import app.lawnchair.lawnicons.model.IconRequestModel
import app.lawnchair.lawnicons.repository.preferenceManager
import app.lawnchair.lawnicons.ui.util.Constants
import app.lawnchair.lawnicons.ui.util.copyTextToClipboard
import app.lawnchair.lawnicons.ui.util.isScrollingUp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun IconRequestFAB(
    iconRequestsEnabled: Boolean,
    iconRequestModel: IconRequestModel?,
    lazyGridState: LazyGridState,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    val prefs = preferenceManager()
    val list = iconRequestModel?.list ?: emptyList()
    val enabled = iconRequestModel != null
    val requestsEnabled = iconRequestsEnabled || prefs.forceEnableIconRequest.asState().value

    RequestHandler(
        enabled = enabled,
        iconRequestsEnabled = requestsEnabled,
        iconRequestList = list,
        snackbarHostState = snackbarHostState,
        onLongClick = {},
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
            interactionSource = if (enabled) interactionSource else null,
            containerColor = if (!enabled) MaterialTheme.colorScheme.surfaceVariant else FloatingActionButtonDefaults.containerColor,
            modifier = modifier,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IconRequestIconButton(
    snackbarHostState: SnackbarHostState,
    iconRequestsEnabled: Boolean,
    iconRequestModel: IconRequestModel?,
    modifier: Modifier = Modifier,
) {
    val prefs = preferenceManager()

    val list = iconRequestModel?.list ?: emptyList()
    val enabled = iconRequestModel != null
    val requestsEnabled = iconRequestsEnabled || prefs.forceEnableIconRequest.asState().value

    val tooltipState = rememberTooltipState()
    val scope = rememberCoroutineScope()

    RequestHandler(
        enabled = enabled,
        iconRequestsEnabled = requestsEnabled,
        iconRequestList = list,
        snackbarHostState = snackbarHostState,
        onLongClick = {
            scope.launch {
                tooltipState.show()
            }
        },
    ) { interactionSource ->
        TooltipBox(
            positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
            tooltip = {
                PlainTooltip {
                    Text(stringResource(R.string.request_icons))
                }
            },
            state = tooltipState,
            modifier = modifier,
            enableUserInput = false,
        ) {
            IconButton(
                onClick = {},
                enabled = enabled,
                interactionSource = if (enabled) interactionSource else null,
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.icon_request_app),
                    contentDescription = stringResource(R.string.request_icons),
                    modifier = Modifier.requiredSize(24.dp),
                )
            }
        }
    }
}

@Composable
fun RequestHandler(
    enabled: Boolean,
    iconRequestsEnabled: Boolean,
    iconRequestList: List<IconRequest>,
    snackbarHostState: SnackbarHostState,
    onLongClick: () -> Unit,
    content: @Composable (interactionSource: MutableInteractionSource) -> Unit,
) {
    val prefs = preferenceManager()
    val showFirstLaunchSnackbar by prefs.showFirstLaunchSnackbar.asState()
    val context = LocalContext.current

    val requestList = formatIconRequestList(iconRequestList)
    val encodedRequestList = buildForm(requestList.replace("\n", "%20"))
    val directLinkEnabled = encodedRequestList.length < Constants.DIRECT_LINK_MAX_LENGTH

    val scope = rememberCoroutineScope()
    val interactionSource = remember { MutableInteractionSource() }

    HandleTouchInteractions(
        enabled = enabled,
        iconRequestsEnabled = iconRequestsEnabled,
        interactionSource = interactionSource,
        viewConfiguration = LocalViewConfiguration.current,
        context = context,
        coroutineScope = scope,
        onLongClick = onLongClick,
        iconRequestList = iconRequestList,
        directLinkEnabled = directLinkEnabled,
        requestList = requestList,
        encodedRequestList = encodedRequestList,
        snackbarHostState = snackbarHostState,
    )

    content(interactionSource)

    LaunchedEffect(enabled) {
        if (showFirstLaunchSnackbar && enabled) {
            openSnackbarFirstLaunchContent(
                context,
                scope,
                { prefs.showFirstLaunchSnackbar.set(false) },
                snackbarHostState,
            )
        }
    }
}

@Composable
private fun HandleTouchInteractions(
    enabled: Boolean,
    iconRequestsEnabled: Boolean,
    interactionSource: MutableInteractionSource,
    viewConfiguration: ViewConfiguration,
    context: Context,
    coroutineScope: CoroutineScope,
    onLongClick: () -> Unit,
    iconRequestList: List<IconRequest>,
    directLinkEnabled: Boolean,
    requestList: String,
    encodedRequestList: String,
    snackbarHostState: SnackbarHostState,
) {
    val latestOnClick by rememberUpdatedState(newValue = onLongClick)

    if (enabled) {
        LaunchedEffect(interactionSource) {
            var isLongClick = false

            interactionSource.interactions.collectLatest { interaction ->
                when (interaction) {
                    is PressInteraction.Press -> {
                        isLongClick = false
                        delay(viewConfiguration.longPressTimeoutMillis)
                        isLongClick = true
                        latestOnClick()
                    }

                    is PressInteraction.Release -> {
                        if (!isLongClick) {
                            if (iconRequestsEnabled) {
                                handleRequestClick(
                                    iconRequestList,
                                    context,
                                    directLinkEnabled,
                                    encodedRequestList,
                                    requestList,
                                    coroutineScope,
                                    snackbarHostState,
                                )
                            } else {
                                openSnackbarRequestsDisabled(
                                    context,
                                    coroutineScope,
                                    snackbarHostState,
                                )
                            }
                        }
                    }

                    is PressInteraction.Cancel -> {
                        isLongClick = false
                    }
                }
            }
        }
    }
}

private fun formatIconRequestList(iconRequestList: List<IconRequest>) = iconRequestList.joinToString("\n") { "${it.label}\n${it.componentName}" }

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

private fun openSnackbarRequestsDisabled(
    context: Context,
    coroutineScope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
) {
    coroutineScope.launch {
        val result = snackbarHostState
            .showSnackbar(
                message = context.getString(R.string.icon_requests_suspended),
                duration = SnackbarDuration.Short,
            )
        if (result == SnackbarResult.Dismissed) {
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
    return "https://lawnchair.app/lawnicons-request/?request=$string"
}
