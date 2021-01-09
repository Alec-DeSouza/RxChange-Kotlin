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

import com.umbraltech.rxchange.message.ChangeMessage
import com.umbraltech.rxchange.message.MetaChangeMessage
import com.umbraltech.rxchange.type.ChangeType
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.locks.ReadWriteLock
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.withLock

/** Alias used for the type ChangeMessage<List<D>> */
typealias ChangeMessageList<D> = ChangeMessage<List<D>>

/**
 * An adapter that implements the reactive change model for lists
 *
 * @param D the type of data held by the list
 * @constructor Default constructor
 */
class ListChangeAdapter<D>() {
    private val publishSubject: PublishSubject<ChangeMessageList<D>> = PublishSubject.create()
    private val dataList: MutableList<D> = mutableListOf()
    private val readWriteLock: ReadWriteLock = ReentrantReadWriteLock()

    /** Initializes the adapter with [initialDataList] without emitting a change message */
    constructor(initialDataList: List<D>) : this() {
        dataList.addAll(initialDataList)
    }

    /**
     * Adds [data] to the list and emits a change message to surrounding observers
     *
     * The metadata in the emitted change message will contain the [data] that was just added
     *
     * @return true always
     */
    fun add(data: D): Boolean = readWriteLock.writeLock().withLock {
        val oldListSnapshot: List<D> = dataList.toList()
        dataList.add(data)

        val newListSnapshot: List<D> = dataList.toList()

        // Signal addition
        publishSubject.onNext(MetaChangeMessage(oldListSnapshot, newListSnapshot, ChangeType.ADD, data))

        return true
    }

    /**
     * Adds [data] to the list at the specified [index] and emits a change message to surrounding observers
     *
     * The metadata in the emitted change message will contain the [data] that was just added
     *
     * @return true if the element was added to the list, false otherwise
     */
    fun addAt(index: Int, data: D): Boolean = readWriteLock.writeLock().withLock {

        // Validate index (and allow adding at ends)
        if (index !in 0..dataList.size) {
            return false
        }

        val oldListSnapShot: List<D> = dataList.toList()
        dataList.add(index, data)

        val newListSnapshot: List<D> = dataList.toList()

        // Signal addition
        publishSubject.onNext(MetaChangeMessage(oldListSnapShot, newListSnapshot, ChangeType.ADD, data))

        return true
    }

    /**
     * Adds [dataList] to the list and emits a change message to surrounding observers
     *
     * The metadata in the emitted change message will contain a snapshot of the [dataList] that was just added
     *
     * @return true always
     */
    fun addAll(dataList: List<D>): Boolean = readWriteLock.writeLock().withLock {
        val oldListSnapshot: List<D> = this.dataList.toList()
        this.dataList.addAll(dataList)

        val newListSnapshot: List<D> = this.dataList.toList()
        val changeSnapshot: List<D> = dataList.toList()

        // Signal addition
        publishSubject.onNext(MetaChangeMessage(oldListSnapshot, newListSnapshot, ChangeType.ADD, changeSnapshot))

        return true
    }

    /**
     * Removes [data] from the list and emits a change message to surrounding observers
     *
     * The metadata in the emitted change message will contain the [data] that was just removed
     *
     * @return true if the element was removed, false otherwise
     */
    fun remove(data: D): Boolean = readWriteLock.writeLock().withLock {

        // Validate item
        if (data !in dataList) {
            return false
        }

        val oldListSnapshot: List<D> = dataList.toList()
        dataList.remove(data)

        val newListSnapshot: List<D> = dataList.toList()

        // Signal removal
        publishSubject.onNext(MetaChangeMessage(oldListSnapshot, newListSnapshot, ChangeType.REMOVE, data))

        return true
    }

    /**
     * Removes data at the specified [index] and emits a change message to surrounding observers
     *
     * The metadata in the emitted change message will contain the data that was just removed
     *
     * @return true if the element was removed, false otherwise
     */
    fun removeAt(index: Int): Boolean = readWriteLock.writeLock().withLock {

        // Validate index
        if (index !in 0 until dataList.size) {
            return false
        }

        val oldListSnapshot: List<D> = dataList.toList()
        val data: D = dataList.removeAt(index)

        val newListSnapshot: List<D> = dataList.toList()

        // Signal removal
        publishSubject.onNext(MetaChangeMessage(oldListSnapshot, newListSnapshot, ChangeType.REMOVE, data))

        return true
    }

    /**
     * Removes [dataList] from the list and emits a change message to surrounding observers
     *
     * The metadata in the emitted change message will contain a snapshot of the [dataList] that was just removed
     *
     * @return true if all of the elements of [dataList] were removed, false otherwise
     */
    fun removeAll(dataList: List<D>): Boolean = readWriteLock.writeLock().withLock {

        // Validate items
        if (!this.dataList.containsAll(dataList)) {
            return false
        }

        val oldListSnapshot: List<D> = this.dataList.toList()
        this.dataList.removeAll(dataList)

        val newListSnapshot: List<D> = this.dataList.toList()
        val changeSnapshot: List<D> = dataList.toList()

        // Signal removal
        publishSubject.onNext(MetaChangeMessage(oldListSnapshot, newListSnapshot, ChangeType.REMOVE, changeSnapshot))

        return true
    }

    /**
     * Updates the element at the specified [index] with [data]
     *
     * The metadata in the emitted change message will contain the [data] that was just updated
     *
     * @return true if the element was updated, false otherwise
     */
    fun update(index: Int, data: D): Boolean = readWriteLock.writeLock().withLock {

        // Validate index
        if (index !in 0 until dataList.size) {
            return false
        }

        val oldListSnapshot: List<D> = dataList.toList()
        dataList[index] = data

        val newListSnapshot: List<D> = dataList.toList()

        // Signal update
        publishSubject.onNext(MetaChangeMessage(oldListSnapshot, newListSnapshot, ChangeType.UPDATE, data))

        return true
    }

    /**
     * Retrieves the element at the specified [index] of the list
     *
     * @return the element if a valid [index] is provided, null otherwise
     */
    operator fun get(index: Int): D = readWriteLock.readLock().withLock {
        return dataList[index]
    }

    /**
     * Retrieves a snapshot of the current list
     *
     * @return the list of elements
     */
    fun getAll(): List<D> = readWriteLock.readLock().withLock {
        return dataList.toList()
    }

    /**
     * Retrieves a reference to the observable used for listening to change messages
     *
     * @return the observable reference
     */
    fun getObservable(): Observable<ChangeMessageList<D>> = publishSubject
}