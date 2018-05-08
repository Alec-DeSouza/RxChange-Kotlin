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
import com.umbraltech.rxchange.type.ChangeType
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MetadataFilterTest {
    private val intMetadataFilter: MetadataFilter = MetadataFilter(Int::class)
    private val listMetadataFilter: MetadataFilter = MetadataFilter(List::class)

    @Test
    fun test() {
        val changeMessage: MetaChangeMessage<Int, Int> = MetaChangeMessage(0, 1, ChangeType.UPDATE, 1)

        assertTrue("Metadata type int", intMetadataFilter.test(changeMessage))
        assertFalse("Metadata type list", listMetadataFilter.test(changeMessage))
    }

    @Test
    fun testNoMetadata() {
        val changeMessage: MetaChangeMessage<Int, Int?> = MetaChangeMessage(0, 1, ChangeType.UPDATE, null)

        assertFalse("Metadata type int", intMetadataFilter.test(changeMessage))
        assertFalse("Metadata type list", listMetadataFilter.test(changeMessage))
    }

    @Test
    fun testNotMetadataInstance() {
        val changeMessage: ChangeMessage<Int> = ChangeMessage(0, 1, ChangeType.UPDATE)

        assertFalse("Metadata type int", intMetadataFilter.test(changeMessage))
        assertFalse("Metadata type list", listMetadataFilter.test(changeMessage))
    }
}