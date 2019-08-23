package com.nhs.online.fidoclient.uaf.client

import com.nhs.online.fidoclient.uaf.tlv.TagsEnum
import com.nhs.online.fidoclient.uaf.tlv.UserVerificationEnum
import java.io.ByteArrayOutputStream

class AssertionUtil {

    fun encodeInt(id: Int): ByteArray {
        val bytes = ByteArray(2)

        bytes[0] = (id and 0x00ff).toByte()
        bytes[1] = (id and 0xff00 shr 8).toByte()

        return bytes
    }

    private fun encodeLong(id: Long): ByteArray {
        val bytes = ByteArray(4)

        bytes[0] = (id and 0x00ff).toByte()
        bytes[1] = (id and 0xff00 shr 8).toByte()
        bytes[2] = (id and 0xff00 shr 16).toByte()
        bytes[3] = (id and 0xff00 shr 24).toByte()

        return bytes
    }

    @Throws(Exception::class)
    fun getStorageAssertion(): ByteArray {
        val byteout = ByteArrayOutputStream()

        val extensionId = "fido.uaf.uvm"
        val extensionIdBytes = extensionId.toByteArray()

        byteout.write(encodeInt(TagsEnum.TAG_EXTENSION_ID.id))
        byteout.write(encodeInt(extensionIdBytes.size))
        byteout.write(extensionIdBytes)

        val uvmData = getUvmData()
        byteout.write(encodeInt(TagsEnum.TAG_EXTENSION_DATA.id))
        byteout.write(encodeInt(uvmData.size))
        byteout.write(uvmData)

        return byteout.toByteArray()
    }

    @Throws(Exception::class)
    private fun getUvmData(): ByteArray {
        val byteOut = ByteArrayOutputStream()

        // We enforce the user to their presence with a fingerprint
        val biometricType =
            UserVerificationEnum.USER_VERIFY_PRESENCE.id + UserVerificationEnum.USER_VERIFY_FINGERPRINT.id

        byteOut.write(encodeLong(biometricType))
        byteOut.write(encodeInt(TagsEnum.KEY_PROTECTION_SOFTWARE.id + TagsEnum.KEY_PROTECTION_SECURE_ELEMENT.id))
        byteOut.write(encodeInt(TagsEnum.MATCHER_PROTECTION_ON_CHIP.id))

        return byteOut.toByteArray()
    }
}

