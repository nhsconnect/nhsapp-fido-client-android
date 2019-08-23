/*
 * Copyright 2015 eBay Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Modified heavily (including conversion to Kotlin) by NHS App
 */

package com.nhs.online.fidoclient.uaf.client.operation

import android.annotation.SuppressLint
import android.util.Log
import com.google.gson.GsonBuilder
import com.nhs.online.fidoclient.constants.UAF_AUTH_RESPONSE_FIELD
import com.nhs.online.fidoclient.exceptions.FidoAssertionException
import com.nhs.online.fidoclient.exceptions.FidoInvalidSignatureException
import com.nhs.online.fidoclient.exceptions.GenericFidoException
import com.nhs.online.fidoclient.uaf.client.AuthAssertionBuilder
import com.nhs.online.fidoclient.uaf.crypto.Base64url
import com.nhs.online.fidoclient.uaf.message.*
import com.nhs.online.fidoclient.uaf.operationcall.AuthenticationCall
import com.nhs.online.fidoclient.utils.extractJSONString

class Authentication(
    private val auth: AuthenticationCall = AuthenticationCall()
) {
    private val TAG = Authentication::class.java.simpleName
    private val gson = GsonBuilder().disableHtmlEscaping().create()

    private val defaultScheme = "UAFV1TLV"

    @Throws(GenericFidoException::class, FidoInvalidSignatureException::class)
    fun auth(
        uafMessage: String,
        assertionBuilder: AuthAssertionBuilder
    ): String {
        Log.d(TAG, "[UAF] Auth")
        try {
            Log.d(TAG, "  [UAF] Auth - priv key retrieved")
            val regResponse = processRequest(getAuthRequest(uafMessage), assertionBuilder)
            Log.d(TAG, "  [UAF] Auth - Authentication Response Formed  ")
            Log.d(TAG, regResponse.assertions[0]!!.assertion)
            Log.d(TAG, "  [UAF] Auth - done  ")
            val registrationResponses = arrayOf(regResponse)
            return getUafProtocolMessage(gson.toJson(registrationResponses))
        } catch (e: FidoInvalidSignatureException) {
            throw FidoInvalidSignatureException("Biometric authentication revoked.", e)
        } catch (e: GenericFidoException) {
            throw GenericFidoException("Failed to process auth request.", e)
        }

    }

    fun processRequest(
            request: AuthenticationRequest,
            assertionBuilder: AuthAssertionBuilder
    ): AuthenticationResponse {
        val requestHeader = request.header ?: OperationHeader()
        val response = AuthenticationResponse(requestHeader)
        val fcParams = FinalChallengeParams(requestHeader.appID, request.challenge, "")
        response.fcParams = Base64url.encodeToString(gson.toJson(fcParams).toByteArray())
        setAssertions(response, assertionBuilder)
        return response
    }


    private fun setAssertions(response: AuthenticationResponse, builder: AuthAssertionBuilder) {
        try {
            val assertion = builder.getAssertions(response)
            val authSignAssertion =
                AuthenticatorSignAssertion(assertionScheme = defaultScheme, assertion = assertion)
            response.assertions = listOf(authSignAssertion)

        } catch (e: FidoInvalidSignatureException) {
            throw FidoInvalidSignatureException("Biometrics signature invalid", e)
        } catch (e: Exception) {
            throw FidoAssertionException("Failed to sign authentication assertions", e)
        }

    }

    @SuppressLint("PackageManagerGetSignatures") // This vulnerability can only be exploited in Android version 4.4 and below. This is below the minimum supported version of the app
    fun requestUafAuthenticationMessage(facetId: String, authRequestGetUrl: String): String {

        val uafMessage = auth.getUafMessageRequest(facetId, false, authRequestGetUrl)
        return uafMessage.extractJSONString(UAF_AUTH_RESPONSE_FIELD)
    }

    private fun getAuthRequest(uafMessage: String): AuthenticationRequest {
        Log.d(TAG, "  [UAF]Registration - getAuthRequest  : $uafMessage")
        return gson.fromJson(uafMessage, Array<AuthenticationRequest>::class.java)[0]
    }

    fun getUafProtocolMessage(uafMessage: String): String {
        var message = "{\"uafProtocolMessage\":\"" + uafMessage.replace("\"", "\\\"")
        message = "$message\"}"
        return message
    }
}
