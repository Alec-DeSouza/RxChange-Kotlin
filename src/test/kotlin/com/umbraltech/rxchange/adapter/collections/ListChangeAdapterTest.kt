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

package com.umbraltech.rxchange.adapter.collections

import com.umbraltech.rxchange.filter.ChangeTypeFilter
import com.umbraltech.rxchange.type.ChangeType
import com.umbraltech.rxchange.util.ChangePayloadTestObserver
import com.umbraltech.rxchange.util.InvocationFailObserver
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ListChangeAdapterTest {
    private lateinit var changeAdapter: ListChangeAdapter<Int?>

    private val testList: List<Int> = listOf(0, 1, 2)

    @Before
    fun setUp() {
        changeAdapter = ListChangeAdapter()
    }

    @Test
    fun add() {
        val oldPayloadList: MutableList<List<Int?>> = mutableListOf(
                listOf(),
                listOf(0),
                listOf(0, 1)
        )

        val newPayloadList: MutableList<List<Int?>> = mutableListOf(
                listOf(0),
                listOf(0, 1),
                listOf(0, 1, 2)
        )

        changeAdapter.getObservable()
                .filter(ChangeTypeFilter(ChangeType.ADD))
                .subscribe(ChangePayloadTestObserver(oldPayloadList, newPayloadList))

        // Perform sequence of updates
        for (i: Int in testList) {
            assertTrue("Add", changeAdapter.add(i))
        }

        // Verify all payloads were tested
        assertEquals("Remaining old payload", 0, oldPayloadList.size)
        assertEquals("Remaining new payload", 0, newPayloadList.size)
    }

    @Test
    fun addAt() {
        val oldPayloadList: MutableList<List<Int?>> = mutableListOf(
                listOf(),
                listOf(0),
                listOf(0, 1)
        )

        val newPayloadList: MutableList<List<Int?>> = mutableListOf(
                listOf(0),
                listOf(0, 1),
                listOf(0, 1, 2)
        )

        changeAdapter.getObservable()
                .filter(ChangeTypeFilter(ChangeType.ADD))
                .subscribe(ChangePayloadTestObserver(oldPayloadList, newPayloadList))

        // Perform sequence of updates
        for (i: Int in testList.indices) {
            assertTrue("Add at $i", changeAdapter.addAt(i, testList[i]))
        }

        // Verify all payloads were tested
        assertEquals("Remaining old payload", 0, oldPayloadList.size)
        assertEquals("Remaining new payload", 0, newPayloadList.size)
    }

    @Test
    fun addAtNonExistent() {
        changeAdapter.getObservable()
                .filter(ChangeTypeFilter(ChangeType.ADD))
                .subscribe(InvocationFailObserver("Add invoked for invalid index"))

        assertFalse("Add at nonexistent", changeAdapter.addAt(1, 0))
    }

    @Test
    fun addAll() {
        val oldPayloadList: MutableList<List<Int?>> = mutableListOf(
                listOf()
        )

        val newPayloadList: MutableList<List<Int?>> = mutableListOf(
                listOf(0, 1, 2)
        )

        changeAdapter.getObservable()
                .filter(ChangeTypeFilter(ChangeType.ADD))
                .subscribe(ChangePayloadTestObserver(oldPayloadList, newPayloadList))

        // Perform batch update
        assertTrue("Add all", changeAdapter.addAll(testList))

        // Verify all payloads were tested
        assertEquals("Remaining old payload", 0, oldPayloadList.size)
        assertEquals("Remaining new payload", 0, newPayloadList.size)
    }

    @Test
    fun remove() {
        val oldPayloadList: MutableList<List<Int?>> = mutableListOf(
                listOf(0, 1, 2),
                listOf(1, 2),
                listOf(2)
        )

        val newPayloadList: MutableList<List<Int?>> = mutableListOf(
                listOf(1, 2),
                listOf(2),
                listOf()
        )

        // Set up initial values
        changeAdapter.addAll(testList)

        changeAdapter.getObservable()
                .filter(ChangeTypeFilter(ChangeType.REMOVE))
                .subscribe(ChangePayloadTestObserver(oldPayloadList, newPayloadList))

        // Perform sequence of updates
        for (i: Int in testList) {
            assertTrue("Remove", changeAdapter.remove(i))
        }

        // Verify all payloads were tested
        assertEquals("Remaining old payload", 0, oldPayloadList.size)
        assertEquals("Remaining new payload", 0, newPayloadList.size)
    }

    @Test
    fun removeNonExistent() {
        changeAdapter.getObservable()
                .filter(ChangeTypeFilter(ChangeType.REMOVE))
                .subscribe(InvocationFailObserver("Remove invoked for nonexistent entry"))

        assertFalse("Remove nonexistent", changeAdapter.remove(0))
    }

    @Test
    fun removeAt() {
        val oldPayloadList: MutableList<List<Int?>> = mutableListOf(
                listOf(0, 1, 2),
                listOf(1, 2),
                listOf(2)
        )

        val newPayloadList: MutableList<List<Int?>> = mutableListOf(
                listOf(1, 2),
                listOf(2),
                listOf()
        )

        // Set up initial values
        changeAdapter.addAll(testList)

        changeAdapter.getObservable()
                .filter(ChangeTypeFilter(ChangeType.REMOVE))
                .subscribe(ChangePayloadTestObserver(oldPayloadList, newPayloadList))

        // Perform sequence of updates
        for (i: Int in 0 until testList.size) {
            assertTrue("Remove at", changeAdapter.removeAt(0))
        }

        // Verify all payloads were tested
        assertEquals("Remaining old payload", 0, oldPayloadList.size)
        assertEquals("Remaining new payload", 0, newPayloadList.size)
    }

    @Test
    fun removeAtNonExistent() {
        changeAdapter.getObservable()
                .filter(ChangeTypeFilter(ChangeType.REMOVE))
                .subscribe(InvocationFailObserver("Remove invoked for invalid index"))

        assertFalse("Remove at nonexistent", changeAdapter.removeAt(0))
    }

    @Test
    fun removeAll() {
        val oldPayloadList: MutableList<List<Int?>> = mutableListOf(
                listOf(0, 1, 2)
        )

        val newPayloadList: MutableList<List<Int?>> = mutableListOf(
                listOf()
        )

        // Set up initial values
        changeAdapter.addAll(testList)

        changeAdapter.getObservable()
                .filter(ChangeTypeFilter(ChangeType.REMOVE))
                .subscribe(ChangePayloadTestObserver(oldPayloadList, newPayloadList))

        // Perform batch update
        assertTrue("Remove", changeAdapter.removeAll(testList))

        // Verify all payloads were tested
        assertEquals("Remaining old payload", 0, oldPayloadList.size)
        assertEquals("Remaining new payload", 0, newPayloadList.size)
    }

    @Test
    fun removeAllNonExistent() {
        changeAdapter.getObservable()
                .filter(ChangeTypeFilter(ChangeType.REMOVE))
                .subscribe(InvocationFailObserver("Remove invoked for nonexistent entries"))

        assertFalse("Remove all nonexistent", changeAdapter.removeAll(testList))
    }

    @Test
    fun update() {
        val finalTestList: List<Int> = listOf(1, 2, 3)

        val oldPayloadList: MutableList<List<Int?>> = mutableListOf(
                listOf(0, 1, 2),
                listOf(1, 1, 2),
                listOf(1, 2, 2)
        )

        val newPayloadList: MutableList<List<Int?>> = mutableListOf(
                listOf(1, 1, 2),
                listOf(1, 2, 2),
                listOf(1, 2, 3)
        )

        // Set up initial values
        changeAdapter.addAll(testList)

        changeAdapter.getObservable()
                .filter(ChangeTypeFilter(ChangeType.UPDATE))
                .subscribe(ChangePayloadTestObserver(oldPayloadList, newPayloadList))

        // Perform sequence of updates
        for (i: Int in 0 until testList.size) {
            assertTrue("Update", changeAdapter.update(i, finalTestList[i]))
        }

        // Verify all payloads were tested
        assertEquals("Remaining old payload", 0, oldPayloadList.size)
        assertEquals("Remaining new payload", 0, newPayloadList.size)
    }

    @Test
    fun updateNonExistent() {
        changeAdapter.getObservable()
                .filter(ChangeTypeFilter(ChangeType.UPDATE))
                .subscribe(InvocationFailObserver("Update invoked for nonexistent entry"))

        assertFalse("Update nonexistent", changeAdapter.update(0, 1))
    }

    @Test
    fun get() {
        // Set up initial values
        changeAdapter.addAll(testList)

        for (i: Int in 0 until testList.size) {
            assertEquals("Get", testList[i], changeAdapter[i])
        }
    }

    @Test
    fun getAll() {
        // Set up initial values
        changeAdapter.addAll(testList)

        assertEquals("Get all", testList, changeAdapter.getAll())
    }
}