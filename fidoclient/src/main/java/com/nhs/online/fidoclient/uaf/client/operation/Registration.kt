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
import com.google.gson.Gson
import com.nhs.online.fidoclient.exceptions.FidoAssertionException
import com.nhs.online.fidoclient.uaf.client.RegAssertionBuilder
import com.nhs.online.fidoclient.uaf.crypto.Base64url
import com.nhs.online.fidoclient.uaf.message.*
import com.nhs.online.fidoclient.uaf.operationcall.RegistrationCall
import com.nhs.online.fidoclient.utils.extractJSONString

class Registration(
    private val registrationCall: RegistrationCall = RegistrationCall()
) {
    private val TAG = Registration::class.java.simpleName
    private val gson = Gson()
    private val defaultScheme = "UAFV1TLV"

    @SuppressLint("PackageManagerGetSignatures") // This vulnerability can only be exploited in Android version 4.4 and below. This is below the minimum supported version of the app
    fun requestUafRegistrationMessage(facetId: String, accessToken: String, regRequestGetUrl: String): String {
        val uafRegistrationMessage =
            registrationCall.getUafRegistrationMessage(accessToken, facetId, regRequestGetUrl)
        Log.d(TAG, "UAF registrationCall request: $uafRegistrationMessage")

        return uafRegistrationMessage.extractJSONString("uafProtocolMessage")
    }


    fun processRegisterMessage(
        uafMessage: String,
        assertionBuilder: RegAssertionBuilder
    ): String {
        Log.d(TAG, "  [UAF] Registration  ")
        val registrationResponses = arrayOfNulls<RegistrationResponse>(1)
        val registrationReq = getRegistrationRequest(uafMessage)
        val regResponse = processRequest(registrationReq, assertionBuilder)
        Log.d(TAG, "  [UAF] Registration - Registration Response Formed  ")
        Log.d(TAG, regResponse.assertions.firstOrNull()?.assertion)
        Log.d(TAG, "  [UAF] Registration - done  ")
        Log.d(TAG, "  [UAF] Registration - keys stored  ")
        registrationResponses[0] = regResponse
        return getUafProtocolMessage(gson.toJson(registrationResponses))

    }

    fun processRequest(
            regRequest: RegistrationRequest,
            assertionBuilder: RegAssertionBuilder
    ): RegistrationResponse {
        val gson = Gson()

        val header = regRequest.header ?: OperationHeader()
        val finalChallengeParameters = FinalChallengeParams(header.appID, regRequest.challenge, "")

        val encodedFinalChallengeParameters =
            Base64url.encodeToString(gson.toJson(finalChallengeParameters).toByteArray())

        val response = RegistrationResponse(encodedFinalChallengeParameters, header = header)
        setAssertions(response, assertionBuilder)
        return response
    }

    private fun setAssertions(response: RegistrationResponse, builder: RegAssertionBuilder) {
        try {
            val assertion = builder.getAssertions(response)
            val authSignAssertion =
                AuthenticatorRegistrationAssertion(assertionScheme = defaultScheme,
                    assertion = assertion)
            response.assertions = listOf(authSignAssertion)
        } catch (e: Exception) {
            throw FidoAssertionException("Failed to sign authentication assertions", e)
        }

    }

    fun getRegistrationRequest(uafMessage: String): RegistrationRequest {
        Log.d(TAG, "  [UAF]Registration - getRegRequest  : $uafMessage")
        return gson.fromJson(uafMessage, Array<RegistrationRequest>::class.java)[0]
    }

    fun retrieveApplicationIdFrom(uafMessage: String): String {
        val requestHeader = getRegistrationRequest(uafMessage).header ?: OperationHeader()
        return requestHeader.appID
    }

    fun getUafProtocolMessage(uafMessage: String): String {
        return "{\"uafProtocolMessage\":" + "\"" + uafMessage.replace("\"", "\\\"") + "\"}"
    }
}