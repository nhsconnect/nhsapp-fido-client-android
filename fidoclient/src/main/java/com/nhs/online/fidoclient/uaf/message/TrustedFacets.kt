package com.nhs.online.fidoclient.uaf.message


data class TrustedFacets(
    var version: Version? = null,
    var ids: List<String>? = null
)
