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

package com.nhs.online.fidoclient.uaf.client

import android.util.Log
import com.nhs.online.fidoclient.constants.AAID
import com.nhs.online.fidoclient.constants.ALGORITHM_SHA256
import com.nhs.online.fidoclient.uaf.crypto.Base64url
import com.nhs.online.fidoclient.uaf.crypto.KeyCodec
import com.nhs.online.fidoclient.uaf.crypto.SHA
import com.nhs.online.fidoclient.uaf.message.RegistrationResponse
import com.nhs.online.fidoclient.uaf.tlv.AlgAndEncodingEnum
import com.nhs.online.fidoclient.uaf.tlv.TagsEnum
import com.nhs.online.fidoclient.uaf.tlv.TlvAssertionParser
import com.nhs.online.fidoclient.uaf.tlv.UnsignedUtil.encodeInt
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.security.GeneralSecurityException
import java.security.NoSuchAlgorithmException
import java.security.PublicKey
import java.security.Signature
import java.security.interfaces.ECPublicKey

class RegAssertionBuilder(
    private val publicKey: PublicKey,
    private val signature: Signature,
    private val keyId: String = ""
) {
    private val TAG = RegAssertionBuilder::class.java.simpleName
    private val parser = TlvAssertionParser()
    private val assertionUtil = AssertionUtil()

    private val pubKeyId: ByteArray
        @Throws(GeneralSecurityException::class, IOException::class)
        get() {
            Log.d(TAG, String.format("key: alg: %s enc: %s",
                publicKey.algorithm,
                publicKey.format))

            return KeyCodec.getJCEKeyAsRawBytes(publicKey as ECPublicKey)
        }


    private val counters: ByteArray
        @Throws(IOException::class)
        get() {
            val byteout = ByteArrayOutputStream()
            byteout.write(assertionUtil.encodeInt(0))
            byteout.write(assertionUtil.encodeInt(1))
            byteout.write(assertionUtil.encodeInt(0))
            byteout.write(assertionUtil.encodeInt(1))
            return byteout.toByteArray()
        }

    private val aaid: ByteArray
        @Throws(IOException::class)
        get() {
            val byteout = ByteArrayOutputStream()
            val value = AAID.toByteArray()
            byteout.write(value)
            return byteout.toByteArray()
        }

    @Throws(Exception::class)
    fun getAssertions(response: RegistrationResponse): String {
        val value: ByteArray = getRegAssertion(response)
        val length: Int = value.size

        val byteOut = ByteArrayOutputStream()
        byteOut.write(encodeInt(TagsEnum.TAG_UAFV1_REG_ASSERTION.id))
        byteOut.write(encodeInt(length))
        byteOut.write(value)

        val storageAssertion = assertionUtil.getStorageAssertion()
        byteOut.write(assertionUtil.encodeInt(TagsEnum.TAG_EXTENSION.id))
        byteOut.write(assertionUtil.encodeInt(storageAssertion.size))
        byteOut.write(storageAssertion)

        val assertions = Base64url.encodeToString(byteOut.toByteArray())
        Log.d(TAG, " : assertion : $assertions")
        val tags = parser.parse(assertions)
        Log.d(TAG, "tags: $tags")
        val aaid = String(tags.getTags()[TagsEnum.TAG_AAID.id]!!.value)
        Log.d(TAG, "AAID: $aaid")
        val keyID = String(tags.getTags()[TagsEnum.TAG_KEYID.id]!!.value)
        Log.d(TAG, "keyID: $keyID")
        return assertions
    }

    @Throws(Exception::class)
    private fun getRegAssertion(response: RegistrationResponse): ByteArray {
        var value: ByteArray = getSignedData(response)

        val byteout = ByteArrayOutputStream()
        byteout.write(encodeInt(TagsEnum.TAG_UAFV1_KRD.id))
        byteout.write(encodeInt(value.size))
        byteout.write(value)

        val signedDataValue = byteout.toByteArray()

        byteout.write(encodeInt(TagsEnum.TAG_ATTESTATION_BASIC_FULL.id))
        value = getAttestationBasicSurrogate(signedDataValue)

        byteout.write(encodeInt(value.size))
        byteout.write(value)

        return byteout.toByteArray()
    }

    @Throws(Exception::class)
    private fun getAttestationBasicSurrogate(signedDataValue: ByteArray): ByteArray {
        val byteout = ByteArrayOutputStream()

        byteout.write(encodeInt(TagsEnum.TAG_SIGNATURE.id))
        val value = signData(signedDataValue)
        byteout.write(encodeInt(value.size))
        byteout.write(value)

        return byteout.toByteArray()
    }

    @Throws(IOException::class, GeneralSecurityException::class)
    private fun getSignedData(response: RegistrationResponse): ByteArray {
        val byteout = ByteArrayOutputStream()

        byteout.write(encodeInt(TagsEnum.TAG_AAID.id))
        byteout.write(encodeInt(aaid.size))
        byteout.write(aaid)

        var value = makeAssertionInfo()
        byteout.write(assertionUtil.encodeInt(TagsEnum.TAG_ASSERTION_INFO.id))
        byteout.write(encodeInt(value.size))
        byteout.write(value)

        byteout.write(assertionUtil.encodeInt(TagsEnum.TAG_FINAL_CHALLENGE.id))
        value = getFinalChallengeParameters(response)
        byteout.write(encodeInt(value.size))
        byteout.write(value)

        byteout.write(assertionUtil.encodeInt(TagsEnum.TAG_KEYID.id))
        value = keyId.toByteArray()
        byteout.write(encodeInt(value.size))
        byteout.write(value)

        byteout.write(assertionUtil.encodeInt(TagsEnum.TAG_COUNTERS.id))
        value = counters
        byteout.write(encodeInt(value.size))
        byteout.write(value)

        byteout.write(assertionUtil.encodeInt(TagsEnum.TAG_PUB_KEY.id))
        value = pubKeyId
        byteout.write(encodeInt(value.size))
        byteout.write(value)

        return byteout.toByteArray()
    }

    private fun makeAssertionInfo(): ByteArray {
        //2 bytes - vendor; 1 byte Authentication Mode; 2 bytes Sig Alg; 2 bytes Pub Key Alg
        val bb = ByteBuffer.allocate(7)
        bb.order(ByteOrder.LITTLE_ENDIAN)
        // 2 bytes - vendor assigned version
        bb.put(0x0.toByte())
        bb.put(0x0.toByte())
        // 1 byte Authentication Mode;
        bb.put(0x1.toByte())
        // 2 bytes Sig Alg
        bb.putShort(AlgAndEncodingEnum.UAF_ALG_SIGN_SECP256R1_ECDSA_SHA256_RAW.id.toShort())
        // 2 bytes Pub Key Alg
        bb.putShort(AlgAndEncodingEnum.UAF_ALG_KEY_ECC_X962_RAW.id.toShort())

        return bb.array().clone()
    }

    @Throws(NoSuchAlgorithmException::class)
    private fun getFinalChallengeParameters(response: RegistrationResponse): ByteArray {
        return SHA.sha(response.fcParams.toByteArray(), ALGORITHM_SHA256)
    }

    private fun signData(dataForSigning: ByteArray): ByteArray {
        signature.update(dataForSigning)

        Log.d(TAG, "dataForSigning: ${Base64url.encodeToString(dataForSigning)}")
        val signedData = signature.sign()
        Log.d(TAG, "signature: ${Base64url.encodeToString(signedData)}")

        return signedData
    }
}