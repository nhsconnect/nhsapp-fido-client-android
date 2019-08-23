package com.nhs.online.fidoclient.uaf.message

import com.google.gson.Gson
import com.nhs.online.fidoclient.uaf.message.DeregisterAuthenticator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DeregisterAuthenticatorTest {

    private val gson = Gson()

    @Test
    fun testDeserialize() {
        val deregisterAuth = gson.fromJson(getTestDeregisterAuth(), DeregisterAuthenticator::class.java)

        assertNotNull("Failed to deserialize to a DeregisterAuthenticator object", deregisterAuth)
        assertEquals("ABCD#ABCD", deregisterAuth.aaid)
        assertEquals("ZMCPn92yHv1Ip-iCiBb6i4ADq6ZOv569KFQCvYSJfNg", deregisterAuth.keyID)
    }

    private fun getTestDeregisterAuth(): String {
        return "{\"aaid\": \"ABCD#ABCD\",\"keyID\": \"ZMCPn92yHv1Ip-iCiBb6i4ADq6ZOv569KFQCvYSJfNg\"}"
    }
}