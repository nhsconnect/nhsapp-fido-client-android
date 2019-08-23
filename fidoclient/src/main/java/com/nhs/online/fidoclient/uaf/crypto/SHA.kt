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

package com.nhs.online.fidoclient.uaf.crypto

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

object SHA {

    fun sha256(base: String): String {
        return sha(base, "SHA-256")
    }

    private fun sha(base: String, alg: String): String {
        try {
            val digest = MessageDigest.getInstance(alg)
            val hash = digest.digest(base.toByteArray(charset("UTF-8")))
            val hexString = StringBuffer()

            for (i in hash.indices) {
                val hex = Integer.toHexString(0xff and hash[i].toInt())
                if (hex.length == 1)
                    hexString.append('0')
                hexString.append(hex)
            }

            return hexString.toString()
        } catch (ex: Exception) {
            throw RuntimeException(ex)
        }

    }

    @Throws(NoSuchAlgorithmException::class)
    fun sha(base: ByteArray, alg: String): ByteArray {
        val digest = MessageDigest.getInstance(alg)
        return digest.digest(base)
    }

}
