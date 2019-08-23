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

package com.nhs.online.fidoclient.uaf.curl

import android.net.Uri
import android.util.Log
import com.nhs.online.fidoclient.constants.CONNECTION_ERROR_CODE_KEY_AND_VALUE
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLConnection
import java.io.BufferedWriter
import java.io.OutputStreamWriter
import java.lang.Exception


object Curl {

    private fun URLConnection.responseToString(): String {
        return try {
            val responseBufferedReader = BufferedReader(InputStreamReader(getInputStream()))
            val stringBuffer = StringBuffer()
            responseBufferedReader.forEachLine { stringBuffer.append("$it\n") }
            responseBufferedReader.close()
            stringBuffer.toString()
        } catch (ex: Exception) {
            "Error"
        }
    }

    fun get(url: String, header: String?, redirect_match: String = ""): String {
        Log.d("CURL: ",
            "Entered CURL get with url: $url and header: $header and redirect_match: $redirect_match")
        val requestHeaders: Array<String> =
            header?.split(" ")?.dropLastWhile { it.isEmpty() }?.toTypedArray() ?: emptyArray()
        return get(url, requestHeaders, redirect_match)
    }

    fun get(
        urlString: String,
        headers: Array<String> = arrayOf(),
        redirect_match: String = ""
    ): String {
        Log.d("CURL: ",
            "Entered CURL get with url: $urlString and headers length: ${headers.size} and redirect_match: $redirect_match")
        return try {
            val urlConnection =
                toHttpUrlConnection(urlString,
                    mapOf(Pair("http.protocol.handle-redirects", "false")))
            addRequestHeadersToUrlConnection(headers, urlConnection)
            try {
                urlConnection.connect()
                val locationHeaders = urlConnection.headerFields["Location"]
                if (urlConnection.responseCode == HttpURLConnection.HTTP_MOVED_TEMP && locationHeaders != null) {
                    Log.d("CURL: ",
                        "Got a 302 response with Location header. Following redirect if location does not match redirect_match")
                    val locationHeaderValue = locationHeaders.first()
                    Log.d("CURL: ", "Location is $locationHeaderValue")
                    var locationHeaderWithHost = ""
                    return if (locationHeaderValue.startsWith(redirect_match)) {
                        Log.d("CURL: ", "Matched redirect - sending location back as the response")
                        locationHeaderValue
                    } else {
                        Log.d("CURL: ",
                            "Did not match redirect - following location with new http request")
                        if (locationHeaderValue.indexOf("://") == -1) {
                            //need to prefix the location with the original host
                            val host = urlString.substring(0, urlString.indexOf("/", 10) + 1)
                            Log.d("CURL: ", "host to prefix the url is $host")
                            locationHeaderWithHost = host + locationHeaderValue
                        }
                        get(locationHeaderWithHost, headers, redirect_match)
                    }
                }
                urlConnection.responseToString()
            } catch (ex: Exception) {
                ex.printStackTrace()
                "{${CONNECTION_ERROR_CODE_KEY_AND_VALUE},'e':'$urlString'}"
            } finally {
                urlConnection.disconnect()
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
            "{${CONNECTION_ERROR_CODE_KEY_AND_VALUE},'e':'$ex'}"
        }
    }

    private fun toHttpUrlConnection(
        urlString: String,
        queries: Map<String, String> = emptyMap()
    ): HttpURLConnection {
        val uriBuilder = Uri.parse(urlString).buildUpon()
        for ((key, value) in queries) {
            uriBuilder.appendQueryParameter(key, value)
        }
        val urlWithQuery = uriBuilder.build().toString()
        return URL(urlWithQuery).openConnection() as HttpURLConnection
    }

    fun post(url: String, header: String, data: String): String {
        return post(url,
            header.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray(),
            data)
    }

    private fun post(urlString: String, headers: Array<String>, data: String): String {
        Log.d("CURL: ",
            "Entered CURL post with url: $urlString and headers length: ${headers.size} and data: $data")
        return try {
            val urlConnection = toHttpUrlConnection(urlString)
            urlConnection.requestMethod = "POST"
            addRequestHeadersToUrlConnection(headers, urlConnection, Pair("&nbsp;", " "))
            try {
                writeDataToUrlConnection(data, urlConnection)
                urlConnection.connect()
                urlConnection.responseToString()
            } catch (ex: Exception) {
                ex.printStackTrace()
                "{${CONNECTION_ERROR_CODE_KEY_AND_VALUE},'e':'$urlString'}"
            } finally {
                urlConnection.disconnect()
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            "{${CONNECTION_ERROR_CODE_KEY_AND_VALUE},'e':'$ex'}"
        }
    }

    private fun writeDataToUrlConnection(data: String, urlConnection: HttpURLConnection) {
        urlConnection.doOutput = true
        urlConnection.addRequestProperty("Content-Length", data.length.toString())

        urlConnection.outputStream.use { outputStream ->
            BufferedWriter(
                OutputStreamWriter(outputStream, "UTF-8")).use { writer ->
                writer.write(data)
            }
        }
    }

    private fun addRequestHeadersToUrlConnection(
        headers: Array<String>?,
        urlConnection: HttpURLConnection,
        replaceText: Pair<String, String>? = null
    ) {
        headers?.forEach { header ->
            val headerKV = header.split(":")
                .dropLastWhile { it.isEmpty() }
                .toTypedArray()
            if (headerKV.size == 2) {
                val value = if (replaceText == null) headerKV[1] else headerKV[1].replace(
                    replaceText.first,
                    replaceText.second)

                urlConnection.addRequestProperty(headerKV[0], value)
            }
        }
    }
}