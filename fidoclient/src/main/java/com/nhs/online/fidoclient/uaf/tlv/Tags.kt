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

import android.util.SparseArray

class Tags {

    //    private val tags = HashMap<Int, Tag>()
    private val tags = SparseArray<Tag>()

    fun add(tag: Tag) {
        tags.put(tag.id, tag)
    }

    fun addAll(all: Tags) {
        for (index in 0 until all.tags.size()) {
            val tag = all.tags.valueAt(index)
            tags.put(tag.id, tag)
        }
    }

    fun getTags(): SparseArray<Tag> {
        return tags
    }

    override fun toString(): String {
        val result = StringBuilder()
        for (index in 0 until tags.size()) {
            val tag = tags.valueAt(index)
            result.append(", ")
            result.append(tag.toString())
        }
        return if (result.isEmpty()) "{}" else "{${result.substring(1)}}"
    }
}
