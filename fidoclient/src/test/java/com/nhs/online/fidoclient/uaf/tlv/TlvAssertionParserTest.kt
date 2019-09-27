package com.nhs.online.fidoclient.uaf.tlv

import com.google.gson.Gson
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhs.online.fidoclient.uaf.crypto.FidoSigner
import com.nhs.online.fidoclient.uaf.SampleCert
import com.nhs.online.fidoclient.uaf.client.AuthAssertionBuilder
import com.nhs.online.fidoclient.uaf.client.RegAssertionBuilder
import com.nhs.online.fidoclient.uaf.crypto.Base64url
import com.nhs.online.fidoclient.uaf.crypto.KeyCodec
import com.nhs.online.fidoclient.uaf.message.AuthenticationResponse
import com.nhs.online.fidoclient.uaf.message.RegistrationResponse
import org.junit.Assert
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.IOException
import java.security.KeyPair
import java.security.PrivateKey
import java.security.PublicKey
import java.security.Signature


@RunWith(RobolectricTestRunner::class)
class TlvAssertionParserTest {

    private val gson = Gson()
    private lateinit var parser: TlvAssertionParser
    private lateinit var mockKeyPair: KeyPair
    private lateinit var mockFidoSigner: FidoSigner
    private lateinit var authAssertionBuilder: AuthAssertionBuilder
    private lateinit var regAssertionBuilder: RegAssertionBuilder

    @Before
    fun setup() {
        parser = TlvAssertionParser()

        mockKeyPair = mock {
            on { public } doReturn getPublicKey()
            on { private } doReturn getPrivateKey()
        }
        mockFidoSigner = mock {
            on { sign(any(), any()) } doReturn byteArrayOf(1)
        }

        authAssertionBuilder = AuthAssertionBuilder(mockFidoSigner, mockKeyPair)
        regAssertionBuilder = RegAssertionBuilder(mockKeyPair.public, getSignature())
    }

    @Test
    @Throws(IOException::class)
    fun parserForAuthAssertion() {
        val raw: String?
        val list: Tags?

        raw = TestAssertions.exampleAuthAssertions
        list = parser.parse(raw)
        Assert.assertNotNull(list)

        assertTrue(list.getTags().size() > 0)
        assertTrue(list.getTags()[TagsEnum.UAF_CMD_STATUS_ERR_UNKNOWN.id] == null)
    }

    @Test
    @Throws(IOException::class)
    fun parserForAuthAssertionFromClient() {
        val raw: String?
        val list: Tags?

        raw = TestAssertions.exampleRegAssertionsFromClient
        list = parser.parse(raw)
        assertNotNull(list)

        assertTrue(list.getTags().size() > 0)
        assertTrue(list.getTags()[TagsEnum.UAF_CMD_STATUS_ERR_UNKNOWN.id] == null)
    }

    @Test
    @Throws(IOException::class)
    fun parserForAuthAssertionFromCertTool() {
        val raw: String?
        val list: Tags?

        raw = TestAssertions.exampleAuthAssertionsFromCertTool
        list = parser.parse(raw)
        assertNotNull(list)

        assertTrue(list.getTags().size() > 0)
        assertTrue(list.getTags()[TagsEnum.UAF_CMD_STATUS_ERR_UNKNOWN.id] == null)
    }

    @Test
    @Throws(Exception::class)
    fun forGeneratedAssertion() {
        val assertions = authAssertionBuilder.getAssertions(getAuthResponse())

        val tags = parser.parse(assertions)

        assertNotNull(tags)
    }

    @Test
    @Throws(IOException::class)
    fun parserForRegAssertion() {
        val raw: String?
        val list: Tags?
        raw = TestAssertions.exampleRegAssertions

        list = parser.parse(raw)

        checkRegTags(list)
    }

    @Test
    @Throws(IOException::class)
    fun parserForRegAssertionTest2() {
        val raw: String?
        val list: Tags?
        raw = TestAssertions.secondExampleRegAssertions

        list = parser.parse(raw)

        checkRegTags(list)
    }

