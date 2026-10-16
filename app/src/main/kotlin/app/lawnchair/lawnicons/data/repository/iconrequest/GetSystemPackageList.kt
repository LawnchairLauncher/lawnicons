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

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.util.Log
import app.lawnchair.lawnicons.data.model.Component
import app.lawnchair.lawnicons.data.model.IconInfo
import app.lawnchair.lawnicons.data.model.SystemIconInfo

fun Context.getPackagesList(): List<ResolveInfo> {
    return try {
        packageManager.queryIntentActivities(
            Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER),
            PackageManager.GET_RESOLVED_FILTER,
        )
    } catch (e: Exception) {
        listOf()
    }
}

fun Context.getSystemIconInfo(
    lawniconsIconList: List<IconInfo> = emptyList(),
): List<SystemIconInfo> {
    val themedComponentStrings = if (lawniconsIconList.isNotEmpty()) {
        lawniconsIconList
            .flatMap { it.componentNames }
            .mapTo(HashSet()) { it.component.flattenToString() }
    } else {
        null
    }

    return getPackagesList().mapNotNull { ri ->
        with(ri) {
            val riPkg = activityInfo.packageName
            val componentStr = "$riPkg/${activityInfo.name}"
            val component = Component.unflattenFromString(componentStr) ?: return@mapNotNull null

            if (themedComponentStrings != null && component.flattenToString() in themedComponentStrings) {
                return@mapNotNull null
            }

            val name = loadLabel(packageManager).toString()
            val drawable = getHighResIcon(packageManager) ?: loadIcon(packageManager)

            SystemIconInfo(
                drawable = drawable,
                label = name,
                component = component,
            )
        }
    }
}

fun ResolveInfo.getHighResIcon(packageManager: PackageManager): Drawable? {
    return try {
        val componentName =
            ComponentName(activityInfo.packageName, activityInfo.name)
        if (iconResource != 0) {
            return packageManager.getResourcesForActivity(componentName).getDrawable(
                iconResource,
                null,
            )
        }
        null
    } catch (e: PackageManager.NameNotFoundException) {
        Log.d(TAG, "$e")
        null
    } catch (e: Resources.NotFoundException) {
        Log.d(TAG, "$e")
        null
    }
}

private const val TAG = "GetSystemPackageList"
