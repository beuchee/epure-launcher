package com.alain.epure.system

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

// Opens Android system screens and web pages. Épure itself has no INTERNET permission:
// links are opened by the browser, on the user's request.
object SystemSettings {

    // Android does not let an app change its own home app: the user picks it on the screen opened here.
    fun openHomeSettings(context: Context) {
        try {
            start(context, Intent(Settings.ACTION_HOME_SETTINGS))
        } catch (e: ActivityNotFoundException) {
            start(context, Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS))
        }
    }

    fun openUrl(context: Context, url: String) {
        start(context, Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }

    private fun start(context: Context, intent: Intent) {
        try {
            context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        } catch (e: ActivityNotFoundException) {
            // No screen or browser available on this device: nothing to open.
        }
    }
}
