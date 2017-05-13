package com.soprasteria.initiatives.ideas.mapping

import org.assertj.core.api.Assertions.assertThat
import org.bson.types.ObjectId
import org.junit.Test

class StringMappingTests {

    @Test
    fun `should map existing string to objectId`() {
        val initId = ObjectId()
        assertThat(initId.toString().toId()).isNotNull().isEqualTo(initId)
    }

    @Test
    fun `should map null string to new objectId`() {
        val str: String? = null
        assertThat(str.toId()).isNotNull()
    }
}