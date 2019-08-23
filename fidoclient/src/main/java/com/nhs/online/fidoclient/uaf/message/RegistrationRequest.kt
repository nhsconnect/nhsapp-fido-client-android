package com.nhs.online.fidoclient.uaf.message

data class RegistrationRequest(
    val header: OperationHeader? = null,
    val challenge: String? = null,
    val username: String = "",
    val policy: Policy? = null
)
