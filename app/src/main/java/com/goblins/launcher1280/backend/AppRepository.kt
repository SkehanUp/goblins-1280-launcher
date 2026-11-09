package com.goblins.launcher1280.backend

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

data class InstalledApp(
    val label: String,
    val packageName: String,
    val activityName: String,
    val t9Digits: String,
)

class AppRepository(private val context: Context) {
    fun loadLaunchableApps(t9Encoder: (String) -> String): List<InstalledApp> {
        val packageManager = context.packageManager
        val launcherIntent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)

        return packageManager.queryIntentActivities(launcherIntent, 0)
            .asSequence()
            .mapNotNull { resolveInfo ->
                val activityInfo = resolveInfo.activityInfo ?: return@mapNotNull null
                if (activityInfo.packageName == context.packageName) return@mapNotNull null

                val label = resolveInfo.loadLabel(packageManager)?.toString().orEmpty()
                InstalledApp(
                    label = label,
                    packageName = activityInfo.packageName,
                    activityName = activityInfo.name,
                    t9Digits = t9Encoder(label),
                )
            }
            .sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.label })
            .toList()
    }

    fun launchApp(packageName: String): Boolean = runCatching {
        val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
            ?: return false
        context.startActivity(launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        true
    }.getOrDefault(false)

    fun openAppDetails(packageName: String) {
        runCatching {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                .setData(Uri.fromParts("package", packageName, null))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    fun openDialer(phoneNumber: String) {
        runCatching {
            val intent = Intent(
                Intent.ACTION_DIAL,
                Uri.parse("tel:" + Uri.encode(phoneNumber)),
            ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    fun openDefaultHomeSettings() {
        runCatching {
            context.startActivity(
                Intent(Settings.ACTION_HOME_SETTINGS)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
            )
        }
    }
}
