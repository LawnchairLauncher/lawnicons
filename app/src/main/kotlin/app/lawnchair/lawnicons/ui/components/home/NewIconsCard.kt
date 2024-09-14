/*
 * Copyright 2024 Lawnchair Launcher
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

package app.lawnchair.lawnicons.ui.components.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.lawnchair.lawnicons.BuildConfig
import app.lawnchair.lawnicons.R
import app.lawnchair.lawnicons.repository.preferenceManager
import app.lawnchair.lawnicons.ui.theme.LawniconsTheme
import app.lawnchair.lawnicons.ui.util.PreviewLawnicons

@Composable
fun NewIconsCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val prefs = preferenceManager()
    val cardState = prefs.showNewIconsCard.asState()
    NewIconsCard(
        onClick = onClick,
        visible = cardState.value,
        onVisibilityChange = {
            prefs.showNewIconsCard.set(false)
        },
        modifier = modifier,
    )
}

@Composable
fun NewIconsCard(
    onClick: () -> Unit,
    visible: Boolean,
    onVisibilityChange: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(visible) {
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainer,
            shape = MaterialTheme.shapes.extraLarge,
            modifier = modifier
                .padding(horizontal = 8.dp)
                .padding(bottom = 12.dp)
                .fillMaxWidth(),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable { onClick() }
                    .padding(start = 12.dp),
            ) {
                Row {
                    Icon(
                        painterResource(R.drawable.new_releases),
                        contentDescription = null,
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = stringResource(
                            R.string.new_icons_in_version,
                            BuildConfig.VERSION_NAME,
                        ),
                        style = MaterialTheme.typography.titleSmall,
                    )
                }
                Spacer(Modifier.weight(1f))
                IconButton(
                    onClick = onVisibilityChange,
                ) {
                    Icon(Icons.Rounded.Clear, contentDescription = stringResource(R.string.clear))
                }
            }
        }
    }
}

@PreviewLawnicons
@Composable
private fun NewIconsCardPreview() {
    LawniconsTheme {
        Surface {
            NewIconsCard({})
        }
    }
}
