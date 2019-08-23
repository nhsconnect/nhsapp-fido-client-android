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

package com.nhs.online.fidoclient.uaf.tlv

import com.nhs.online.fidoclient.uaf.crypto.Base64url

import java.io.IOException


class TlvAssertionParser {

    @Throws(IOException::class)
    fun parse(base64OfRegResponse: String): Tags {
        val bytes = ByteInputStream(Base64url.decode(base64OfRegResponse))
        val isRegistration = false
        return parse(bytes, isRegistration)
    }


    @Throws(IOException::class)
    fun parse(bytes: ByteInputStream, isRegistered: Boolean): Tags {
        var isRegistration = isRegistered

        val result = Tags()

        var tag: Tag
        while (bytes.available() > 0) {
            tag = Tag()
            tag.id = UnsignedUtil.readUAFV1UINT16(bytes)
            tag.length = UnsignedUtil.readUAFV1UINT16(bytes)

            if (tag.id == TagsEnum.TAG_UAFV1_AUTH_ASSERTION.id) {
                //ret.add(t);
                addTagAndValue(bytes, result, tag)
                addSubTags(isRegistration, result, tag)
            } else if (tag.id == TagsEnum.TAG_UAFV1_SIGNED_DATA.id) {
                //ret.add(t);
                addTagAndValue(bytes, result, tag)
                addSubTags(isRegistration, result, tag)
            } else if (tag.id == TagsEnum.TAG_UAFV1_REG_ASSERTION.id) {
                isRegistration = true
                //ret.add(t);
                addTagAndValue(bytes, result, tag)
                addSubTags(isRegistration, result, tag)
            } else if (tag.id == TagsEnum.TAG_UAFV1_KRD.id) {
                result.add(tag)
                addTagAndValue(bytes, result, tag)
                addSubTags(isRegistration, result, tag)
            } else if (tag.id == TagsEnum.TAG_AAID.id) {
                addTagAndValue(bytes, result, tag)
            } else if (tag.id == TagsEnum.TAG_ASSERTION_INFO.id) {
                //2 - Vendor assigned authenticator version.
                //1 - Authentication Mode indicating whether user explicitly verified or not and indicating if there is a transaction content or not.
                //2 - Signature algorithm and encoding format.
                if (isRegistration) {
                    tag.value = bytes.read(7)
                } else {
                    tag.value = bytes.read(5)
                }
                result.add(tag)
            } else if (tag.id == TagsEnum.TAG_AUTHENTICATOR_NONCE.id) {
                addTagAndValue(bytes, result, tag)
            } else if (tag.id == TagsEnum.TAG_FINAL_CHALLENGE.id) {
                addTagAndValue(bytes, result, tag)
            } else if (tag.id == TagsEnum.TAG_TRANSACTION_CONTENT_HASH.id) {
                if (tag.length > 0) {
                    addTagAndValue(bytes, result, tag)
                } else {
                    //Length of Transaction Content Hash. This length is 0 if AuthenticationMode == 0x01, i.e. authentication, not transaction confirmation.
                    result.add(tag)
                }
            } else if (tag.id == TagsEnum.TAG_KEYID.id) {
                addTagAndValue(bytes, result, tag)
            } else if (tag.id == TagsEnum.TAG_COUNTERS.id) {
                //Indicates how many times this authenticator has performed signatures in the past
                if (isRegistration) {
                    tag.value = bytes.read(8)
                } else {
                    tag.value = bytes.read(4)
                }
                result.add(tag)
            } else if (tag.id == TagsEnum.TAG_KEYID.id) {
                addTagAndValue(bytes, result, tag)
            } else if (tag.id == TagsEnum.TAG_PUB_KEY.id) {
                addTagAndValue(bytes, result, tag)
            } else if (tag.id == TagsEnum.TAG_ATTESTATION_BASIC_FULL.id) {
                result.add(tag)
            } else if (tag.id == TagsEnum.TAG_SIGNATURE.id) {
                addTagAndValue(bytes, result, tag)
            } else if (tag.id == TagsEnum.TAG_ATTESTATION_CERT.id) {
                addTagAndValue(bytes, result, tag)
            } else if (tag.id == TagsEnum.TAG_ATTESTATION_BASIC_SURROGATE.id) {
                result.add(tag)
            } else {
                tag.statusId = TagsEnum.UAF_CMD_STATUS_ERR_UNKNOWN.id
                tag.value = bytes.readAll()
                result.add(tag)
            }

        }

        return result
    }

    @Throws(IOException::class)
    private fun addSubTags(isReg: Boolean, ret: Tags, t: Tag) {
        ret.addAll(parse(ByteInputStream(t.value), isReg))
    }

    private fun addTagAndValue(bytes: ByteInputStream, ret: Tags, tag: Tag) {
        tag.value = bytes.read(tag.length)
        ret.add(tag)
    }
}
