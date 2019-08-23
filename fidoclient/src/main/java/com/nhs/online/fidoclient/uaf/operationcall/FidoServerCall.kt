package com.nhs.online.fidoclient.uaf.operationcall

import com.google.gson.Gson
import com.nhs.online.fidoclient.constants.CONNECTION_ERROR_CODE_KEY_AND_VALUE
import com.nhs.online.fidoclient.constants.EMPTY_UAF_RESPONSE_MESSAGE
import com.nhs.online.fidoclient.constants.UAF_AUTH_RESPONSE_FIELD
import com.nhs.online.fidoclient.uaf.crypto.Base64url
import com.nhs.online.fidoclient.uaf.curl.Curl
import com.nhs.online.fidoclient.uaf.message.TrustedFacetsList
import com.nhs.online.fidoclient.uaf.message.Version
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

private const val HEADER_FIELD = "header"
private const val APP_ID_FIELD = "appID"
private const val VERSION_FIELD = "upv"
private const val TRANSACTION_FIELD = "transaction"

private const val CONTENT_TYPE_FIELD = "contentType"
private const val CONTENT_TYPE_TEXT = "text/plain"
private const val CONTENT_FIELD = "content"
private const val CONTENT_AUTHENTICATION = "Authentication"

open class FidoServerCall {
    private val transaction: JSONArray
        get() {
            val transaction = JSONObject()
            transaction.put(CONTENT_TYPE_FIELD, CONTENT_TYPE_TEXT)
            transaction.put(CONTENT_FIELD,
                Base64url.encodeToString(CONTENT_AUTHENTICATION.toByteArray()))

            val ret = JSONArray()
            ret.put(transaction)
            return ret
        }

    /**
     * Process Request Message
     * @param serverResponse Registration or Authentication request message
     * @param facetId Application facet Id
     * @param isTransaction always false for Registration messages. For Authentication it should be true only for transactions
     * @return uafProtocolMessage
     */
    fun processUafResponseMessage(
        serverResponse: String,
        facetId: String,
        isTransaction: Boolean
    ): String {
        try {
            val requestArray = JSONArray(serverResponse)
            val appID: String? =
                (requestArray.get(0) as JSONObject).getJSONObject(HEADER_FIELD)
                    .getString(APP_ID_FIELD)
            val version =
                Gson().fromJson((requestArray.get(0) as JSONObject).getJSONObject(HEADER_FIELD).getString(
                    VERSION_FIELD), Version::class.java)
            if (appID == null || appID.isEmpty()) {
                (requestArray.get(0) as JSONObject).getJSONObject(HEADER_FIELD)
                    .put(APP_ID_FIELD, facetId)
            } else {
                if (facetId != appID) {
                    val trustedFacetsJson = getTrustedFacets(appID)
                    val trustedFacets =
                        Gson().fromJson(trustedFacetsJson, TrustedFacetsList::class.java)
                    val facetFound = processTrustedFacetsList(trustedFacets, version, facetId)
                    if (!facetFound) {
                        return EMPTY_UAF_RESPONSE_MESSAGE
                    }
                }
            }
            if (isTransaction) {
                (requestArray.get(0) as JSONObject).put(TRANSACTION_FIELD, transaction)
            }
            val uafMessage = JSONObject()
            uafMessage.put(UAF_AUTH_RESPONSE_FIELD, requestArray.toString())
            return uafMessage.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
            return if (serverResponse.contains(CONNECTION_ERROR_CODE_KEY_AND_VALUE))
                serverResponse else
                EMPTY_UAF_RESPONSE_MESSAGE
        }
    }

    /**
     * From among the objects in the trustedFacet array, select the one with the version matching
     * that of the protocol message version. The scheme of URLs in ids MUST identify either an
     * application identity (e.g. using the apk:, ios: or similar scheme) or an https: Web Origin RFC6454.
     * Entries in ids using the https:// scheme MUST contain only scheme, host and port components,
     * with an optional trailing /. Any path, query string, username/password, or fragment information
     * MUST be discarded.
     * @param trustedFacetsList
     * @param version
     * @param facetId
     * @return true if appID list contains facetId (current Android application's signature).
     */
    private fun processTrustedFacetsList(
        trustedFacetsList: TrustedFacetsList?,
        version: Version,
        facetId: String
    ): Boolean {
        trustedFacetsList?.trustedFacets?.forEach { trustedFacets ->
            val facetVersion = trustedFacets.version ?: return@forEach
            val facetIds = trustedFacets.ids ?: return@forEach
            if (facetVersion.minor < version.minor || facetVersion.major > version.major)
                return@forEach
            //The scheme of URLs in ids MUST identify either an application identity
            // (e.g. using the apk:, ios: or similar scheme) or an https: Web Origin [RFC6454].
            for (id in facetIds) if (id == facetId) return true
        }
        return false
    }

    /**
     * Fetches the Trusted Facet List using the HTTP GET method. The location MUST be identified with
     * an HTTPS URL. A Trusted Facet List MAY contain an unlimited number of entries, but clients MAY
     * truncate or decline to process large responses.
     * @param appID an identifier for a set of different Facets of a relying party's application.
     * The AppID is a URL pointing to the TrustedFacets, i.e. list of FacetIDs related
     * to this AppID.
     * @return  Trusted Facets List
     */
    private fun getTrustedFacets(appID: String): String {
        return Curl.get(appID)
    }
}