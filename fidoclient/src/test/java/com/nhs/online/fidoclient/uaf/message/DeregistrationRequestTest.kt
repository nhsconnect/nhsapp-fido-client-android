package com.nhs.online.fidoclient.uaf.message

import com.google.gson.Gson
import com.nhs.online.fidoclient.uaf.message.DeregistrationRequest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner


@RunWith(RobolectricTestRunner::class)
class DeregistrationRequestTest {

    private val gson = Gson()

    @Test
    fun testDeserialize() {
        val deregistrationRequest =
            gson.fromJson(getTestDeregistrationRequest(), DeregistrationRequest::class.java)

        assertNotNull("Failed to deserialize to a DeregistrationRequest object",
            deregistrationRequest)
        assertEquals(Operation.Dereg.toString(), deregistrationRequest.header?.op)
    }

    private fun getTestDeregistrationRequest(): String {
        return "{\"header\": {\"op\": \"Dereg\",\"upv\": {\"major\": 1,\"minor\": 0},\"appID\": \"https://acem.com\"},\"authenticators\": [{\"aaid\": \"ABCD#ABCD\",\"keyID\": \"ZMCPn92yHv1Ip-iCiBb6i4ADq6ZOv569KFQCvYSJfNg\"}]}"
    }
}