package com.nhs.online.fidoclient.uaf.client.operation

import android.util.Log
import com.nhs.online.fidoclient.constants.AAID
import com.nhs.online.fidoclient.exceptions.GenericFidoException
import com.nhs.online.fidoclient.uaf.message.*
import com.nhs.online.fidoclient.uaf.operationcall.DeRegistrationCall

class DeRegistration() {
    private val deRegistrationCall: DeRegistrationCall = DeRegistrationCall()
    private val TAG = DeRegistration::class.java.simpleName

    fun sendDeRegistrationOperation(appId: String, keyId: String, accessToken: String, deregistrationPostUrl: String) {
        val version = Version(1, 0)
        val operation = Operation.Dereg.toString()
        val requestHeader = OperationHeader(version, operation, appId)
        val deRegAuthenticator = DeregisterAuthenticator(AAID, keyId)

        val deRegistrationRequest = DeregistrationRequest(requestHeader, listOf(deRegAuthenticator))
        try {
            val deRegistrationResponse = deRegistrationCall.post(deRegistrationRequest, deregistrationPostUrl, accessToken)
            Log.d(TAG, "De-registration Response: $deRegistrationResponse")
        } catch (e: Exception) {
            throw GenericFidoException("Failed to send de-registration request.", e)
        }
    }
}