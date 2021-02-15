package com.nhs.online.fidoclient.uaf.client.operation

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.Signature
import com.google.gson.Gson
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhs.online.fidoclient.uaf.SampleCert
import com.nhs.online.fidoclient.uaf.client.RegAssertionBuilder
import com.nhs.online.fidoclient.uaf.crypto.Base64url
import com.nhs.online.fidoclient.uaf.crypto.FidoKeystoreAndroidM
import com.nhs.online.fidoclient.uaf.crypto.FidoSigner
import com.nhs.online.fidoclient.uaf.crypto.KeyCodec
import com.nhs.online.fidoclient.uaf.message.RegistrationRequest
import com.nhs.online.fidoclient.uaf.operationcall.RegistrationCall
import com.nhs.online.fidoclient.utils.fidoHelpers
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.security.KeyPair
import java.security.PublicKey

@RunWith(RobolectricTestRunner::class)
class RegistrationTest {
    private lateinit var dataToReturn: ByteArray
    private lateinit var mockFidoSigner: FidoSigner
    private lateinit var mockKeyPair: KeyPair
    private lateinit var mockContext: Context
    private lateinit var mockFidoKeystore: FidoKeystoreAndroidM
    private lateinit var mockRegistrationCall: RegistrationCall
    private lateinit var packageManager: PackageManager
    private lateinit var packageInfo: PackageInfo
    private lateinit var signature: Signature
    private lateinit var registration: Registration
    private lateinit var gson: Gson

    @Before
    fun setup() {

        gson = Gson()

        val publicKey = getKey()
        Assert.assertNotNull(publicKey)

        mockKeyPair = mock {
            on { public } doReturn publicKey
        }

        mockFidoKeystore = mock {
            on { generateKeyPair("username") } doReturn mockKeyPair
            on { getPublicKey("username") } doReturn publicKey!!
        }

        mockRegistrationCall = mock {
            on { getUafRegistrationMessage(any<String>(), any<String>(), any<String>()) } doReturn "{}"
        }

        dataToReturn = byteArrayOf(1)
        signature = Signature(getSignature())

        packageInfo = PackageInfo()
        packageInfo.packageName = "testPackageName"
        packageInfo.signatures = arrayOf(signature)

        packageManager = mock {
            on { getPackageInfo(any<String>(), any<Int>()) } doReturn packageInfo
        }

        mockContext = mock {
            on { packageName } doReturn "testPackageName"
            on { packageManager } doReturn packageManager
        }

        registration =
                Registration(mockRegistrationCall)

        mockFidoSigner = mock {
            on { sign(any<ByteArray>(), anyOrNull<KeyPair>()) } doReturn dataToReturn
        }
    }

    @Test
    fun testReg() {
        val assertionBuilder = RegAssertionBuilder(mockKeyPair.public, getJavaSecSignature())
        val uafMessage =
            registration.processRegisterMessage(getTestRequestAsJsonString(), assertionBuilder)
        Assert.assertNotNull(uafMessage)
    }

    @Test
    fun testProcessRequest() {
        val assertionBuilder = RegAssertionBuilder(mockKeyPair.public, getJavaSecSignature())
        val request =
            gson.fromJson(getTestRequestAsJsonString(), Array<RegistrationRequest>::class.java)[0]
        Assert.assertNotNull(request)
        val response = registration.processRequest(request, assertionBuilder)
        Assert.assertNotNull(response)
    }

    @Test
    fun getRegistrationRequest() {
        val req = registration.getRegistrationRequest(getTestRequestAsJsonString())

        val registrationRequest = gson.fromJson(getTestRequestAsJsonString(),
            Array<RegistrationRequest>::class.java)[0]

        Assert.assertNotNull(registrationRequest)
        Assert.assertEquals(registrationRequest.header!!.appID, req.header!!.appID)
    }

    @Test
    fun testUAFProtocolMessage() {
        val testMessageJSON = "{test: message}"

        Assert.assertEquals("{\"uafProtocolMessage\":\"{test: message}\"}",
            registration.getUafProtocolMessage(testMessageJSON))
    }

    @Test
    fun requestUafRegistrationMsg_ReturnsNonEmptyRegMessage() {
        val facetId = fidoHelpers.getFacetId(mockContext)
        Assert.assertNotNull(facetId)
        val uafRegMsg = registration.requestUafRegistrationMessage(facetId!!, "accessToken", "/getReg")
        Assert.assertNotNull(uafRegMsg)
        Assert.assertFalse("Registration Uaf message can't be empty", uafRegMsg.isEmpty())
        Assert.assertFalse("Escape double quates is (\\\") is not expected",
            uafRegMsg.contains("\\\""))
    }

