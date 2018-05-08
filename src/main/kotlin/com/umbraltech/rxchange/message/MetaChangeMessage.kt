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
 * The core class used to provide metadata values with change messages
 *
 * The metadata can be used to provide details on which elements were changed
 *
 * @param D the type of data
 * @param M the type of metadata
 * @property oldData the original data
 * @property newData the updated data
 * @property changeType the type of change that occurred
 * @property metadata the metadata
 * @constructor Creates a change message with the specified data, change type, and metadata
 */
class MetaChangeMessage<D, M>(override val oldData: D,
                              override val newData: D,
                              override val changeType: ChangeType,
                              val metadata: M)
    : ChangeMessage<D>(oldData, newData, changeType) {
    override fun toString(): String {
        return "${MetaChangeMessage::class.simpleName}(changeMessage=${ChangeMessage::class.simpleName}(oldData=$oldData, newData=$newData, changeType=$changeType), metadata=$metadata)"
    }
}