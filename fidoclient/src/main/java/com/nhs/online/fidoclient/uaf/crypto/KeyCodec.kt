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

import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.math.BigInteger
import java.security.*
import java.security.interfaces.ECPublicKey
import java.security.spec.InvalidKeySpecException
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec


object KeyCodec {

    private val TAG = KeyCodec::class.java.simpleName

    @Throws(IOException::class)
    fun getJCEKeyAsRawBytes(pub: ECPublicKey): ByteArray {
        val raw: ByteArray
        val bos = ByteArrayOutputStream(65)

        bos.write(0x04)
        bos.write(asUnsignedByteArray(pub.w.affineX))
        bos.write(asUnsignedByteArray(pub.w.affineY))
        raw = bos.toByteArray()
        Log.d(TAG, "Raw key length:" + raw.size)
        return raw
    }

    /**
     * Return the passed in value as an unsigned byte array.
     *
     * @param value value to be converted.
     * @return a byte array without a leading zero byte if present in the signed encoding.
     */
    private fun asUnsignedByteArray(
        value: BigInteger
    ): ByteArray {
        val bytes = value.toByteArray()

        if (bytes[0].toInt() == 0) {
            val tmp = ByteArray(bytes.size - 1)

            System.arraycopy(bytes, 1, tmp, 0, tmp.size)

            return tmp
        }

        return bytes
    }

    @Throws(InvalidKeySpecException::class,
        NoSuchAlgorithmException::class,
        NoSuchProviderException::class)
    fun getPubKey(bytes: ByteArray): PublicKey {
        val kf = KeyFactory.getInstance("EC")
        return kf.generatePublic(X509EncodedKeySpec(bytes))
    }

    @Throws(NoSuchAlgorithmException::class,
        InvalidKeySpecException::class,
        NoSuchProviderException::class)
    fun getPrivKey(bytes: ByteArray): PrivateKey {
        val kf = KeyFactory.getInstance("EC")
        return kf.generatePrivate(PKCS8EncodedKeySpec(bytes))
    }

}
