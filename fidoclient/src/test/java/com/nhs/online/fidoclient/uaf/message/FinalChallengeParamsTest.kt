package com.nhs.online.fidoclient.uaf.message

import android.util.Base64
import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class FinalChallengeParamsTest {

    private val gson = Gson()

    @Test
    fun deserializeFinalParams() {
        val fcParamsAsJson = String(
                Base64.decode(getTestfcParamsAsBase64(), Base64.DEFAULT))
        val fcParams = gson.fromJson(fcParamsAsJson, FinalChallengeParams::class.java)

        assertNotNull(fcParamsAsJson)
        assertEquals("H9iW9yA9aAXF_lelQoi_DhUk514Ad8Tqv0zCnCqKDpo", fcParams.challenge)
    }

    private fun getTestfcParamsAsBase64(): String {
        return "eyJhcHBJRCI6Imh0dHBzOi8vdWFmLXRlc3QtMS5ub2tub2t0ZXN0LmNvbTo4NDQzL1NhbXBsZUFwcC91YWYvZmFjZXRzIiwiY2hhbGxlbmdlIjoiSDlpVzl5QTlhQVhGX2xlbFFvaV9EaFVrNTE0QWQ4VHF2MHpDbkNxS0RwbyIsImNoYW5uZWxCaW5kaW5nIjp7fSwiZmFjZXRJRCI6ImNvbS5ub2tub2suYW5kcm9pZC5zYW1wbGVhcHAifQ"
    }
}