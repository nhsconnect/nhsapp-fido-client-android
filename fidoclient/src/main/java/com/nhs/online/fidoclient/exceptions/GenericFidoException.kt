package com.nhs.online.fidoclient.exceptions

class GenericFidoException : RuntimeException {

    constructor(cause: Throwable) : super(cause)

    constructor(message: String, cause: Throwable) : super(message, cause)

}