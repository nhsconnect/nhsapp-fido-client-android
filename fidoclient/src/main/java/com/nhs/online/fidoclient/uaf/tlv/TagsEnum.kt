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

enum class TagsEnum constructor(val id: Int) {

    UAF_CMD_STATUS_ERR_UNKNOWN(0x01),
    TAG_UAFV1_REG_ASSERTION(0x3E01),
    TAG_UAFV1_AUTH_ASSERTION(0x3E02),
    TAG_UAFV1_KRD(0x3E03),
    TAG_UAFV1_SIGNED_DATA(0x3E04),
    TAG_ATTESTATION_CERT(0x2E05),
    TAG_SIGNATURE(0x2E06),
    TAG_ATTESTATION_BASIC_FULL(0x3E07),
    TAG_ATTESTATION_BASIC_SURROGATE(0x3E08),
    TAG_KEYID(0x2E09),
    TAG_FINAL_CHALLENGE(0x2E0A),
    TAG_AAID(0x2E0B),
    TAG_PUB_KEY(0x2E0C),
    TAG_COUNTERS(0x2E0D),
    TAG_ASSERTION_INFO(0x2E0E),
    TAG_AUTHENTICATOR_NONCE(0x2E0F),
    TAG_TRANSACTION_CONTENT_HASH(0x2E10),
    TAG_EXTENSION(0x3E11),
    TAG_EXTENSION_NON_CRITICAL(0x3E12),
    TAG_EXTENSION_ID(0x2E13),
    TAG_EXTENSION_DATA(0x2E14),
    KEY_PROTECTION_SOFTWARE(0x0001),
    KEY_PROTECTION_HARDWARE(0x0002),
    KEY_PROTECTION_TEE(0x0004),
    KEY_PROTECTION_SECURE_ELEMENT(0x0008),
    MATCHER_PROTECTION_SOFTWARE(0x0001),
    MATCHER_PROTECTION_TEE(0x0002),
    MATCHER_PROTECTION_ON_CHIP(0x0004);


    companion object {

        operator fun get(id: Int): TagsEnum? {
            for (tag in TagsEnum.values()) {
                if (tag.id == id) {
                    return tag
                }
            }
            return null
        }
    }
}
