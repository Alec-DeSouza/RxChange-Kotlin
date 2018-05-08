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

class MapChangeAdapterTest {
    private lateinit var changeAdapter: MapChangeAdapter<Int, String?>

    private val testMap: Map<Int, String> = linkedMapOf(0 to "0", 1 to "1", 2 to "2")

    @Before
    fun setUp() {
        changeAdapter = MapChangeAdapter()
    }

    @Test
    fun add() {
        val oldPayloadList: MutableList<Map<Int, String?>> = mutableListOf(
                mapOf(),
                mapOf(0 to "0"),
                mapOf(0 to "0", 1 to "1")
        )

        val newPayloadList: MutableList<Map<Int, String?>> = mutableListOf(
                mapOf(0 to "0"),
                mapOf(0 to "0", 1 to "1"),
                mapOf(0 to "0", 1 to "1", 2 to "2")
        )

        changeAdapter.getObservable()
                .filter(ChangeTypeFilter(ChangeType.ADD))
                .subscribe(ChangePayloadTestObserver(oldPayloadList, newPayloadList))

        // Perform sequence of updates
        for ((key: Int, data: String) in testMap) {
            assertTrue("Add", changeAdapter.add(key, data))
        }

        // Verify all payloads were tested
        assertEquals("Remaining old payload", 0, oldPayloadList.size)
        assertEquals("Remaining new payload", 0, newPayloadList.size)
    }

    @Test
    fun addExisting() {
        // Set up initial values
        changeAdapter.addAll(testMap)

        changeAdapter.getObservable()
                .filter(ChangeTypeFilter(ChangeType.ADD))
                .subscribe(InvocationFailObserver("Add invoked for existing entry"))

        for ((key: Int, data: String) in testMap) {
            assertFalse("Add existing", changeAdapter.add(key, data))
        }
    }

    @Test
    fun addAll() {
        val oldPayloadList: MutableList<Map<Int, String?>> = mutableListOf(
                mapOf()
        )

        val newPayloadList: MutableList<Map<Int, String?>> = mutableListOf(
                mapOf(0 to "0", 1 to "1", 2 to "2")
        )

        changeAdapter.getObservable()
                .filter(ChangeTypeFilter(ChangeType.ADD))
                .subscribe(ChangePayloadTestObserver(oldPayloadList, newPayloadList))

        // Perform batch update
        assertTrue("Add all", changeAdapter.addAll(testMap))

        // Verify all payloads were tested
        assertEquals("Remaining old payload", 0, oldPayloadList.size)
        assertEquals("Remaining new payload", 0, newPayloadList.size)
    }

    @Test
    fun addAllExisting() {
        // Set up initial values
        changeAdapter.addAll(testMap)

        changeAdapter.getObservable()
                .filter(ChangeTypeFilter(ChangeType.ADD))
                .subscribe(InvocationFailObserver("Add invoked for existing entries"))

        assertFalse("Add all existing", changeAdapter.addAll(testMap))
    }

