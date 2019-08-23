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

import android.os.Build
import android.util.Log
import com.nhs.online.fidoclient.constants.AAID
import com.nhs.online.fidoclient.uaf.crypto.Base64url
import com.nhs.online.fidoclient.uaf.crypto.FidoSigner
import com.nhs.online.fidoclient.uaf.crypto.SHA
import com.nhs.online.fidoclient.uaf.message.AuthenticationResponse
import com.nhs.online.fidoclient.uaf.tlv.AlgAndEncodingEnum
import com.nhs.online.fidoclient.uaf.tlv.TagsEnum
import org.mindrot.jbcrypt.BCrypt
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.security.KeyPair
import java.security.NoSuchAlgorithmException

class AuthAssertionBuilder(
        private val fidoSigner: FidoSigner,
        private val signingKeyPair: KeyPair?,
        private val keyId: String = ""
) {

    private val assertionUtil = AssertionUtil()

    private val counters: ByteArray
        @Throws(IOException::class)
        get() {
            val byteout = ByteArrayOutputStream()
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
    fun getAssertions(response: AuthenticationResponse): String {
        val byteOut = ByteArrayOutputStream()
        val value: ByteArray = getAuthAssertion(response)
        val length: Int = value.size

        byteOut.write(assertionUtil.encodeInt(TagsEnum.TAG_UAFV1_AUTH_ASSERTION.id))
        byteOut.write(assertionUtil.encodeInt(length))
        byteOut.write(value)

        val storageAssertion = assertionUtil.getStorageAssertion()
        byteOut.write(assertionUtil.encodeInt(TagsEnum.TAG_EXTENSION.id))
        byteOut.write(assertionUtil.encodeInt(storageAssertion.size))
        byteOut.write(storageAssertion)

        val assertions = Base64url.encodeToString(byteOut.toByteArray())
        Log.d(TAG, " : assertion : $assertions")
        return assertions
    }

    @Throws(Exception::class)
    private fun getAuthAssertion(response: AuthenticationResponse): ByteArray {
        val byteout = ByteArrayOutputStream()
        var value: ByteArray?
        var length: Int

        byteout.write(assertionUtil.encodeInt(TagsEnum.TAG_UAFV1_SIGNED_DATA.id))
        value = getSignedData(response)
        length = value.size
        byteout.write(assertionUtil.encodeInt(length))
        byteout.write(value)

        val signedDataValue = byteout.toByteArray()

        byteout.write(assertionUtil.encodeInt(TagsEnum.TAG_SIGNATURE.id))
        value = getSignature(signedDataValue)
        length = value.size
        byteout.write(assertionUtil.encodeInt(length))
        byteout.write(value)

        return byteout.toByteArray()
    }

    @Throws(IOException::class, NoSuchAlgorithmException::class)
    private fun getSignedData(response: AuthenticationResponse): ByteArray {
        val byteout = ByteArrayOutputStream()
        var value: ByteArray?
        var length: Int

        byteout.write(assertionUtil.encodeInt(TagsEnum.TAG_AAID.id))
        value = aaid
        length = value.size
        byteout.write(assertionUtil.encodeInt(length))
        byteout.write(value)

        byteout.write(assertionUtil.encodeInt(TagsEnum.TAG_ASSERTION_INFO.id))
        value = makeAssertionInfo()

        length = value.size
        byteout.write(assertionUtil.encodeInt(length))
        byteout.write(value)

        byteout.write(assertionUtil.encodeInt(TagsEnum.TAG_AUTHENTICATOR_NONCE.id))
        value = SHA.sha256(BCrypt.gensalt()).toByteArray()
        length = value.size
        byteout.write(assertionUtil.encodeInt(length))
        byteout.write(value)

        byteout.write(assertionUtil.encodeInt(TagsEnum.TAG_FINAL_CHALLENGE.id))
        value = getFinalChallengeParameters(response)
        length = value.size
        byteout.write(assertionUtil.encodeInt(length))
        byteout.write(value)

        byteout.write(assertionUtil.encodeInt(TagsEnum.TAG_TRANSACTION_CONTENT_HASH.id))
        length = 0
        byteout.write(assertionUtil.encodeInt(length))

        byteout.write(assertionUtil.encodeInt(TagsEnum.TAG_KEYID.id))
        value = keyId.toByteArray()
        length = value.size
        byteout.write(assertionUtil.encodeInt(length))
        byteout.write(value)

        byteout.write(assertionUtil.encodeInt(TagsEnum.TAG_COUNTERS.id))
        value = counters
        length = value.size
        byteout.write(assertionUtil.encodeInt(length))
        byteout.write(value)

        return byteout.toByteArray()
    }

    @Throws(NoSuchAlgorithmException::class)
    private fun getFinalChallengeParameters(response: AuthenticationResponse): ByteArray {
        return SHA.sha(response.fcParams!!.toByteArray(), "SHA-256")
    }

    @Throws(Exception::class)
    private fun getSignature(dataForSigning: ByteArray): ByteArray {
        Log.d(TAG, "getSignature")

        Log.d(TAG, "dataForSigning : " + Base64url.encode(dataForSigning))
        val signature = fidoSigner.sign(dataForSigning, signingKeyPair)

        Log.d(TAG, " : signature : " + Base64url.encode(signature))

        return signature
    }

    companion object {

        private val TAG = AuthAssertionBuilder::class.java.simpleName

        private const val VENDOR = 0x0
        private const val AUTHENTICATION_MODE = 0x1

        private fun makeAssertionInfo(): ByteArray {
            //2 bytes - vendor; 1 byte Authentication Mode; 2 bytes Sig Alg
            val vendorByte = VENDOR.toByte()
            val authenticationModeByte = AUTHENTICATION_MODE.toByte()
            //2 bytes - vendor 1 byte Authentication Mode;
            val byteArray = byteArrayOf(vendorByte, vendorByte, authenticationModeByte)
            val bb = ByteBuffer.allocate(5)
            bb.order(ByteOrder.LITTLE_ENDIAN)
            bb.put(byteArray)
            // 2 bytes Sig Alg
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                bb.putShort(AlgAndEncodingEnum.UAF_ALG_SIGN_SECP256R1_ECDSA_SHA256_DER.id.toShort())
            } else {
                bb.putShort(AlgAndEncodingEnum.UAF_ALG_SIGN_SECP256R1_ECDSA_SHA256_RAW.id.toShort())
            }

            return bb.array().clone()
        }
    }

}

