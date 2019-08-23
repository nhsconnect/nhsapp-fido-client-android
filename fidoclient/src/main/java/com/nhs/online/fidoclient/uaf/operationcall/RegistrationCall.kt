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

package com.nhs.online.fidoclient.uaf.operationcall

import android.util.Log
import com.nhs.online.fidoclient.constants.HEADER_AUTH_FIELD
import com.nhs.online.fidoclient.constants.UAF_AUTH_RESPONSE_FIELD
import com.nhs.online.fidoclient.uaf.curl.Curl
import org.json.JSONException
import org.json.JSONObject

private const val REGISTRATION_POST_HEADER_VALUE =
    "Content-Type:Application/json Accept:Application/json"

class RegistrationCall() : FidoServerCall() {

    fun getUafRegistrationMessage(
        accessToken: String, facetId: String, regRequestGetUrl: String
    ): String {
        val header = "$HEADER_AUTH_FIELD:$accessToken"

        Log.d("Registration: ", "Making CURL GET with url: $regRequestGetUrl and header: $header")

        val serverResponse = Curl.get(regRequestGetUrl, header)
        return processUafResponseMessage(serverResponse, facetId, false)
    }

    fun sendClientRegistrationMessage(uafMessage: String, regResponsePostUrl: String): String {
        var decoded = ""
        try {
            val json = JSONObject(uafMessage)
            decoded = json.getString(UAF_AUTH_RESPONSE_FIELD).replace("\\", "")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return Curl.post(regResponsePostUrl,
            REGISTRATION_POST_HEADER_VALUE,
            decoded)
    }
}
