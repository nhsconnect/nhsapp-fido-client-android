package com.nhs.online.fidoclient.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.util.Base64
import android.util.Log
import com.nhs.online.fidoclient.constants.ALGORITHM_SHA1
import com.nhs.online.fidoclient.constants.CERTIFICATE_TYPE_X509
import com.nhs.online.fidoclient.constants.FACET_KEY_PREFIX
import com.nhs.online.fidoclient.uaf.crypto.Base64url
import org.mindrot.jbcrypt.BCrypt
import java.io.ByteArrayInputStream
import java.security.MessageDigest
import java.security.cert.CertificateFactory

object fidoHelpers {
    fun generateFidoKeyId(keyPrefix: String): String {
        val key = "$keyPrefix-${Base64url.encodeToString(BCrypt.gensalt().toByteArray())}"
        return Base64url.encodeToString(key.toByteArray())
    }

    @SuppressLint("PackageManagerGetSignatures") // This vulnerability can only be exploited in Android version 4.4 and below. This is below the minimum supported version of the app
    fun getFacetId(context: Context): String? {
        try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName,
                    PackageManager.GET_SIGNATURES)
            val byteArrayInputStream =
                    ByteArrayInputStream(packageInfo.signatures[0].toByteArray())
            val certificate =
                    CertificateFactory.getInstance(CERTIFICATE_TYPE_X509)
                            .generateCertificate(byteArrayInputStream)
            val messageDigest = MessageDigest.getInstance(ALGORITHM_SHA1)

            return "$FACET_KEY_PREFIX:" + Base64.encodeToString((messageDigest as MessageDigest).digest(
                    certificate.encoded), 3)
        } catch (e: Exception) {
            Log.d("Fido", "Failed to get Facet ID with error: $e")
            return null
        }
    }
}

