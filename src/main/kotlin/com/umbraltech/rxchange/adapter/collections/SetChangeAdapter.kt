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
import com.umbraltech.rxchange.withLock
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.locks.ReadWriteLock
import java.util.concurrent.locks.ReentrantReadWriteLock

/** Alias used for the type ChangeMessage<Set<D>> */
typealias ChangeMessageSet<D> = ChangeMessage<Set<D>>

/**
 * An adapter that implements the reactive change model for sets
 *
 * @param D the type of data held by the set
 * @constructor Default constructor
 */
class SetChangeAdapter<D>() {
    private val publishSubject: PublishSubject<ChangeMessageSet<D>> = PublishSubject.create()
    private val dataSet: MutableSet<D> = mutableSetOf()
    private val readWriteLock: ReadWriteLock = ReentrantReadWriteLock()

    /** Initializes the adapter with [initialDataSet] without emitting a change message */
    constructor(initialDataSet: Set<D>) : this() {
        dataSet.addAll(initialDataSet)
    }

    /**
     * Adds [data] to the set and emits a change message to surrounding observers
     *
     * The metadata in the emitted change message will contain the [data] that was added
     *
     * @return true if the element was added, false otherwise
     */
    fun add(data: D): Boolean = withLock(readWriteLock.writeLock()) {

        // Check if entry already exists
        if (data in dataSet) {
            return false
        }

        val oldSetSnapshot: Set<D> = dataSet.toSet()
        dataSet.add(data)

        val newSetSnapshot: Set<D> = dataSet.toSet()

        // Signal addition
        publishSubject.onNext(MetaChangeMessage(oldSetSnapshot, newSetSnapshot, ChangeType.ADD, data))

        return true
    }

    /**
     * Adds [dataSet] to the set and emits a change message to surrounding observers
     *
     * The metadata in the emitted change message will contain a snapshot of the [dataSet] that was just added
     *
     * @return true if all of the elements of [dataSet] were added, false otherwise
     */
    fun addAll(dataSet: Set<D>): Boolean = withLock(readWriteLock.writeLock()) {

        // Check if entries already exist
        if (this.dataSet.containsAll(dataSet)) {
            return false
        }

        val oldSetSnapshot: Set<D> = this.dataSet.toSet()
        this.dataSet.addAll(dataSet)

        val newSetSnapshot: Set<D> = this.dataSet.toSet()
        val changeSnapshot: Set<D> = dataSet.toSet()

        // Signal addition
        publishSubject.onNext(MetaChangeMessage(oldSetSnapshot, newSetSnapshot, ChangeType.ADD, changeSnapshot))

        return true
    }

    /**
     * Removes [data] from the set and emits a change message to surrounding observers
     *
     * The metadata in the emitted change message will contain the [data] that was just removed
     *
     * @return true if the element was removed, false otherwise
     */
    fun remove(data: D): Boolean = withLock(readWriteLock.writeLock()) {

        // Check if no entry to remove
        if (data !in dataSet) {
            return false
        }

        val oldSetSnapshot: Set<D> = dataSet.toSet()
        dataSet.remove(data)

        val newSetSnapshot: Set<D> = dataSet.toSet()

        // Signal removal
        publishSubject.onNext(MetaChangeMessage(oldSetSnapshot, newSetSnapshot, ChangeType.REMOVE, data))

        return true
    }

    /**
     * Removes [dataSet] from the set and emits a change message to surrounding observers
     *
     * The metadata in the emitted change message will contain a snapshot of the [dataSet] that was just removed
     *
     * @return true if all of the elements of [dataSet] were removed, false otherwise
     */
    fun removeAll(dataSet: Set<D>): Boolean = withLock(readWriteLock.writeLock()) {

        // Check if entries do not exist
        if (!this.dataSet.containsAll(dataSet)) {
            return false
        }

        val oldSetSnapshot: Set<D> = this.dataSet.toSet()
        this.dataSet.removeAll(dataSet)

        val newSetSnapshot: Set<D> = this.dataSet.toSet()
        val changeSnapshot: Set<D> = dataSet.toSet()

        // Signal removal
        publishSubject.onNext(MetaChangeMessage(oldSetSnapshot, newSetSnapshot, ChangeType.REMOVE, changeSnapshot))

        return true
    }

    /**
     * Retrieves a snapshot of the current set
     *
     * @return the set of elements
     */
    fun getAll(): Set<D> = withLock(readWriteLock.readLock()) {
        return dataSet.toSet()
    }

    /**
     * Retrieves a reference to the observable used for listening to change messages
     *
     * @return the observable reference
     */
    fun getObservable(): Observable<ChangeMessageSet<D>> = publishSubject
}