    @Test
    @Throws(IOException::class)
    fun parserForRegAssertionRaon() {
        val raw: String?
        val list: Tags?
        raw = TestAssertions.regRequestAssertionsFromRaon()

        list = parser.parse(raw)

        checkRegTags(list)
    }

    @Test
    @Throws(IOException::class)
    fun forGeneratedRegAssertion() {
        val assertions = regAssertionBuilder.getAssertions(getRegResponse())

        val tags = parser.parse(assertions)

        checkRegTags(tags)
    }

    private fun getPublicKey(): PublicKey {
        return KeyCodec.getPubKey(Base64url.decode(SampleCert.PUBLIC_CERT))
    }

    private fun getPrivateKey(): PrivateKey {
        return KeyCodec.getPrivKey(Base64url.decode(SampleCert.PRIVATE_KEY))
    }

    private fun getAuthResponse(): AuthenticationResponse {
        val response =
            "{\"assertions\": [{\"assertion\": \"Aj7WAAQ-jgALLgkAQUJDRCNBQkNEDi4FAAABAQEADy4gAHwyJAEX8t1b2wOxbaKOC5ZL7ACqbLo_TtiQfK3DzDsHCi4gAFwCUz-dOuafXKXJLbkUrIzjAU6oDbP8B9iLQRmCf58fEC4AAAkuIABkwI-f3bIe_Uin6IKIFvqLgAOrpk6_nr0oVAK9hIl82A0uBAACAAAABi5AADwDOcBvPslX2bRNy4SvFhAwhEAoBSGUitgMUNChgUSMxss3K3ukekq1paG7Fv1v5mBmDCZVPt2NCTnjUxrjTp4\",\"assertionScheme\": \"UAFV1TLV\"}],\"fcParams\": \"eyJhcHBJRCI6Imh0dHBzOi8vdWFmLXRlc3QtMS5ub2tub2t0ZXN0LmNvbTo4NDQzL1NhbXBsZUFwcC91YWYvZmFjZXRzIiwiY2hhbGxlbmdlIjoiSFExVmtUVVFDMU5KRE9vNk9PV2R4ZXdyYjlpNVd0aGpmS0llaEZ4cGV1VSIsImNoYW5uZWxCaW5kaW5nIjp7fSwiZmFjZXRJRCI6ImNvbS5ub2tub2suYW5kcm9pZC5zYW1wbGVhcHAifQ\",\"header\": {\"appID\": \"https://acem.com\",\"op\": \"Auth\",\"serverData\": \"5s7n8-7_LDAtRIKKYqbAtTTOezVKCjl2mPorYzbpxRrZ-_3wWroMXsF_pLYjNVm_l7bplAx4bkEwK6ibil9EHGfdfKOQ1q0tyEkNJFOgqdjVmLioroxgThlj8Istpt7q\",\"upv\": {\"major\": 1,\"minor\": 0}}}"

        return gson.fromJson(response, AuthenticationResponse::class.java)
    }

