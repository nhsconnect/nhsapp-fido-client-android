package com.nhs.online.fidoclient.uaf.crypto

import java.security.KeyPair

interface FidoSigner {

    fun sign(dataToSign: ByteArray, keyPair: KeyPair?): ByteArray
}
