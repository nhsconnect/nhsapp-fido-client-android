package com.nhs.online.fidoclient.uaf.client.operation

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.Signature
import com.google.gson.Gson
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhs.online.fidoclient.uaf.SampleCert
import com.nhs.online.fidoclient.uaf.client.AuthAssertionBuilder
import com.nhs.online.fidoclient.uaf.crypto.Base64url
import com.nhs.online.fidoclient.uaf.crypto.FidoSigner
import com.nhs.online.fidoclient.uaf.crypto.KeyCodec
import com.nhs.online.fidoclient.uaf.message.AuthenticationRequest
import com.nhs.online.fidoclient.uaf.operationcall.AuthenticationCall
import com.nhs.online.fidoclient.utils.fidoHelpers
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.*
import org.robolectric.RobolectricTestRunner
import java.security.KeyPair
import java.security.PublicKey

@RunWith(RobolectricTestRunner::class)
class AuthenticationTest {
    private lateinit var dataToReturn: ByteArray
    private lateinit var assertionBuilder: AuthAssertionBuilder
    private lateinit var mockAuthenticationCall: AuthenticationCall
    private lateinit var mockContext: Context
    private lateinit var packageManager: PackageManager
    private lateinit var packageInfo: PackageInfo
    private lateinit var signature: Signature
    private val key: PublicKey? = getKey()

    private lateinit var auth: Authentication


    @Before
    fun setup() {
        dataToReturn = byteArrayOf(1)
        signature = Signature(getSignature())

        packageInfo = PackageInfo()
        packageInfo.packageName = "testPackageName"
        packageInfo.signatures = arrayOf(signature)

        packageManager = mock {
            on { getPackageInfo(any<String>(), anyInt()) } doReturn packageInfo
        }

        mockContext = mock {
            on { packageName } doReturn "testPackageName"
            on { packageManager } doReturn packageManager
        }

        mockAuthenticationCall = mock {
            on { getUafMessageRequest(any(), anyBoolean(), anyString()) } doReturn "{}"
        }


        auth = Authentication(mockAuthenticationCall)


        val mockFidoSigner: FidoSigner = mock {
            on { sign(any(), any()) } doReturn dataToReturn
        }

        val mockKeyPair: KeyPair = mock {
            on { public } doReturn key
        }
        assertionBuilder = AuthAssertionBuilder(mockFidoSigner, mockKeyPair)
    }

    @Test
    fun testAuth() {
        val uafMessage = auth.auth(getTestAuthRequest(), assertionBuilder)

        Assert.assertNotNull(uafMessage)
    }

    @Test
    fun testUAFProtocolMessage() {
        val testMessageJSON = "{test: message}"

        Assert.assertEquals("{\"uafProtocolMessage\":\"{test: message}\"}",
            auth.getUafProtocolMessage(testMessageJSON))
    }

    @Test
    fun requestUafAuthenticationMsg_ReturnsNonEmptyUafMsg() {
        val facetId = fidoHelpers.getFacetId(mockContext)
        Assert.assertNotNull(facetId)
        val regLoginUafMsg = auth.requestUafAuthenticationMessage(facetId!!, "/authRequest")
        Assert.assertNotNull(regLoginUafMsg)
        Assert.assertFalse("Uaf authentication message can't be empty", regLoginUafMsg.isEmpty())
        Assert.assertFalse("Escape double quotes is (\\\") is not expected",
            regLoginUafMsg.contains("\\\""))
    }

    @Test
    fun testProcessAuthenticationRequest() {
        val request = getTestAuthRequest()
        val authReq = Gson().fromJson(request, Array<AuthenticationRequest>::class.java)[0]

        val response = auth.processRequest(authReq, assertionBuilder)

        Assert.assertNotNull(response)
    }

