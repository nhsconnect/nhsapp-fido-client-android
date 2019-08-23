package com.nhs.online.fidoclient.uaf.message

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TrustedFacetsTest {
    private val trustedFacets = TrustedFacets()

    @Test
    fun trustedFacetsTest() {
        val ids = listOf("id1", "id2", "id3")
        val version = Version(0, 1)
        trustedFacets.ids = ids
        trustedFacets.version = version

        assertEquals(version, trustedFacets.version)
        assertTrue(ids === (trustedFacets.ids))
    }

}