package com.alain.epure.system

import android.app.AppOpsManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Process
import com.alain.epure.domain.ResumeEvent
import com.alain.epure.domain.UsageOpens
import java.time.LocalDate
import java.time.ZoneId

// Totals and openings of the day, as read from the system (F-3.2).
data class TodayUsage(val totalMillis: Map<String, Long>, val opens: Map<String, Int>)

// Reads the system usage data. Called on request only, never in the background (SPEC § 9).
class UsageAccess(context: Context) {

    private val appContext = context.applicationContext
    private val appOps = appContext.getSystemService(AppOpsManager::class.java)
    private val usageStats = appContext.getSystemService(UsageStatsManager::class.java)

    // True when the user has allowed Épure to read usage data (« Octroi d'autorisation »).
    // The unchecked variant is the only check for this app op on minSdk 31; it is deprecated but still works.
    @Suppress("DEPRECATION")
    fun isGranted(): Boolean {
        val mode = appOps.unsafeCheckOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(),
            appContext.packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    // Time in foreground and openings per app, from local midnight until now.
    fun readToday(): TodayUsage {
        val now = System.currentTimeMillis()
        val start = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

        val totals = usageStats.queryAndAggregateUsageStats(start, now)
            .mapValues { it.value.totalTimeInForeground }

        val resumes = mutableListOf<ResumeEvent>()
        val events = usageStats.queryEvents(start, now)
        val event = UsageEvents.Event()
        while (events.hasNextEvent()) {
            events.getNextEvent(event)
            if (event.eventType == UsageEvents.Event.ACTIVITY_RESUMED) {
                resumes += ResumeEvent(event.packageName, event.timeStamp)
            }
        }
        return TodayUsage(totals, UsageOpens.count(resumes))
    }

    // Épure and the home apps (the system launcher) are left out of the statistics (F-3.2).
    fun excludedPackages(): Set<String> {
        val home = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)
        val launchers = appContext.packageManager.queryIntentActivities(home, PackageManager.MATCH_ALL)
            .map { it.activityInfo.packageName }
        return (launchers + appContext.packageName).toSet()
    }
}
