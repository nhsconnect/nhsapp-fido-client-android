package com.nhs.online.fidoclient.uaf.tlv

enum class UserVerificationEnum constructor(val id: Long) {

    USER_VERIFY_PRESENCE(0x00000001),
    USER_VERIFY_FINGERPRINT(0x00000002),
    USER_VERIFY_FACEPRINT(0x00000010);

    companion object {

        operator fun get(id: Long): UserVerificationEnum? {
            for (tag in UserVerificationEnum.values()) {
                if (tag.id == id) {
                    return tag
                }
            }
            return null
        }
    }
}