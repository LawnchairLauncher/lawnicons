package app.lawnchair.lawnicons.ui.destination

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ContentAlpha
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import app.lawnchair.lawnicons.BuildConfig
import app.lawnchair.lawnicons.R
import app.lawnchair.lawnicons.ui.component.ClickableIcon
import app.lawnchair.lawnicons.ui.component.TopBarWithInsets
import app.lawnchair.lawnicons.util.appIcon

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun About(navController: NavController) {
    val scrollBehavior = remember { TopAppBarDefaults.pinnedScrollBehavior() }
    val context = LocalContext.current

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopBarWithInsets(
                scrollBehavior = scrollBehavior,
                title = stringResource(id = R.string.about),
                navigationIcon = {
                    ClickableIcon(
                        onClick = { navController.popBackStack() },
                        imageVector = Icons.Rounded.ArrowBack,
                        size = 40.dp,
                        modifier = Modifier.padding(horizontal = 4.dp),
                    )
                },
            )
        }
    ) { paddingValues ->
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxWidth(),
        ) {
            item {
                Image(
                    bitmap = context.appIcon().asImageBitmap(),
                    contentDescription = stringResource(id = R.string.app_name),
                    modifier = Modifier.size(72.dp),
                )
                Text(
                    text = stringResource(id = R.string.app_name),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(top = 16.dp),
                )
                Text(
                    text = stringResource(id = R.string.version_x, BuildConfig.VERSION_NAME),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = ContentAlpha.medium),
                )
            }
        }
    }
}
