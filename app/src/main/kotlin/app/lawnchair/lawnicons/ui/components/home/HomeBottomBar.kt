package app.lawnchair.lawnicons.ui.components.home

import android.content.Context
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.FloatingToolbarDefaults.ScreenOffset
import androidx.compose.material3.FloatingToolbarScrollBehavior
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults.rememberTooltipPositionProvider
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.lawnchair.lawnicons.R
import app.lawnchair.lawnicons.model.IconRequestModel
import app.lawnchair.lawnicons.ui.util.Constants
import app.lawnchair.lawnicons.ui.util.visitUrl

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun BoxScope.HomeBottomToolbar(
    context: Context,
    iconRequestsEnabled: Boolean,
    iconRequestModel: IconRequestModel?,
    snackbarHostState: SnackbarHostState,
    onNavigate: () -> Unit,
    onExpandSearch: () -> Unit,
    scrollBehavior: FloatingToolbarScrollBehavior,
    modifier: Modifier = Modifier,
) {
    HorizontalFloatingToolbar(
        expanded = true,
        scrollBehavior = scrollBehavior,
        floatingActionButton = {
            SimpleTooltipBox(
                label = stringResource(id = R.string.search),
            ) {
                FloatingToolbarDefaults.StandardFloatingActionButton(
                    onClick = onExpandSearch,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = stringResource(id = R.string.search),
                    )
                }
            }
        },
        content = {
            SimpleTooltipBox(
                label = stringResource(id = R.string.github),
            ) {
                IconButton(
                    onClick = {
                        context.visitUrl(Constants.GITHUB)
                    },
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.github_foreground),
                        contentDescription = stringResource(id = R.string.github),
                        modifier = Modifier.requiredSize(24.dp),
                    )
                }
            }

            IconRequestIconButton(
                snackbarHostState = snackbarHostState,
                iconRequestsEnabled = iconRequestsEnabled,
                iconRequestModel = iconRequestModel,
            )

            SimpleTooltipBox(
                label = stringResource(id = R.string.about),
            ) {
                IconButton(onClick = onNavigate) {
                    Icon(
                        painter = painterResource(id = R.drawable.about_icon),
                        contentDescription = stringResource(id = R.string.about),
                        modifier = Modifier.requiredSize(24.dp),
                    )
                }
            }
        },
        modifier = modifier
            .align(Alignment.BottomCenter)
            .offset(y = -ScreenOffset)
            .navigationBarsPadding(),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SimpleTooltipBox(
    label: String,
    modifier: Modifier = Modifier,
    content: @Composable (() -> Unit),
) {
    TooltipBox(
        positionProvider = rememberTooltipPositionProvider(),
        tooltip = {
            PlainTooltip {
                Text(label)
            }
        },
        state = rememberTooltipState(),
        modifier = modifier,
    ) {
        content()
    }
}