    @Test
    fun remove() {
        val oldPayloadList: MutableList<Map<Int, String?>> = mutableListOf(
                mapOf(0 to "0", 1 to "1", 2 to "2"),
                mapOf(1 to "1", 2 to "2"),
                mapOf(2 to "2")
        )

        val newPayloadList: MutableList<Map<Int, String?>> = mutableListOf(
                mapOf(1 to "1", 2 to "2"),
                mapOf(2 to "2"),
                mapOf()
        )

        // Set up initial values
        changeAdapter.addAll(testMap)

        changeAdapter.getObservable()
                .filter(ChangeTypeFilter(ChangeType.REMOVE))
                .subscribe(ChangePayloadTestObserver(oldPayloadList, newPayloadList))

        // Perform sequence of updates
        for (key: Int in testMap.keys) {
            assertTrue("Remove", changeAdapter.remove(key))
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
    fun removeAll() {
        val oldPayloadList: MutableList<Map<Int, String?>> = mutableListOf(
                mapOf(0 to "0", 1 to "1", 2 to "2")
        )

        val newPayloadList: MutableList<Map<Int, String?>> = mutableListOf(
                mapOf()
        )

        // Set up initial values
        changeAdapter.addAll(testMap)

        changeAdapter.getObservable()
                .filter(ChangeTypeFilter(ChangeType.REMOVE))
                .subscribe(ChangePayloadTestObserver(oldPayloadList, newPayloadList))

        // Perform batch update
        assertTrue("Remove all", changeAdapter.removeAll(testMap.keys))

        // Verify all payloads were tested
        assertEquals("Remaining old payload", 0, oldPayloadList.size)
        assertEquals("Remaining new payload", 0, newPayloadList.size)
    }

    @Test
    fun removeAllNonExistent() {
        changeAdapter.getObservable()
                .filter(ChangeTypeFilter(ChangeType.REMOVE))
                .subscribe(InvocationFailObserver("Remove invoked for nonexistent entries"))

        assertFalse("Remove all nonexistent", changeAdapter.removeAll(testMap.keys))
    }

    @Test
    fun update() {
        val finalTestMap: Map<Int, String> = linkedMapOf(0 to "1", 1 to "2", 2 to "3")

        val oldPayloadList: MutableList<Map<Int, String?>> = mutableListOf(
                mapOf(0 to "0", 1 to "1", 2 to "2"),
                mapOf(0 to "1", 1 to "1", 2 to "2"),
                mapOf(0 to "1", 1 to "2", 2 to "2")
        )

        val newPayloadList: MutableList<Map<Int, String?>> = mutableListOf(
                mapOf(0 to "1", 1 to "1", 2 to "2"),
                mapOf(0 to "1", 1 to "2", 2 to "2"),
                mapOf(0 to "1", 1 to "2", 2 to "3")
        )

        // Set up initial values
        changeAdapter.addAll(testMap)

        changeAdapter.getObservable()
                .filter(ChangeTypeFilter(ChangeType.UPDATE))
                .subscribe(ChangePayloadTestObserver(oldPayloadList, newPayloadList))

        // Perform sequence of updates
        for ((key: Int, data: String) in finalTestMap) {
            assertTrue("Update", changeAdapter.update(key, data))
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

        assertFalse("Update nonexistent", changeAdapter.update(0, "0"))
    }

    @Test
    fun updateAll() {
        val finalTestMap: Map<Int, String> = linkedMapOf(0 to "1", 1 to "2", 2 to "3")

        val oldPayloadList: MutableList<Map<Int, String?>> = mutableListOf(
                mapOf(0 to "0", 1 to "1", 2 to "2")
        )

        val newPayloadList: MutableList<Map<Int, String?>> = mutableListOf(
                mapOf(0 to "1", 1 to "2", 2 to "3")
        )

        // Set up initial values
        changeAdapter.addAll(testMap)

        changeAdapter.getObservable()
                .filter(ChangeTypeFilter(ChangeType.UPDATE))
                .subscribe(ChangePayloadTestObserver(oldPayloadList, newPayloadList))

        // Perform batch update
        assertTrue("Update all", changeAdapter.updateAll(finalTestMap))

        // Verify all payloads were tested
        assertEquals("Remaining old payload", 0, oldPayloadList.size)
        assertEquals("Remaining new payload", 0, newPayloadList.size)
    }

    @Test
    fun updateAllNonExistent() {
        changeAdapter.getObservable()
                .filter(ChangeTypeFilter(ChangeType.UPDATE))
                .subscribe(InvocationFailObserver("Update invoked for nonexistent entries"))

        assertFalse("Update all nonexistent", changeAdapter.updateAll(testMap))
    }

    @Test
    fun get() {
        // Set up initial values
        changeAdapter.addAll(testMap)

        for (key: Int in testMap.keys) {
            assertEquals("Get", testMap[key], changeAdapter[key])
        }
    }

    @Test
    fun getNonExistent() {
        assertNull("Get nonexistent", changeAdapter[0])
    }

    @Test
    fun getAll() {
        // Set up initial values
        changeAdapter.addAll(testMap)

        assertEquals("Get all", testMap, changeAdapter.getAll())
    }
}