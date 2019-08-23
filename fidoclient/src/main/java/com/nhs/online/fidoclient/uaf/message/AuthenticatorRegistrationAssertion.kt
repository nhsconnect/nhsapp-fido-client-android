package com.nhs.online.fidoclient.uaf.message

data class AuthenticatorRegistrationAssertion(
    val assertionScheme: String? = null,
    val assertion: String? = null,
    val exts: List<Extension>? = null
)
