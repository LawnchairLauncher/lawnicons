package app.lawnchair.lawnicons.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import app.lawnchair.lawnicons.model.IconInfo
import app.lawnchair.lawnicons.model.LabelAndComponent

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

fun Context.getSystemIconInfoAppfilter(): List<IconInfo> {
    return getPackagesList().map { ri ->
        with(ri) {
            val riPkg = activityInfo.packageName
            val component = "$riPkg/${activityInfo.name}"

            val name = loadLabel(packageManager) ?: riPkg

            IconInfo(
                drawableName = "",
                componentNames = listOf(
                    LabelAndComponent(name.toString(), component),
                ),
                id = 0,
            )
        }
    }
}
