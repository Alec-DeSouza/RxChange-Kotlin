/*
 * Copyright 2018 - present, RxChange contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.umbraltech.rxchange.message

import com.umbraltech.rxchange.type.ChangeType
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ChangeMessageTest {
    private lateinit var changeMessage: ChangeMessage<Int>

    @Before
    fun setUp() {
        changeMessage = ChangeMessage(1, 2, ChangeType.UPDATE, 2)
    }

    @Test
    fun getOldData() {
        assertEquals("Old data", 1, changeMessage.oldData)
    }

    @Test
    fun getNewData() {
        assertEquals("New data", 2, changeMessage.newData)
    }

    @Test
    fun getChangeType() {
        assertEquals("Change type", ChangeType.UPDATE, changeMessage.changeType)
    }

    @Test
    fun getChangeSnapshot() {
        assertEquals("Change snapshot", 2, changeMessage.changeSnapshot)
    }

    @Test
    fun getString() {
        val testMessage = "${ChangeMessage::class.simpleName}(oldData=${changeMessage.oldData}, newData=${changeMessage.newData}, changeType=${changeMessage.changeType}, changeSnapshot=${changeMessage.changeSnapshot})"

        assertEquals("toString", testMessage, changeMessage.toString())
    }
}