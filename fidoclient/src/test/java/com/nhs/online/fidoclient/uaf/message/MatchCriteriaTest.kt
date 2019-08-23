package com.nhs.online.fidoclient.uaf.message

import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MatchCriteriaTest {

    private val gson = Gson()

    @Test
    fun deserializeMatchCriteria() {
        val matchCriteria = gson.fromJson(getTestMatchCriteria(), MatchCriteria::class.java)

        assertNotNull("Failed to deserialize to a MatchCriteria object", matchCriteria)
        assertEquals(2, matchCriteria.authenticatorVersion)
        assertEquals("1234#5678", matchCriteria.aaid!![0])
    }

    private fun getTestMatchCriteria(): String {
        return "{\"aaid\": [\"1234#5678\"], \"vendorID\": [\"1234\"], \"userVerificationDetails\": [ [ { \"userVerification\": 2, \"baDesc\": { \"FAR\": 0.001 } } ] ], \"keyProtection\": 6, \"matcherProtection\": 2, \"attachmentHint\": 1, \"tcDisplay\": 4, \"authenticationAlgorithms\": [1], \"assertionScheme\": \"UAFV1TLV\", \"attestationTypes\": [15879], \"authenticatorVersion\": 2 }"
    }

}