    private fun getTestAuthRequest(): String {
        return "[{\"header\": {\"upv\": {\"major\": 1,\"minor\": 0},\"op\": \"Auth\",\"appID\": \"https://uaf-test-1.noknoktest.com:8443/SampleApp/uaf/facets\",\"serverData\": \"5s7n8-7_LDAtRIKKYqbAtTTOezVKCjl2mPorYzbpxRrZ-_3wWroMXsF_pLYjNVm_l7bplAx4bkEwK6ibil9EHGfdfKOQ1q0tyEkNJFOgqdjVmLioroxgThlj8Istpt7q\"},\"challenge\": \"HQ1VkTUQC1NJDOo6OOWdxewrb9i5WthjfKIehFxpeuU\",\"policy\": {\"accepted\": [[{\"userVerification\": 512,\"keyProtection\": 1,\"tcDisplay\": 1,\"authenticationAlgorithms\": [1],\"assertionSchemes\": [\"UAFV1TLV\"]}],[{\"userVerification\": 4,\"keyProtection\": 1,\"tcDisplay\": 1,\"authenticationAlgorithms\": [1],\"assertionSchemes\": [\"UAFV1TLV\"]}],[{\"userVerification\": 4,\"keyProtection\": 1,\"tcDisplay\": 1,\"authenticationAlgorithms\": [2]}],[{\"userVerification\": 2,\"keyProtection\": 4,\"tcDisplay\": 1,\"authenticationAlgorithms\": [2]}],[{\"userVerification\": 4,\"keyProtection\": 2,\"tcDisplay\": 1,\"authenticationAlgorithms\": [1,3]}],[{\"userVerification\": 2,\"keyProtection\": 2,\"authenticationAlgorithms\": [2]}],[{\"userVerification\": 32,\"keyProtection\": 2,\"assertionSchemes\": [\"UAFV1TLV\"]},{\"userVerification\": 2,\"authenticationAlgorithms\": [1,3],\"assertionSchemes\": [\"UAFV1TLV\"]},{\"userVerification\": 2,\"authenticationAlgorithms\": [1,3],\"assertionSchemes\": [\"UAFV1TLV\"]},{\"userVerification\": 4,\"keyProtection\": 1,\"authenticationAlgorithms\": [1,3],\"assertionSchemes\": [\"UAFV1TLV\"]}]],\"disallowed\": [{\"userVerification\": 512,\"keyProtection\": 16,\"assertionSchemes\": [\"UAFV1TLV\"]},{\"userVerification\": 256,\"keyProtection\": 16}]}}]"

    }

    private fun getKey(): PublicKey? {
        return KeyCodec.getPubKey(Base64url.decode(SampleCert.PUBLIC_CERT))
    }

    private fun getSignature(): String {
        return "308201dd30820146020101300d06092a864886f70d010105050030373116301406035504030c0d416e64726f69642044656275673110300e060355040a0c07416e64726f6964310b3009060355040613025553301e170d3138303733313038323930365a170d3438303732333038323930365a30373116301406035504030c0d416e64726f69642044656275673110300e060355040a0c07416e64726f6964310b300906035504061302555330819f300d06092a864886f70d010101050003818d0030818902818100d68bb09bc83dcf88ef5d4120d753e2df881ba938358b865206380c40b5dff779ffa51e7244fb74edbfbff7e44cc4485849280d5c7299a872592ccacdf4daa1e09e0200ad74acbe4858ff320906034ef21c0fd467c71c0a0b1cb39ea58700d54f2b4976f2fbae6c381cea85d9379a825c70c139dbfe9daf25013407fc9e50f1bb0203010001300d06092a864886f70d010105050003818100a1cdabb8310ef0dac7cc688f1fc6f4de1d25c3b666c0f70211f836629603ea7241a458c9506bfd4677c7a2de67f38f5259dbb36ad4094154451985fe6fa00e7ac9c929b4762bb855ddbf245fd898051987de32feee42c6e586914d26854a0c5b1431302074e2c31075e2e8979b3c35b5daa664edd200ea82bd3bfed1c0568df2"
    }

}