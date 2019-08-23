package com.nhs.online.fidoclient.uaf.crypto

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SHATest {

    private val baseString = "AString"
    private val differentString = "AnotherString"

    @Test
    fun sha256Test() {
        val sha256 = SHA.sha256(baseString)
        Assert.assertNotNull(sha256)
        Assert.assertNotEquals(sha256, baseString)
    }

    @Test
    fun uniqueResult() {
        val sha1 = SHA.sha256(baseString)
        val sha2 = SHA.sha256(differentString)
        Assert.assertNotEquals(sha1, sha2)
    }

    @Test
    fun deterministic() {
        val sha1 = SHA.sha256(baseString)
        Assert.assertEquals(sha1, SHA.sha256(baseString))
    }
}