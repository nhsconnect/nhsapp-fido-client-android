package com.nhs.online.fidoclient.constants

const val CONNECTION_ERROR_CODE_KEY_AND_VALUE = "\"error_code\":\"connect_fail\""

const val REGISTRATION_STATUS = "status"
const val ATTESTATION_STATUS = "attestVerifiedStatus"
const val REGISTRATION_STATUS_SUCCESS = "SUCCESS"
const val ATTESTATION_STATUS_VALID = "VALID"
const val REGISTRATION_RESPONSE_ERROR = "Error"

const val HEADER_AUTH_FIELD = "Authorization"

const val UAF_AUTH_RESPONSE_FIELD = "uafProtocolMessage"
const val EMPTY_UAF_RESPONSE_MESSAGE =
        "{\"$UAF_AUTH_RESPONSE_FIELD\": {\"$UAF_AUTH_RESPONSE_FIELD\": \"\"}"

const val AAID = "EBA0#0001"
const val ALGORITHM_SHA256: String = "SHA-256"
const val ALGORITHM_SHA1 = "SHA1"
const val CERTIFICATE_TYPE_X509 = "X509"

const val FACET_KEY_PREFIX = "android:apk-key-hash"