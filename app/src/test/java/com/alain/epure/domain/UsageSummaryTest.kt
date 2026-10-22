package com.alain.epure.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class UsageSummaryTest {

    @Test
    fun excludedPackagesAreLeftOut() {
        val totals = mapOf("a" to 1_000L, "epure" to 5_000L, "launcher" to 9_000L)
        val result = UsageSummary.build(totals, emptyMap(), setOf("epure", "launcher"))
        assertEquals(listOf("a"), result.map { it.packageName })
    }

    @Test
    fun appsWithoutTimeAreDropped() {
        val totals = mapOf("a" to 0L, "b" to 2_000L)
        val result = UsageSummary.build(totals, emptyMap(), emptySet())
        assertEquals(listOf("b"), result.map { it.packageName })
    }

    @Test
    fun longestUsageComesFirst() {
        val totals = mapOf("a" to 1_000L, "b" to 3_000L, "c" to 2_000L)
        val result = UsageSummary.build(totals, emptyMap(), emptySet())
        assertEquals(listOf("b", "c", "a"), result.map { it.packageName })
    }

    @Test
    fun missingOpeningsCountAsZero() {
        val result = UsageSummary.build(mapOf("a" to 1_000L), emptyMap(), emptySet())
        assertEquals(0, result.single().opens)
    }

    @Test
    fun openingsAreTakenPerApp() {
        val totals = mapOf("a" to 1_000L, "b" to 2_000L)
        val result = UsageSummary.build(totals, mapOf("a" to 3), emptySet())
        assertEquals(mapOf("a" to 3, "b" to 0), result.associate { it.packageName to it.opens })
    }

    @Test
    fun totalIsTheSumOfUsages() {
        val usages = listOf(AppUsage("a", 1_000L, 1), AppUsage("b", 2_500L, 2))
        assertEquals(3_500L, UsageSummary.totalMillis(usages))
    }
}
