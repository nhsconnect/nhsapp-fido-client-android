package com.nhs.online.fidoclient.exceptions

class FidoAssertionException(message: String, cause: Throwable) :
    RuntimeException(message, cause) {

    companion object {
        private const val serialVersionUID = 7718828512143293558L

    }

}