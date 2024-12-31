package app.lawnchair.lawnicons.ui.components.home

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.lawnchair.lawnicons.R
import app.lawnchair.lawnicons.model.IconRequestModel
import app.lawnchair.lawnicons.ui.util.Constants

@Composable
fun HomeBottomBar(
    context: Context,
    iconRequestsEnabled: Boolean,
    iconRequestModel: IconRequestModel?,
    snackbarHostState: SnackbarHostState,
    onNavigate: () -> Unit,
    onExpandSearch: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BottomAppBar(
        actions = {
            SimpleTooltipBox(
                label = stringResource(id = R.string.github),
            ) {
                IconButton(
                    onClick = {
                        val webpage = Uri.parse(Constants.GITHUB)
                        val intent = Intent(Intent.ACTION_VIEW, webpage)
                        if (intent.resolveActivity(context.packageManager) != null) {
                            context.startActivity(intent)
                        }
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
        floatingActionButton = {
            SimpleTooltipBox(
                label = stringResource(id = R.string.search),
            ) {
                FloatingActionButton(
                    onClick = onExpandSearch,
                    containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                    elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = stringResource(id = R.string.search),
                    )
                }
            }
        },
        modifier = modifier,
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
        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
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