    private fun getTestRequestAsJsonString(): String {
        return "[{\"header\":{\"upv\":{\"major\":1,\"minor\":0},\"op\":\"Reg\",\"appID\":\"https://acem.com\",\"serverData\":\"IjycjPZYiWMaQ1tKLrJROiXQHmYG0tSSYGjP5mgjsDaM17RQgq0dl3NNDDTx9d-aSR_6hGgclrU2F2Yj-12S67v5VmQHj4eWVseLulHdpk2v_hHtKSvv_DFqL4n2IiUY6XZWVbOnvg\"},\"challenge\":\"H9iW9yA9aAXF_lelQoi_DhUk514Ad8Tqv0zCnCqKDpo\",\"username\":\"apa\",\"policy\":{\"accepted\":[[{\"userVerification\":512,\"keyProtection\":1,\"tcDisplay\":1,\"authenticationAlgorithms\":[1],\"assertionSchemes\":[\"UAFV1TLV\"]}],[{\"userVerification\":4,\"keyProtection\":1,\"tcDisplay\":1,\"authenticationAlgorithms\":[1],\"assertionSchemes\":[\"UAFV1TLV\"]}],[{\"userVerification\":4,\"keyProtection\":1,\"tcDisplay\":1,\"authenticationAlgorithms\":[2]}],[{\"userVerification\":2,\"keyProtection\":4,\"tcDisplay\":1,\"authenticationAlgorithms\":[2]}],[{\"userVerification\":4,\"keyProtection\":2,\"tcDisplay\":1,\"authenticationAlgorithms\":[1,3]}],[{\"userVerification\":2,\"keyProtection\":2,\"authenticationAlgorithms\":[2]}],[{\"userVerification\":32,\"keyProtection\":2,\"assertionSchemes\":[\"UAFV1TLV\"]},{\"userVerification\":2,\"authenticationAlgorithms\":[1,3],\"assertionSchemes\":[\"UAFV1TLV\"]},{\"userVerification\":2,\"authenticationAlgorithms\":[1,3],\"assertionSchemes\":[\"UAFV1TLV\"]},{\"userVerification\":4,\"keyProtection\":1,\"authenticationAlgorithms\":[1,3],\"assertionSchemes\":[\"UAFV1TLV\"]}]],\"disallowed\":[{\"userVerification\":512,\"keyProtection\":16,\"assertionSchemes\":[\"UAFV1TLV\"]},{\"userVerification\":256,\"keyProtection\":16},{\"aaid\":[\"ABCD#ABCD\"],\"keyIDs\":[\"RfY_RDhsf4z5PCOhnZExMeVloZZmK0hxaSi10tkY_c4\"]}]}}]"
    }

    private fun getKey(): PublicKey? {
        return KeyCodec.getPubKey(Base64url.decode(SampleCert.PUBLIC_CERT))
    }

    private fun getJavaSecSignature(): java.security.Signature {
        val signature = java.security.Signature.getInstance("SHA256withECDSA")
        val privateKey = KeyCodec.getPrivKey(Base64url.decode(SampleCert.PRIVATE_KEY))
        signature.initSign(privateKey)
        return signature
    }

    private fun getSignature(): String {
        return "308201dd30820146020101300d06092a864886f70d010105050030373116301406035504030c0d416e64726f69642044656275673110300e060355040a0c07416e64726f6964310b3009060355040613025553301e170d3138303733313038323930365a170d3438303732333038323930365a30373116301406035504030c0d416e64726f69642044656275673110300e060355040a0c07416e64726f6964310b300906035504061302555330819f300d06092a864886f70d010101050003818d0030818902818100d68bb09bc83dcf88ef5d4120d753e2df881ba938358b865206380c40b5dff779ffa51e7244fb74edbfbff7e44cc4485849280d5c7299a872592ccacdf4daa1e09e0200ad74acbe4858ff320906034ef21c0fd467c71c0a0b1cb39ea58700d54f2b4976f2fbae6c381cea85d9379a825c70c139dbfe9daf25013407fc9e50f1bb0203010001300d06092a864886f70d010105050003818100a1cdabb8310ef0dac7cc688f1fc6f4de1d25c3b666c0f70211f836629603ea7241a458c9506bfd4677c7a2de67f38f5259dbb36ad4094154451985fe6fa00e7ac9c929b4762bb855ddbf245fd898051987de32feee42c6e586914d26854a0c5b1431302074e2c31075e2e8979b3c35b5daa664edd200ea82bd3bfed1c0568df2"
    }


}