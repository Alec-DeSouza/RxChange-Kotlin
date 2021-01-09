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

/** Alias used for the type ChangeMessage<Map<K, D>> */
typealias ChangeMessageMap<K, D> = ChangeMessage<Map<K, D>>

/**
 * An adapter that implements the reactive change model for maps
 *
 * @param K the type used for the keys
 * @param D the type used for the data
 * @constructor Default constructor
 */
class MapChangeAdapter<K, D>() {
    private val publishSubject: PublishSubject<ChangeMessageMap<K, D>> = PublishSubject.create()
    private val dataMap: MutableMap<K, D> = mutableMapOf()
    private val readWriteLock: ReadWriteLock = ReentrantReadWriteLock()

    /** Initializes the adapter with [initialDataMap] without emitting a change message */
    constructor(initialDataMap: Map<K, D>) : this() {
        dataMap.putAll(initialDataMap)
    }

    /**
     * Adds the key-value pair entry ([key], [data]) to the map and emits a change message to surrounding observers
     *
     * The metadata in the emitted change message will contain a snapshot of the entry that was just added
     *
     * @return true if the entry was added to the map, false otherwise
     */
    fun add(key: K, data: D): Boolean = readWriteLock.writeLock().withLock {

        // Check if entry already exists
        if (key in dataMap) {
            return false
        }

        val oldMapSnapshot: Map<K, D> = dataMap.toMap()
        dataMap[key] = data

        val newMapSnapshot: Map<K, D> = dataMap.toMap()
        val changeSnapshot: Map.Entry<K, D> = mapOf(key to data).iterator().next()

        // Signal addition
        publishSubject.onNext(MetaChangeMessage(oldMapSnapshot, newMapSnapshot, ChangeType.ADD, changeSnapshot))

        return true
    }

    /**
     * Adds the key-value pair entries from [dataMap] to the map and emits a change message to surrounding observers
     *
     * The metadata in the emitted change message will contain a snapshot of the entries that were just added
     *
     * @return true if all of the entries were added, false otherwise
     */
    fun addAll(dataMap: Map<K, D>): Boolean = readWriteLock.writeLock().withLock {

        // Check if entries already exist
        for (key: K in dataMap.keys) {
            if (key in this.dataMap) {
                return false
            }
        }

        val oldMapSnapshot: Map<K, D> = this.dataMap.toMap()
        this.dataMap.putAll(dataMap)

        val newMapSnapshot: Map<K, D> = this.dataMap.toMap()
        val changeSnapshot: Map<K, D> = dataMap.toMap()

        // Signal addition
        publishSubject.onNext(MetaChangeMessage(oldMapSnapshot, newMapSnapshot, ChangeType.ADD, changeSnapshot))

        return true
    }

    /**
     * Removes the entry specified by [key] and emits a change message to surrounding observers
     *
     * The metadata in the emitted change message will contain a snapshot of the entry that was just removed
     *
     * @return true if the entry was removed, false otherwise
     */
    fun remove(key: K): Boolean = readWriteLock.writeLock().withLock {

        // Check if no entry to remove
        if (key !in dataMap) {
            return false
        }

        val oldMapSnapshot: Map<K, D> = dataMap.toMap()
        val resultData: D = dataMap.remove(key)!!

        val newMapSnapshot: Map<K, D> = dataMap.toMap()
        val changeSnapshot: Map.Entry<K, D> = mapOf(key to resultData).iterator().next()

        // Signal removal
        publishSubject.onNext(MetaChangeMessage(oldMapSnapshot, newMapSnapshot, ChangeType.REMOVE, changeSnapshot))

        return true
    }

    /**
     * Removes the entries specified by [keySet] from the map and emits a change message to surrounding observers
     *
     * The metadata in the emitted change message will contain a snapshot of the entries that were just removed
     *
     * @return true if all of the entries were removed, false otherwise
     */
    fun removeAll(keySet: Set<K>): Boolean = readWriteLock.writeLock().withLock {

        // Check if no entries to remove
        for (key: K in keySet) {
            if (key !in this.dataMap) {
                return false
            }
        }

        val oldMapSnapshot: Map<K, D> = this.dataMap.toMap()
        this.dataMap.keys.removeAll(keySet)

        val newMapSnapshot: Map<K, D> = this.dataMap.toMap()
        val changeSnapshot: Map<K, D> = oldMapSnapshot - keySet

        // Signal removal
        publishSubject.onNext(MetaChangeMessage(oldMapSnapshot, newMapSnapshot, ChangeType.REMOVE, changeSnapshot))

        return true
    }

    /**
     * Updates the entry specified by [key] with [data] in the map and emits a change message to surrounding observers
     *
     * The metadata in the emitted change message will contain a snapshot of the entry that was just updated
     *
     * @return true if the entry was updated, false otherwise
     */
    fun update(key: K, data: D): Boolean = readWriteLock.writeLock().withLock {

        // Check if entry does not exist
        if (key !in dataMap) {
            return false
        }

        val oldMapSnapshot: Map<K, D> = this.dataMap.toMap()
        dataMap[key] = data

        val newMapSnapshot: Map<K, D> = this.dataMap.toMap()
        val changeSnapshot: Map.Entry<K, D> = mapOf(key to data).iterator().next()

        // Signal update
        publishSubject.onNext(MetaChangeMessage(oldMapSnapshot, newMapSnapshot, ChangeType.UPDATE, changeSnapshot))

        return true
    }

    /**
     * Updates the entries specified by [dataMap] in the map and emits a change message to surrounding observers
     *
     * The metadata in the emitted change message will contain a snapshot of the entries that were just updated
     *
     * @return true if all of the entries were updated, false otherwise
     */
    fun updateAll(dataMap: Map<K, D>): Boolean = readWriteLock.writeLock().withLock {

        // Check if entries do not exist
        for (key: K in dataMap.keys) {
            if (key !in this.dataMap) {
                return false
            }
        }

        val oldMapSnapshot: Map<K, D> = this.dataMap.toMap()
        this.dataMap.putAll(dataMap)

        val newMapSnapshot: Map<K, D> = this.dataMap.toMap()
        val changeSnapshot: Map<K, D> = dataMap.toMap()

        // Signal update
        publishSubject.onNext(MetaChangeMessage(oldMapSnapshot, newMapSnapshot, ChangeType.UPDATE, changeSnapshot))

        return true
    }

    /**
     * Retrieves the data of entry specified by [key] from the map
     *
     * @return the data associated with the key if found within the map, null otherwise
     */
    operator fun get(key: K): D? = readWriteLock.readLock().withLock {
        return dataMap[key]
    }

    /**
     * Retrieves a snapshot of the current map
     *
     * @return the map containing the entries
     */
    fun getAll(): Map<K, D> = readWriteLock.readLock().withLock {
        return dataMap.toMap()
    }

    /**
     * Retrieves a reference to the observable used for listening to change messages
     *
     * @return the observable reference
     */
    fun getObservable(): Observable<ChangeMessageMap<K, D>> = publishSubject
}