package com.nhs.online.fidoclient.uaf.message

import com.google.gson.Gson
import com.nhs.online.fidoclient.uaf.message.AuthenticationRequest
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AuthenticationRequestTest {

    private val gson = Gson()

    @Test
    fun testDeserialize() {
        val authRequest = gson.fromJson(getTestAuthRequest(), AuthenticationRequest::class.java)

        Assert.assertNotNull("Failed to deserialize to a AuthenticationRequest Object", authRequest)
        Assert.assertEquals("HQ1VkTUQC1NJDOo6OOWdxewrb9i5WthjfKIehFxpeuU", authRequest.challenge)
    }

    private fun getTestAuthRequest(): String {
        return "{\"header\": {\"upv\": {\"major\": 1,\"minor\": 0},\"op\": \"Auth\",\"appID\": \"https://uaf-test-1.noknoktest.com:8443/SampleApp/uaf/facets\",\"serverData\": \"5s7n8-7_LDAtRIKKYqbAtTTOezVKCjl2mPorYzbpxRrZ-_3wWroMXsF_pLYjNVm_l7bplAx4bkEwK6ibil9EHGfdfKOQ1q0tyEkNJFOgqdjVmLioroxgThlj8Istpt7q\"},\"challenge\": \"HQ1VkTUQC1NJDOo6OOWdxewrb9i5WthjfKIehFxpeuU\",\"policy\": {\"accepted\": [[{\"userVerification\": 512,\"keyProtection\": 1,\"tcDisplay\": 1,\"authenticationAlgorithms\": [1],\"assertionSchemes\": [\"UAFV1TLV\"]}],[{\"userVerification\": 4,\"keyProtection\": 1,\"tcDisplay\": 1,\"authenticationAlgorithms\": [1],\"assertionSchemes\": [\"UAFV1TLV\"]}],[{\"userVerification\": 4,\"keyProtection\": 1,\"tcDisplay\": 1,\"authenticationAlgorithms\": [2]}],[{\"userVerification\": 2,\"keyProtection\": 4,\"tcDisplay\": 1,\"authenticationAlgorithms\": [2]}],[{\"userVerification\": 4,\"keyProtection\": 2,\"tcDisplay\": 1,\"authenticationAlgorithms\": [1,3]}],[{\"userVerification\": 2,\"keyProtection\": 2,\"authenticationAlgorithms\": [2]}],[{\"userVerification\": 32,\"keyProtection\": 2,\"assertionSchemes\": [\"UAFV1TLV\"]},{\"userVerification\": 2,\"authenticationAlgorithms\": [1,3],\"assertionSchemes\": [\"UAFV1TLV\"]},{\"userVerification\": 2,\"authenticationAlgorithms\": [1,3],\"assertionSchemes\": [\"UAFV1TLV\"]},{\"userVerification\": 4,\"keyProtection\": 1,\"authenticationAlgorithms\": [1,3],\"assertionSchemes\": [\"UAFV1TLV\"]}]],\"disallowed\": [{\"userVerification\": 512,\"keyProtection\": 16,\"assertionSchemes\": [\"UAFV1TLV\"]},{\"userVerification\": 256,\"keyProtection\": 16}]}," +
                "\"transaction\":[{\"contentType\":0, \"content\": \"testContent\", \"tcDisplayPngCharacteristics\":{\"width\":100, \"height\":100, \"bitDepth\": \"8\", \"colorType\":\"testColorType\"," +
                "\"compression\": \"lossless\", \"filter\": \"testFilter\", \"interlace\": \"testInterlace\", \"plte\": [{\"r\":20, \"g\":20, \"b\":20}]}}]}"
    }
}