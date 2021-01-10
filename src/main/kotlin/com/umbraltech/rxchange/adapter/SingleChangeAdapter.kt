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

import com.umbraltech.rxchange.message.ChangeMessage
import com.umbraltech.rxchange.type.ChangeType
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.locks.ReadWriteLock
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.withLock

/**
 * An adapter that implements the reactive change model for a single element
 *
 * @param D the type of data
 * @constructor Initializes the adapter with a value, without emitting a change message
 */
class SingleChangeAdapter<D>(@Volatile private var data: D) {
    private val publishSubject: PublishSubject<ChangeMessage<D>> = PublishSubject.create()
    private val readWriteLock: ReadWriteLock = ReentrantReadWriteLock()

    /**
     * Updates the adapter with [data] and emits a change message to surrounding observers
     *
     * The change snapshot will contain the value that was just updated
     *
     * @return true if the element was added, false otherwise
     */
    fun update(data: D): Boolean = readWriteLock.writeLock().withLock {
        val oldData: D = this.data
        this.data = data

        // Signal update
        publishSubject.onNext(ChangeMessage(oldData, this.data, ChangeType.UPDATE, data))

        return true
    }

    /**
     * Retrieves the underlying data
     *
     * @return the current data
     */
    fun get(): D = readWriteLock.readLock().withLock {
        return data
    }

    /**
     * Retrieves a reference to the observable used for listening to change messages
     *
     * @return the observable reference
     */
    fun getObservable(): Observable<ChangeMessage<D>> = publishSubject
}