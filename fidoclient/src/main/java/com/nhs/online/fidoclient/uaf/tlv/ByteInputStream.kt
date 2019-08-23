/*
 * Copyright 2014 Yubico.
 *
 * Modified heavily (including conversion to Kotlin) by NHS App
 */

package com.nhs.online.fidoclient.uaf.tlv

import java.io.ByteArrayInputStream
import java.io.DataInputStream
import java.io.IOException

/**
 * Provides an easy way to read a byte array in chunks.
 */
class ByteInputStream(data: ByteArray) : DataInputStream(ByteArrayInputStream(data)) {

    fun read(numberOfBytes: Int): ByteArray {
        val readBytes = ByteArray(numberOfBytes)
        try {
            readFully(readBytes)
        } catch (e: IOException) {
            throw AssertionError()
        }

        return readBytes
    }

    fun readAll(): ByteArray {
        try {
            val readBytes = ByteArray(available())
            readFully(readBytes)
            return readBytes
        } catch (e: IOException) {
            throw AssertionError()
        }

    }
}
