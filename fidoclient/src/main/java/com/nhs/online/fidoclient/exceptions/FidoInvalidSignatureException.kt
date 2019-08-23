package com.nhs.online.fidoclient.exceptions

class FidoInvalidSignatureException : RuntimeException {

    constructor(cause: Throwable) : super(cause)
    constructor(message: String, cause: Throwable) : super(message, cause)

}