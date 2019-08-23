package com.nhs.online.fidoclient.uaf.message

data class AuthenticationRequest(
        val header: OperationHeader? = null,
        val challenge: String? = null,
        val policy: Policy? = null
)
