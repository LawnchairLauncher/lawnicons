package app.lawnchair.lawnicons.ui.destination

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
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
import app.lawnchair.lawnicons.ui.component.*
import app.lawnchair.lawnicons.ui.util.Contributor
import app.lawnchair.lawnicons.ui.util.Destinations
import app.lawnchair.lawnicons.util.appIcon

private val coreContributors = listOf(
    Contributor(
        name = "paphonb",
        username = "paphonb",
        photoUrl = "https://avatars.githubusercontent.com/u/8080853",
        socialUrl = "https://twitter.com/paphonb",
    ),
    Contributor(
        name = "Patryk Michalik",
        username = "patrykmichalik",
        photoUrl = "https://raw.githubusercontent.com/patrykmichalik/brand/master/logo-on-indigo.png",
        socialUrl = "https://patrykmichalik.com",
    ),
)

private val specialThanks = listOf(
    Contributor(
        name = "Eatos",
        photoUrl = "https://avatars.githubusercontent.com/u/52837599",
        socialUrl = "https://twitter.com/eatosapps",
        descriptionRes = R.string.special_thanks_icon,
    ),
    Contributor(
        name = "Rik Koedoot",
        photoUrl = "https://avatars.githubusercontent.com/u/29402532",
        username = "rikkoedoot",
        descriptionRes = R.string.special_thanks_name,
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
                        .padding(
                            top = 8.dp,
                            bottom = 32.dp,
                        ),
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
                        modifier = Modifier.padding(top = 12.dp),
                    )
                    Text(
                        text = stringResource(id = R.string.version_x, BuildConfig.VERSION_NAME),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = ContentAlpha.medium),
                    )
                }
            }
            item {
                Card(label = stringResource(id = R.string.core_contributors)) {
                    coreContributors.mapIndexed { index, it ->
                        ContributorRow(
                            name = it.name,
                            photoUrl = it.photoUrl,
                            profileUrl = "https://github.com/${it.username}",
                            divider = index != coreContributors.lastIndex,
                        )
                    }
                }
            }
            item {
                Card(modifier = Modifier.padding(top = 16.dp)) {
                    SimpleListRow(
                        onClick = { navController.navigate(Destinations.CONTRIBUTORS) },
                        label = stringResource(id = R.string.see_all_contributors),
                        divider = false,
                    )
                }
            }
            item {
                Card(
                    label = stringResource(id = R.string.special_thanks),
                    modifier = Modifier.padding(top = 16.dp),
                ) {
                    specialThanks.mapIndexed { index, it ->
                        ContributorRow(
                            name = it.name,
                            photoUrl = it.photoUrl,
                            profileUrl = it.username?.let { "https://github.com/${it}" },
                            description = it.descriptionRes?.let { stringResource(id = it) },
                            divider = index != specialThanks.lastIndex,
                            socialUrl = it.socialUrl,
                        )
                    }
                }
            }
        }
    }
}
