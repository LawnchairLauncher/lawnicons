package app.lawnchair.lawnicons.ui.destination

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ContentAlpha
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.lawnchair.lawnicons.BuildConfig
import app.lawnchair.lawnicons.R
import app.lawnchair.lawnicons.ui.components.ContributorRow
import app.lawnchair.lawnicons.ui.components.ExternalLinkRow
import app.lawnchair.lawnicons.ui.components.core.Card
import app.lawnchair.lawnicons.ui.components.core.LawniconsScaffold
import app.lawnchair.lawnicons.ui.components.core.SimpleListRow
import app.lawnchair.lawnicons.ui.theme.LawniconsTheme
import app.lawnchair.lawnicons.ui.util.Contributor
import app.lawnchair.lawnicons.ui.util.Destinations
import app.lawnchair.lawnicons.ui.util.ExternalLink
import app.lawnchair.lawnicons.ui.util.PreviewLawnicons
import app.lawnchair.lawnicons.util.appIcon

private val externalLinks = listOf(
    ExternalLink(
        name = R.string.github,
        url = "https://github.com/LawnchairLauncher/lawnicons",
    ),
    ExternalLink(
        name = R.string.request_form,
        url = "https://forms.gle/xt7sJhgWEasuo9TR9",
    ),
)

private val coreContributors = listOf(
    Contributor(
        name = "paphonb",
        username = "paphonb",
        photoUrl = "https://avatars.githubusercontent.com/u/8080853",
        socialUrl = "https://x.com/paphonb",
    ),
)

private val specialThanks = listOf(
    Contributor(
        name = "Eatos",
        photoUrl = "https://avatars.githubusercontent.com/u/52837599",
        socialUrl = "https://x.com/eatosapps",
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
fun About(onBack: () -> Unit, onNavigate: (String) -> Unit, isExpandedScreen: Boolean) {
    val context = LocalContext.current

    LawniconsScaffold(
        title = stringResource(id = R.string.about),
        onBack = onBack,
        isExpandedScreen = isExpandedScreen,
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
                    if (LocalInspectionMode.current) {
                        Icon(Icons.Rounded.Star, contentDescription = null, modifier = Modifier.size(72.dp))
                    } else {
                        Image(
                            bitmap = context.appIcon().asImageBitmap(),
                            contentDescription = stringResource(id = R.string.app_name),
                            modifier = Modifier.size(72.dp),
                        )
                    }
                    Text(
                        text = stringResource(id = R.string.app_name),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(top = 12.dp),
                    )
                    Text(
                        text = stringResource(id = R.string.version_x, BuildConfig.VERSION_NAME),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground.copy(
                            alpha = ContentAlpha.medium,
                        ),
                    )
                }
            }
            item {
                Card(label = stringResource(id = R.string.external_links)) {
                    externalLinks.mapIndexed { index, it ->
                        ExternalLinkRow(
                            name = stringResource(id = it.name),
                            url = it.url,
                            divider = index != externalLinks.lastIndex,
                        )
                    }
                }
            }
            item {
                Card(label = stringResource(id = R.string.core_contributors), modifier = Modifier.padding(top = 16.dp)) {
                    coreContributors.mapIndexed { index, it ->
                        ContributorRow(
                            name = it.name,
                            photoUrl = it.photoUrl,
                            profileUrl = it.socialUrl,
                            divider = index != coreContributors.lastIndex,
                        )
                    }
                }
            }
            item {
                Card(modifier = Modifier.padding(top = 16.dp)) {
                    SimpleListRow(
                        onClick = { onNavigate(Destinations.CONTRIBUTORS) },
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
                            profileUrl = it.username?.let { "https://github.com/$it" },
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

@PreviewLawnicons
@Composable
private fun AboutPreview() {
    LawniconsTheme {
        About(
            {},
            {},
            false,
        )
    }
}

@PreviewLawnicons
@Composable
private fun AboutPreviewExpanded() {
    LawniconsTheme {
        About(
            {},
            {},
            true,
        )
    }
}
