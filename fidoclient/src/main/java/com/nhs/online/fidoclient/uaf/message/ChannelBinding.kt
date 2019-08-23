package com.nhs.online.fidoclient.uaf.message

data class ChannelBinding(
    val serverEndPoint: String? = "",
    val tlsServerCertificate: String? = "",
    val tlsUnique: String? = "",
    val cidPubKey: String? = ""
)
