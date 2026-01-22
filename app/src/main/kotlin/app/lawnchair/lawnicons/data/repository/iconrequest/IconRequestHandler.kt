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

package app.lawnchair.lawnicons.data.repository.iconrequest

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.FileProvider
import app.lawnchair.lawnicons.BuildConfig
import app.lawnchair.lawnicons.LawniconsScope
import app.lawnchair.lawnicons.data.model.IconRequestData
import app.lawnchair.lawnicons.ui.util.Constants
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

interface IconRequestHandler {
    fun execute(context: Context, requestData: IconRequestData)
}

@SingleIn(LawniconsScope::class)
@ContributesBinding(LawniconsScope::class)
@Inject
class ArcticonsDashboardHandler constructor() : IconRequestHandler {
    override fun execute(context: Context, requestData: IconRequestData) {
        val authority = "${context.packageName}.fileprovider"
        val uri = FileProvider.getUriForFile(context, authority, requestData.zipFile)

        // 1. Build the email body
        val deviceInfo = getDeviceInfoForEmail()
        val emailBody = "$deviceInfo\n\n${requestData.componentListString}"

        // 2. Create the email intent with all the data
        val emailIntent = Intent(Intent.ACTION_SEND).apply {
            type = "*/*"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(Constants.ICON_REQUEST_EMAIL))
            putExtra(Intent.EXTRA_SUBJECT, "Lawnicons icon request")
            putExtra(Intent.EXTRA_TEXT, emailBody)
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        emailIntent.setPackage("com.google.android.gm")

        // Check if Gmail is installed and can handle the intent
        if (emailIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(emailIntent)
        } else {
            emailIntent.setPackage(null)
            context.startActivity(
                Intent.createChooser(
                    emailIntent,
                    "Send request...",
                ),
            )
        }
    }
}

private fun getDeviceInfoForEmail(): String {
    return """
        Manufacturer: ${Build.MANUFACTURER}
        Model: ${Build.MODEL}
        Product: ${Build.PRODUCT}
        App version: ${BuildConfig.VERSION_NAME}
    """.trimIndent()
}
