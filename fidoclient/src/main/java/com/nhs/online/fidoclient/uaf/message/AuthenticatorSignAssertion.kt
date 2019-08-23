package com.nhs.online.fidoclient.uaf.message

data class AuthenticatorSignAssertion(
    val assertionScheme: String? = null,
    val assertion: String? = null,
    val exts: List<Extension>? = null
)
