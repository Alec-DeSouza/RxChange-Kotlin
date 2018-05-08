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

package com.umbraltech.rxchange.message

import com.umbraltech.rxchange.type.ChangeType

/**
 * The core class used to construct the messages emitted by change adapters
 *
 * @param D the type of data
 * @property oldData the original data
 * @property newData the updated data
 * @property changeType the type of change that occurred
 * @constructor Creates a change message with the specified data and change type
 */
open class ChangeMessage<D>(open val oldData: D,
                            open val newData: D,
                            open val changeType: ChangeType) {
    override fun toString(): String {
        return "${this::class.simpleName}(oldData=$oldData, newData=$newData, changeType=$changeType)"
    }
}