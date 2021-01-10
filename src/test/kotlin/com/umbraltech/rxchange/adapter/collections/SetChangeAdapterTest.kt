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

class SetChangeAdapterTest {
    private lateinit var changeAdapter: SetChangeAdapter<Int>

    private val testSet: Set<Int> = linkedSetOf(0, 1, 2)

    @Before
    fun setUp() {
        changeAdapter = SetChangeAdapter()
    }

    @Test
    fun add() {
        val oldPayloadList: MutableList<Set<Int>> = mutableListOf(
                setOf(),
                setOf(0),
                setOf(0, 1)
        )

        val newPayloadList: MutableList<Set<Int>> = mutableListOf(
                setOf(0),
                setOf(0, 1),
                setOf(0, 1, 2)
        )

        val changeSnapshotList: MutableList<Set<Int>> = mutableListOf(
            setOf(0),
            setOf(1),
            setOf(2)
        )

        changeAdapter.getObservable()
                .filter(ChangeTypeFilter(ChangeType.ADD))
                .subscribe(ChangePayloadTestObserver(oldPayloadList, newPayloadList, changeSnapshotList))

        // Perform sequence of updates
        for (i: Int in testSet) {
            assertTrue("Add", changeAdapter.add(i))
        }

        // Verify all payloads were tested
        assertEquals("Remaining old payload", 0, oldPayloadList.size)
        assertEquals("Remaining new payload", 0, newPayloadList.size)
    }

    @Test
    fun addExisting() {
        // Set up initial values
        changeAdapter.addAll(testSet)

        changeAdapter.getObservable()
                .filter(ChangeTypeFilter(ChangeType.ADD))
                .subscribe(InvocationFailObserver("Add invoked for existing value"))

        for (i: Int in testSet) {
            assertFalse("Add existing", changeAdapter.add(i))
        }
    }

    @Test
    fun addAll() {
        val oldPayloadList: MutableList<Set<Int>> = mutableListOf(
                setOf()
        )

        val newPayloadList: MutableList<Set<Int>> = mutableListOf(
                setOf(0, 1, 2)
        )

        val changeSnapshotList: MutableList<Set<Int>> = mutableListOf(
            setOf(0, 1, 2)
        )

        changeAdapter.getObservable()
                .filter(ChangeTypeFilter(ChangeType.ADD))
                .subscribe(ChangePayloadTestObserver(oldPayloadList, newPayloadList, changeSnapshotList))

        // Perform batch update
        assertTrue("Add all", changeAdapter.addAll(testSet))

        // Verify all payloads were tested
        assertEquals("Remaining old payload", 0, oldPayloadList.size)
        assertEquals("Remaining new payload", 0, newPayloadList.size)
    }

    @Test
    fun addAllExisting() {
        // Set up initial values
        changeAdapter.addAll(testSet)

        changeAdapter.getObservable()
                .filter(ChangeTypeFilter(ChangeType.ADD))
                .subscribe(InvocationFailObserver("Add invoked for existing values"))

        assertFalse("Add all existing", changeAdapter.addAll(testSet))
    }

    @Test
    fun remove() {
        val oldPayloadList: MutableList<Set<Int>> = mutableListOf(
                setOf(0, 1, 2),
                setOf(1, 2),
                setOf(2)
        )

        val newPayloadList: MutableList<Set<Int>> = mutableListOf(
                setOf(1, 2),
                setOf(2),
                setOf()
        )

        val changeSnapshotList: MutableList<Set<Int>> = mutableListOf(
            setOf(0),
            setOf(1),
            setOf(2)
        )

        // Set up initial values
        changeAdapter.addAll(testSet)

        changeAdapter.getObservable()
                .filter(ChangeTypeFilter(ChangeType.REMOVE))
                .subscribe(ChangePayloadTestObserver(oldPayloadList, newPayloadList, changeSnapshotList))

        // Perform sequence of updates
        for (i: Int in testSet) {
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
                .subscribe(InvocationFailObserver("Remove invoked for nonexistent value"))

        for (i: Int in testSet) {
            assertFalse("Remove nonexistent", changeAdapter.remove(i))
        }
    }

    @Test
    fun removeAll() {
        val oldPayloadList: MutableList<Set<Int>> = mutableListOf(
                setOf(0, 1, 2)
        )

        val newPayloadList: MutableList<Set<Int>> = mutableListOf(
                setOf()
        )

        val changeSnapshotList: MutableList<Set<Int>> = mutableListOf(
            setOf(0, 1, 2)
        )

        // Set up initial values
        changeAdapter.addAll(testSet)

        changeAdapter.getObservable()
                .filter(ChangeTypeFilter(ChangeType.REMOVE))
                .subscribe(ChangePayloadTestObserver(oldPayloadList, newPayloadList, changeSnapshotList))

        // Perform batch update
        assertTrue("Remove all", changeAdapter.removeAll(testSet))

        // Verify all payloads were tested
        assertEquals("Remaining old payload", 0, oldPayloadList.size)
        assertEquals("Remaining new payload", 0, newPayloadList.size)
    }

    @Test
    fun removeAllNonExistent() {
        changeAdapter.getObservable()
                .filter(ChangeTypeFilter(ChangeType.REMOVE))
                .subscribe(InvocationFailObserver("Remove invoked for nonexistent values"))

        assertFalse("Remove all nonexistent", changeAdapter.removeAll(testSet))
    }

    @Test
    fun getAll() {
        // Set up initial values
        changeAdapter.addAll(testSet)

        assertEquals("Get all", testSet, changeAdapter.getAll())
    }
}