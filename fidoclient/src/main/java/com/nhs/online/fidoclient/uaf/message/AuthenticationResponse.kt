package com.nhs.online.fidoclient.uaf.message

data class AuthenticationResponse(
    var header: OperationHeader? = null,
    var fcParams: String? = null,
    var assertions: List<AuthenticatorSignAssertion?> = emptyList()
)
