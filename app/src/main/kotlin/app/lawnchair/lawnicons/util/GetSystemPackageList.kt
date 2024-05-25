package app.lawnchair.lawnicons.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import app.lawnchair.lawnicons.model.IconInfoAppfilter

fun Context.getPackagesList(): List<ResolveInfo> {
    val packagesList = try {
        packageManager.queryIntentActivities(
            Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER),
            PackageManager.GET_RESOLVED_FILTER,
        ) + packageManager.queryIntentActivities(
            Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_DEFAULT),
            PackageManager.GET_RESOLVED_FILTER,
        ) + packageManager.queryIntentActivities(
            Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME),
            PackageManager.GET_RESOLVED_FILTER,
        )
    } catch (e: Exception) {
        listOf()
    }
    return packagesList
}

fun Context.getSystemIconInfoAppfilter(): List<IconInfoAppfilter> {
    val resolveInfo = getPackagesList()
    val list: MutableList<IconInfoAppfilter> = mutableListOf()

    for (ri in resolveInfo) {
        val riPkg = ri.activityInfo.packageName
        val component = riPkg + "/" + ri.activityInfo.name

        val name: CharSequence? = try {
            ri.loadLabel(packageManager)
        } catch (e: Exception) {
            riPkg
        }

        val iconInfo = IconInfoAppfilter(
            name = name.toString(),
            componentName = component,
            id = 0,
            drawableName = "",
        )

        list.add(iconInfo)
    }

    return list
}
