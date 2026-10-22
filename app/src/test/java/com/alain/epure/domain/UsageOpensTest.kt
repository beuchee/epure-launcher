package com.alain.epure.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class UsageOpensTest {

    private fun resume(packageName: String, timeMillis: Long) = ResumeEvent(packageName, timeMillis)

    @Test
    fun singleResumeCountsAsOneOpening() {
        assertEquals(mapOf("a" to 1), UsageOpens.count(listOf(resume("a", 0))))
    }

    @Test
    fun resumesWithinWindowCountAsOneOpening() {
        val events = listOf(resume("a", 0), resume("a", 2_000))
        assertEquals(mapOf("a" to 1), UsageOpens.count(events))
    }

    @Test
    fun resumeAtExactlyTheWindowStillCountsAsSameOpening() {
        val events = listOf(resume("a", 0), resume("a", UsageOpens.GROUP_WINDOW_MILLIS))
        assertEquals(mapOf("a" to 1), UsageOpens.count(events))
    }

    @Test
    fun resumesAfterWindowCountAsSeparateOpenings() {
        val events = listOf(resume("a", 0), resume("a", 6_000), resume("a", 20_000))
        assertEquals(mapOf("a" to 3), UsageOpens.count(events))
    }

    @Test
    fun openingsAreCountedPerApp() {
        val events = listOf(resume("a", 0), resume("b", 1_000), resume("a", 60_000))
        assertEquals(mapOf("a" to 2, "b" to 1), UsageOpens.count(events))
    }

    @Test
    fun unsortedEventsAreReadInTimeOrder() {
        val events = listOf(resume("a", 60_000), resume("a", 0))
        assertEquals(mapOf("a" to 2), UsageOpens.count(events))
    }

    @Test
    fun noEventsGiveNoOpenings() {
        assertEquals(emptyMap<String, Int>(), UsageOpens.count(emptyList()))
    }
}
