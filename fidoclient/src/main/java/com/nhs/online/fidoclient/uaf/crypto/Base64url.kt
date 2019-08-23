/*
 * Copyright 2016 eBay Software Foundation
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

package com.nhs.online.fidoclient.uaf.crypto

import android.util.Base64

object Base64url {
    private const val BASE64URL_FLAGS = Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP

    fun encodeToString(input: ByteArray): String {
        return Base64.encodeToString(input, BASE64URL_FLAGS)
    }

    fun encode(input: ByteArray): ByteArray {
        return Base64.encode(input, BASE64URL_FLAGS)
    }

    fun decode(input: String): ByteArray {
        return Base64.decode(input, BASE64URL_FLAGS)
    }
}
