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

package com.umbraltech.rxchange.filter

import com.umbraltech.rxchange.message.ChangeMessage
import com.umbraltech.rxchange.type.ChangeType
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ChangeTypeFilterTest {
    private lateinit var changeMessage: ChangeMessage<Int>

    @Before
    fun setUp() {
        changeMessage = ChangeMessage(0, 1, ChangeType.UPDATE, 1)
    }

    @Test
    fun test() {
        val addTypeFilter = ChangeTypeFilter(ChangeType.ADD)
        val removeTypeFilter = ChangeTypeFilter(ChangeType.REMOVE)
        val updateTypeFilter = ChangeTypeFilter(ChangeType.UPDATE)

        assertFalse("Change type add", addTypeFilter.test(changeMessage))
        assertFalse("Change type remove", removeTypeFilter.test(changeMessage))
        assertTrue("Change type update", updateTypeFilter.test(changeMessage))
    }
}