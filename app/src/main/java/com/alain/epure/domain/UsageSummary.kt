package com.alain.epure.domain

// Time and openings of one app today, for the statistics screen (F-3.2).
data class AppUsage(val packageName: String, val totalMillis: Long, val opens: Int)

// Pure statistics logic: no Android dependency, covered by unit tests.
object UsageSummary {

    // Apps with time today, without the excluded ones (Épure and the home apps), longest first.
    fun build(totalMillis: Map<String, Long>, opens: Map<String, Int>, excluded: Set<String>): List<AppUsage> =
        totalMillis
            .filter { (packageName, millis) -> millis > 0L && packageName !in excluded }
            .map { (packageName, millis) -> AppUsage(packageName, millis, opens[packageName] ?: 0) }
            .sortedByDescending { it.totalMillis }

    fun totalMillis(usages: List<AppUsage>): Long = usages.sumOf { it.totalMillis }
}
