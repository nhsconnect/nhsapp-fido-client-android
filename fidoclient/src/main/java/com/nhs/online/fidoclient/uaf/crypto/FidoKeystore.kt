package com.nhs.online.fidoclient.uaf.crypto

import java.security.KeyPair
import java.security.PrivateKey
import java.security.PublicKey
import java.security.cert.X509Certificate

interface FidoKeystore {
    fun generateKeyPair(username: String): KeyPair

    fun getKeyPair(username: String): KeyPair?

    fun getPublicKey(username: String): PublicKey

    fun getPrivateKey(username: String): PrivateKey

    fun getCertificate(username: String): X509Certificate?

    fun getSigner(username: String): FidoSigner

    fun deleteKey(username: String)
}
