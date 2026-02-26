/*
 * Copyright 2025 Lawnchair Launcher
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package app.lawnchair.lawnicons.ui.destination.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import app.lawnchair.lawnicons.BuildConfig
import app.lawnchair.lawnicons.R
import app.lawnchair.lawnicons.ui.components.ContributorRow
import app.lawnchair.lawnicons.ui.components.core.LawniconsScaffold
import app.lawnchair.lawnicons.ui.components.core.SimpleListRow
import app.lawnchair.lawnicons.ui.theme.icon.AppIcon
import app.lawnchair.lawnicons.ui.theme.icon.Check
import app.lawnchair.lawnicons.ui.theme.icon.LawnIcons
import app.lawnchair.lawnicons.ui.util.PreviewLawnicons
import app.lawnchair.lawnicons.ui.util.PreviewProviders
import kotlinx.serialization.Serializable

enum class ColumnTypes {
    SPACER,
    HEADER,
    NAVIGATION_ITEM,
    LIST_ITEM,
}

@Serializable
data object About : NavKey

fun EntryProviderScope<NavKey>.aboutDestination(
    onBack: () -> Unit,
    onNavigateToContributors: () -> Unit,
    onNavigateToAcknowledgements: () -> Unit,
    isExpandedScreen: Boolean,
) {
    entry<About> {
        About(
            onBack = onBack,
            onNavigateToContributors = onNavigateToContributors,
            onNavigateToAcknowledgements = onNavigateToAcknowledgements,
            isExpandedScreen = isExpandedScreen,
        )
    }
}

@Composable
private fun About(
    onBack: () -> Unit,
    onNavigateToContributors: () -> Unit,
    onNavigateToAcknowledgements: () -> Unit,
    isExpandedScreen: Boolean,
    modifier: Modifier = Modifier,
) {
    LawniconsScaffold(
        modifier = modifier,
        title = stringResource(id = R.string.about),
        onBack = onBack,
        isExpandedScreen = isExpandedScreen,
    ) { paddingValues ->
        val layoutDirection = LocalLayoutDirection.current
        val verticalListPadding = 8.dp
        LazyColumn(
            contentPadding = PaddingValues(
                start = paddingValues.calculateStartPadding(layoutDirection),
                top = paddingValues.calculateTopPadding() + verticalListPadding,
                end = paddingValues.calculateEndPadding(layoutDirection),
                bottom = paddingValues.calculateBottomPadding() + verticalListPadding,
            ),
        ) {
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
                        Icon(
                            imageVector = LawnIcons.Check,
                            contentDescription = null,
                            modifier = Modifier.size(72.dp),
                        )
                    } else {
                        Image(
                            imageVector = LawnIcons.AppIcon,
                            contentDescription = stringResource(id = R.string.app_name),
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape),
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
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            item(contentType = ColumnTypes.HEADER) {
                ListHeader(stringResource(id = R.string.core_contributors))
            }
            itemsIndexed(
                coreContributors,
                contentType = { _, _ -> ColumnTypes.LIST_ITEM },
            ) { index, it ->
                ContributorRow(
                    name = it.name,
                    photoUrl = it.photoUrl,
                    profileUrl = it.socialUrl,
                    divider = index != coreContributors.lastIndex,
                    description = it.descriptionRes?.let { stringResource(id = it) },
                    shapes = ListItemDefaults.segmentedShapes(index, coreContributors.size),
                )
            }
            item(contentType = ColumnTypes.SPACER) {
                Spacer(Modifier.height(8.dp))
            }
            item(contentType = ColumnTypes.NAVIGATION_ITEM) {
                SimpleListRow(
                    label = stringResource(id = R.string.see_all_contributors),
                    background = true,
                    onClick = onNavigateToContributors,
                )
            }
            item(contentType = ColumnTypes.SPACER) {
                Spacer(Modifier.height(16.dp))
            }
            item(contentType = ColumnTypes.HEADER) {
                ListHeader(stringResource(id = R.string.special_thanks))
            }
            itemsIndexed(
                specialThanks,
                contentType = { _, _ -> ColumnTypes.LIST_ITEM },
            ) { index, it ->
                ContributorRow(
                    name = it.name,
                    photoUrl = it.photoUrl,
                    profileUrl = it.username?.let { "https://github.com/$it" },
                    description = it.descriptionRes?.let { stringResource(id = it) },
                    divider = index != specialThanks.lastIndex,
                    socialUrl = it.socialUrl,
                    shapes = ListItemDefaults.segmentedShapes(index, specialThanks.size),
                )
            }
            item(contentType = ColumnTypes.SPACER) {
                Spacer(Modifier.height(8.dp))
            }
            item(contentType = ColumnTypes.NAVIGATION_ITEM) {
                SimpleListRow(
                    label = stringResource(id = R.string.acknowledgements),
                    background = true,
                    onClick = onNavigateToAcknowledgements,
                )
            }
        }
    }
}

@Composable
private fun ListHeader(label: String, modifier: Modifier = Modifier) {
    Text(
        text = label,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier.padding(start = 32.dp, bottom = 8.dp),
    )
}

private val coreContributors = listOf(
    Contributor(
        id = 8080853,
        name = "Suphon T.",
        username = "paphonb",
        socialUrl = "https://x.com/paphonb",
        descriptionRes = R.string.contribution_core,
    ),
    Contributor(
        id = 70206496,
        name = "SuperDragonXD",
        username = "SuperDragonXD",
        socialUrl = "https://github.com/SuperDragonXD",
        descriptionRes = R.string.contribution_core,
    ),
    Contributor(
        id = 100310118,
        name = "Patryk Radziszewski",
        username = "Chefski",
        socialUrl = "https://github.com/Chefski",
        descriptionRes = R.string.contribution_icons,
    ),
    Contributor(
        id = 60105060,
        name = "Gleb",
        username = "x9136",
        socialUrl = "https://github.com/x9136",
        descriptionRes = R.string.contribution_icons,
    ),
    Contributor(
        id = 49114212,
        name = "Grabster",
        username = "Grabstertv",
        socialUrl = "https://x.com/grabstertv",
        descriptionRes = R.string.contribution_icons,
    ),
    Contributor(
        id = 10363352,
        name = "Zongle Wang",
        username = "Goooler",
        socialUrl = "https://github.com/Goooler",
        descriptionRes = R.string.contribution_infra,
    ),
)

private val specialThanks = listOf(
    Contributor(
        id = 52837599,
        name = "Eatos",
        socialUrl = "https://x.com/eatosapps",
        descriptionRes = R.string.special_thanks_icon,
    ),
    Contributor(
        id = 29402532,
        name = "Rik Koedoot",
        username = "rikkoedoot",
        descriptionRes = R.string.special_thanks_name,
    ),
)

val coreContributorIds = coreContributors.map { it.id } + listOf(
    29139614, // Remove Patryk from contributors list, as per https://t.me/lawnchairci/1557
    56888459, // Renovate bot
    41898282, // GitHub Actions bot
    198982749, // Copilot bot
    175728472, // Copilot bot
)

@PreviewLawnicons
@Composable
private fun AboutPreview() {
    PreviewProviders {
        About(
            {},
            {},
            {},
            false,
        )
    }
}

@PreviewLawnicons
@Composable
private fun AboutPreviewExpanded() {
    PreviewProviders {
        About(
            {},
            {},
            {},
            true,
        )
    }
}
