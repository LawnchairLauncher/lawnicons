/*
 * Copyright 2026 Lawnchair Launcher
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

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import app.lawnchair.lawnicons.R
import app.lawnchair.lawnicons.ui.util.PreviewLawnicons
import app.lawnchair.lawnicons.ui.util.PreviewProviders

@Composable
fun NotificationPermissionDialog(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { _ ->
            onDismiss()
        },
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(R.string.notification_permission_label))
        },
        text = {
            Text(text = stringResource(R.string.notification_permission_desc, stringResource(R.string.app_name)))
        },
        confirmButton = {
            Button(
                onClick = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        onDismiss()
                    }
                },
            ) {
                Text(text = stringResource(R.string.notification_permission_grant))
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
            ) {
                Text(text = stringResource(R.string.notification_permission_dismiss))
            }
        },
        modifier = modifier,
    )
}

@PreviewLawnicons
@Composable
private fun NotificationPermissionDialogPreview() {
    PreviewProviders {
        Scaffold {
            Column(
                Modifier.padding(it),
            ) {
                var showDialog by remember {
                    mutableStateOf(false)
                }

                Button(
                    onClick = {
                        showDialog = true
                    },
                ) {
                    Text("Show dialog")
                }

                if (showDialog) {
                    NotificationPermissionDialog(
                        onDismiss = {
                            showDialog = false
                        },
                    )
                }
            }
        }
    }
}
