package app.lawnchair.lawnicons.ui.destination

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ContentAlpha
import androidx.compose.material.ListItem
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
import app.lawnchair.lawnicons.ui.component.ContributorRow
import app.lawnchair.lawnicons.ui.component.TopBarWithInsets
import app.lawnchair.lawnicons.ui.util.Contributor
import app.lawnchair.lawnicons.util.appIcon

private val coreContributors = listOf(
    Contributor(
        name = "paphonb",
        photoUrl = "https://avatars.githubusercontent.com/u/8080853",
        socialUrl = "https://twitter.com/paphonb",
    ),
    Contributor(
        name = "Patryk Michalik",
        photoUrl = "https://raw.githubusercontent.com/patrykmichalik/brand/master/logo-on-indigo.png",
        socialUrl = "https://patrykmichalik.com",
    ),
)

@Composable
@OptIn(ExperimentalMaterial3Api::class, androidx.compose.material.ExperimentalMaterialApi::class)
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
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                ) {
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
            item {
                SectionLabel(
                    text = stringResource(id = R.string.core_contributors),
                    modifier = Modifier.padding(top = 32.dp),
                )
            }
            items(items = coreContributors) {
                ContributorRow(
                    name = it.name,
                    photoUrl = it.photoUrl,
                )
            }
            item {
                ListItem(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .clickable {
                            val website = Uri.parse("https://github.com/LawnchairLauncher/lawnicons/graphs/contributors")
                            val intent = Intent(Intent.ACTION_VIEW, website)
                            context.startActivity(intent)
                        }
                ) {
                    Text(text = stringResource(id = R.string.see_all_contributors))
                }
            }
        }
    }
}

@Composable
private fun SectionLabel(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier.padding(start = 16.dp, bottom = 8.dp),
    )
}
