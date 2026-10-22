package com.alain.epure.domain

// One "activity resumed" event read from the system usage log.
data class ResumeEvent(val packageName: String, val timeMillis: Long)

// Counts app openings from resume events (F-3.2, DEC-0016). Pure logic: no Android dependency.
object UsageOpens {

    // Resumes of the same app closer together than this window count as one opening.
    const val GROUP_WINDOW_MILLIS = 5_000L

    // Each resume is compared with the previous resume of the same app, so events are read in time order.
    fun count(events: List<ResumeEvent>, groupWindowMillis: Long = GROUP_WINDOW_MILLIS): Map<String, Int> {
        val lastResume = mutableMapOf<String, Long>()
        val opens = mutableMapOf<String, Int>()
        events.sortedBy { it.timeMillis }.forEach { event ->
            val last = lastResume[event.packageName]
            if (last == null || event.timeMillis - last > groupWindowMillis) {
                opens[event.packageName] = (opens[event.packageName] ?: 0) + 1
            }
            lastResume[event.packageName] = event.timeMillis
        }
        return opens
    }
}
