package com.nhs.online.fidoclient.uaf.crypto

import com.nhs.online.fidoclient.exceptions.FidoInvalidSignatureException
import java.security.KeyPair
import java.security.Signature
import java.security.SignatureException

class FidoSignerAndroidM
(private val signature: Signature) : FidoSigner {

    override fun sign(dataToSign: ByteArray, keyPair: KeyPair?): ByteArray {
        try {
            signature.update(dataToSign)

            return signature.sign()
        } catch (e: SignatureException) {
            throw FidoInvalidSignatureException(e)
        }

    }
}
