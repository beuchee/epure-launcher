package com.alain.epure.data

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.LauncherApps
import android.net.Uri
import android.os.Process
import android.provider.Settings
import com.alain.epure.domain.LaunchableApp

// Lists the launchable apps of the current user profile (D4: work and secure folder are ignored).
class AppRepository(context: Context) {

    private val appContext = context.applicationContext
    private val launcherApps = appContext.getSystemService(LauncherApps::class.java)

    fun loadApps(): List<LaunchableApp> =
        launcherApps.getActivityList(null, Process.myUserHandle())
            .filter { it.componentName.packageName != appContext.packageName }
            .map { LaunchableApp(it.label.toString(), it.componentName.packageName, it.componentName.className) }

    fun launch(app: LaunchableApp) {
        try {
            launcherApps.startMainActivity(
                ComponentName(app.packageName, app.className),
                Process.myUserHandle(),
                null,
                null
            )
        } catch (e: ActivityNotFoundException) {
            // The app was removed while the list was open: nothing to launch.
        } catch (e: SecurityException) {
            // The app can no longer be started by this launcher.
        }
    }

    // Starts a system action (default shortcuts, DEC-0013); a missing app is ignored.
    fun launchSystem(intent: Intent) {
        try {
            appContext.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        } catch (e: ActivityNotFoundException) {
            // No app handles this action on the device.
        }
    }

    // Opens the system page of the app, where its information and uninstall button are (D31).
    fun openAppDetails(app: LaunchableApp) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", app.packageName, null))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            appContext.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // No settings page for this app: nothing to show.
        }
    }
}
