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

package com.umbraltech.rxchange.filter

import com.umbraltech.rxchange.message.ChangeMessage
import com.umbraltech.rxchange.message.MetaChangeMessage
import io.reactivex.functions.Predicate
import kotlin.reflect.KClass

/**
 * A utility class used for filtering by metadata type in observers
 *
 * Only messages whose metadata is an instance of the specified type will be passed to observers
 *
 * @constructor Creates a filter with the class specified by [metadataClass]
 */
class MetadataFilter(private val metadataClass: KClass<*>) : Predicate<ChangeMessage<*>> {
    override fun test(changeMessage: ChangeMessage<*>): Boolean {

        // Verify message supports metadata
        if (changeMessage !is MetaChangeMessage<*, *>) {
            return false
        }

        // Verify metadata is provided and a subclass of the specified type
        return changeMessage.metadata?.let { return metadataClass.isInstance(it) } ?: return false
    }
}