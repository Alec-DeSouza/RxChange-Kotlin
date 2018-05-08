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

package com.umbraltech.rxchange.observer

import com.umbraltech.rxchange.message.ChangeMessage
import io.reactivex.observers.DefaultObserver

/**
 * A utility class used by observers for overriding specific events
 *
 * @param D the type of data
 * @constructor Default constructor
 */
open class ChangeMessageObserver<D> : DefaultObserver<ChangeMessage<D>>() {

    override fun onNext(changeMessage: ChangeMessage<D>) {
        // Stub
    }

    override fun onComplete() {
        // Stub
    }

    override fun onError(throwable: Throwable) {
        // Stub
    }
}