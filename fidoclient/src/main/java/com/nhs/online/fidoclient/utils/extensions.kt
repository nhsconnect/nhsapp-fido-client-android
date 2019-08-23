package com.nhs.online.fidoclient.utils

import org.json.JSONException
import org.json.JSONObject

fun String.extractJSONString(key: String): String {
    return try {
        JSONObject(this).getString(key)
                .replace("\\\"", "\"")
    } catch (e: JSONException) {
        this
    }
}