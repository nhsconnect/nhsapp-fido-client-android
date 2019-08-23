/*
 * Copyright 2015 eBay Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Modified heavily (including conversion to Kotlin) by NHS App
 */

package com.nhs.online.fidoclient.uaf.tlv

class Tag {
    var statusId = 0x00
    var id: Int = 0
    var length: Int = 0
    var value: ByteArray = byteArrayOf()

    override fun toString(): String {
        var tag = "Tag id:$id"
        tag = tag + " Tag name: " + TagsEnum[id]
        if (value.isNotEmpty()) {
            tag = tag + " Tag value:" + android.util.Base64.encode(value, android.util.Base64.URL_SAFE)
        }
        return tag
    }

}