    private fun getRegResponse(): RegistrationResponse {
        val response =
            "{\"assertions\":[{\"assertion\":\"AT7uAgM-sQALLgkAQUJDRCNBQkNEDi4HAAABAQEAAAEKLiAA9tBzZC64ecgVQBGSQb5QtEIPC8-Vav4HsHLZDflLaugJLiAAZMCPn92yHv1Ip-iCiBb6i4ADq6ZOv569KFQCvYSJfNgNLggAAQAAAAEAAAAMLkEABJsvEtUsVKh7tmYHhJ2FBm3kHU-OCdWiUYVijgYa81MfkjQ1z6UiHbKP9_nRzIN9anprHqDGcR6q7O20q_yctZAHPjUCBi5AACv8L7YlRMx10gPnszGO6rLFqZFmmRkhtV0TIWuWqYxd1jO0wxam7i5qdEa19u4sfpHFZ9RGI_WHxINkH8FfvAwFLu0BMIIB6TCCAY8CAQEwCQYHKoZIzj0EATB7MQswCQYDVQQGEwJVUzELMAkGA1UECAwCQ0ExCzAJBgNVBAcMAlBBMRAwDgYDVQQKDAdOTkwsSW5jMQ0wCwYDVQQLDAREQU4xMRMwEQYDVQQDDApOTkwsSW5jIENBMRwwGgYJKoZIhvcNAQkBFg1ubmxAZ21haWwuY29tMB4XDTE0MDgyODIxMzU0MFoXDTE3MDUyNDIxMzU0MFowgYYxCzAJBgNVBAYTAlVTMQswCQYDVQQIDAJDQTEWMBQGA1UEBwwNU2FuIEZyYW5jaXNjbzEQMA4GA1UECgwHTk5MLEluYzENMAsGA1UECwwEREFOMTETMBEGA1UEAwwKTk5MLEluYyBDQTEcMBoGCSqGSIb3DQEJARYNbm5sQGdtYWlsLmNvbTBZMBMGByqGSM49AgEGCCqGSM49AwEHA0IABCGBt3CIjnDowzSiF68C2aErYXnDUsWXOYxqIPim0OWg9FFdUYCa6AgKjn1R99Ek2d803sGKROivnavmdVH-SnEwCQYHKoZIzj0EAQNJADBGAiEAzAQujXnSS9AIAh6lGz6ydypLVTsTnBzqGJ4ypIqy_qUCIQCFsuOEGcRV-o4GHPBph_VMrG3NpYh2GKPjsAim_cSNmQ\",\"assertionScheme\":\"UAFV1TLV\"}],\"fcParams\":\"eyJhcHBJRCI6Imh0dHBzOi8vdWFmLXRlc3QtMS5ub2tub2t0ZXN0LmNvbTo4NDQzL1NhbXBsZUFwcC91YWYvZmFjZXRzIiwiY2hhbGxlbmdlIjoiSDlpVzl5QTlhQVhGX2xlbFFvaV9EaFVrNTE0QWQ4VHF2MHpDbkNxS0RwbyIsImNoYW5uZWxCaW5kaW5nIjp7fSwiZmFjZXRJRCI6ImNvbS5ub2tub2suYW5kcm9pZC5zYW1wbGVhcHAifQ\",\"header\":{\"appID\":\"https://acem.com\",\"op\":\"Reg\",\"serverData\":\"IjycjPZYiWMaQ1tKLrJROiXQHmYG0tSSYGjP5mgjsDaM17RQgq0dl3NNDDTx9d-aSR_6hGgclrU2F2Yj-12S67v5VmQHj4eWVseLulHdpk2v_hHtKSvv_DFqL4n2IiUY6XZWVbOnvg\",\"upv\":{\"major\":1,\"minor\":0}}}"

        return gson.fromJson(response, RegistrationResponse::class.java)
    }

    private fun checkRegTags(list: Tags) {
        assertNotNull(list)
        assertNotNull(list.getTags()[TagsEnum.TAG_UAFV1_REG_ASSERTION.id])
        assertNotNull(list.getTags()[TagsEnum.TAG_UAFV1_KRD.id])
        assertNotNull(list.getTags()[TagsEnum.TAG_PUB_KEY.id])
        assertNotNull(list.getTags()[TagsEnum.TAG_ASSERTION_INFO.id])
        assertTrue(list.getTags().size() > 0)
        assertTrue(list.getTags()[TagsEnum.UAF_CMD_STATUS_ERR_UNKNOWN.id] == null)
    }

    private fun getSignature(): Signature {
        val signature = java.security.Signature.getInstance("SHA256withECDSA")
        val privateKey = KeyCodec.getPrivKey(Base64url.decode(SampleCert.PRIVATE_KEY))
        signature.initSign(privateKey)
        return signature
    }

}