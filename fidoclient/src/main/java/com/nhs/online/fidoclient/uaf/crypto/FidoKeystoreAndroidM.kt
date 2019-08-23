package com.nhs.online.fidoclient.uaf.crypto

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.support.annotation.RequiresApi
import android.util.Log
import com.nhs.online.fidoclient.exceptions.GenericFidoException

import java.io.IOException
import java.security.*
import java.security.cert.X509Certificate
import java.security.spec.ECGenParameterSpec

@RequiresApi(api = Build.VERSION_CODES.M)
class FidoKeystoreAndroidM (
        private val keyIdPrefix: String
): FidoKeystore {

    private val androidKeyStore: KeyStore
        get() {
            try {
                val keyStore = KeyStore.getInstance("AndroidKeyStore")
                keyStore.load(null)

                return keyStore
            } catch (e: GeneralSecurityException) {
                throw RuntimeException(e)
            } catch (e: IOException) {
                throw RuntimeException(e)
            }

        }

    private fun getKeyId(username: String): String {
        return "${keyIdPrefix}_$username"
    }

    override fun getPrivateKey(username: String): PrivateKey {
        val keystore = androidKeyStore
        return keystore.getKey(getKeyId(username), null) as PrivateKey
    }

    override fun generateKeyPair(username: String): KeyPair {
        Log.d(TAG, "generateKeyPair")

        try {
            val keyId = getKeyId(username)
            Log.d(TAG, "keyId = $keyId")

            val keyPairGenerator = KeyPairGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_EC, "AndroidKeyStore")

            val builder: KeyGenParameterSpec.Builder = KeyGenParameterSpec.Builder(
                keyId,
                KeyProperties.PURPOSE_SIGN)
                .setAlgorithmParameterSpec(ECGenParameterSpec(ecGeneratorParameterSpec))
                .setDigests(KeyProperties.DIGEST_SHA256,
                    KeyProperties.DIGEST_SHA384,
                    KeyProperties.DIGEST_SHA512)
                .setUserAuthenticationRequired(true)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                builder.setAttestationChallenge(ByteArray(16))
            }
            keyPairGenerator.initialize(builder.build())

            val keyPair = keyPairGenerator.generateKeyPair()
            Log.d(TAG, "Generated keypair : $keyPair")

            val keyStore = androidKeyStore
            val cert = keyStore.getCertificate(keyId) as X509Certificate
            Log.d(TAG, "certificate: $cert")

            return keyPair
        } catch (e: GeneralSecurityException) {
            throw RuntimeException(e)
        }

    }

    override fun getKeyPair(username: String): KeyPair? {
        try {
            val pubKey = getPublicKey(username)
            val privKey = androidKeyStore.getKey(getKeyId(username), null) as PrivateKey

            if (!validateKey(privKey)) {
                return null
            }

            return KeyPair(pubKey, privKey)
        } catch (e: GeneralSecurityException) {
            throw RuntimeException(e)
        }
    }

    override fun getPublicKey(username: String): PublicKey {
        return getCertificate(username).publicKey
    }

    override fun getCertificate(username: String): X509Certificate {
        try {
            return androidKeyStore.getCertificate(getKeyId(username)) as X509Certificate
        } catch (e: KeyStoreException) {
            throw RuntimeException(e)
        }

    }

    override fun getSigner(username: String): FidoSigner {
        try {
            val privateKey = androidKeyStore.getKey(getKeyId(username), null) as PrivateKey
            val signature = Signature.getInstance("SHA256withECDSA")
            signature.initSign(privateKey)

            return FidoSignerAndroidM(signature)
        } catch (e: GeneralSecurityException) {
            throw RuntimeException(e)
        }

    }

    override fun deleteKey(username: String) {
        try {
            Log.d(TAG, "Deleting key from KeyStore")
            androidKeyStore.deleteEntry(getKeyId(username))
        } catch (e: KeyStoreException) {
            throw GenericFidoException(e)
        }
    }

    private fun validateKey(privateKey: PrivateKey): Boolean {
        return try {
            val signature = Signature.getInstance("SHA256withECDSA")
            signature.initSign(privateKey)
            true
        } catch (e: InvalidKeyException) {
            false
        }
    }

    companion object {
        private val TAG = FidoKeystoreAndroidM::class.java.simpleName

        private const val ecGeneratorParameterSpec = "secp256r1"
    }
}
