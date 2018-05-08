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

package com.umbraltech.rxchange.adapter

import com.umbraltech.rxchange.filter.ChangeTypeFilter
import com.umbraltech.rxchange.type.ChangeType
import com.umbraltech.rxchange.util.ChangePayloadTestObserver
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SingleChangeAdapterTest {
    private lateinit var changeAdapter: SingleChangeAdapter<Int>

    private val testList: List<Int> = listOf(1, 2, 3)

    @Before
    fun setUp() {
        changeAdapter = SingleChangeAdapter(0)
    }

    @Test
    fun update() {
        val oldPayloadList: MutableList<Int?> = mutableListOf(0, 1, 2)
        val newPayloadList: MutableList<Int?> = mutableListOf(1, 2, 3)

        changeAdapter.getObservable()
                .filter(ChangeTypeFilter(ChangeType.UPDATE))
                .subscribe(ChangePayloadTestObserver(oldPayloadList, newPayloadList))

        for (i: Int in testList) {
            assertTrue("Update", changeAdapter.update(i))
        }

        assertEquals("Remaining old payload", 0, oldPayloadList.size)
        assertEquals("Remaining new payload", 0, newPayloadList.size)
    }

    @Test
    fun get() {
        changeAdapter.update(testList[0])
        assertEquals("Data", testList[0], changeAdapter.get())
    }
}