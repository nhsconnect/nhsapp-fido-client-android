package com.nhs.online.fidoclient.uaf.message

data class DeregisterAuthenticator(
    val aaid: String? = null,
    val keyID: String? = null
)
