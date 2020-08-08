package com.bnorm.junit5.contingent.templates

import com.bnorm.junit5.contingent.Contingent
import com.bnorm.junit5.contingent.ContingentExtension
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

@ContingentExtension
class ContingentBasicTemplate {
    @Test
    @Disabled("disabled")
    @Tag("first")
    fun first() {
    }

    @Test
    @Tag("third")
    @Contingent("second")
    fun third() {
    }

    @Test
    @Tag("second")
    @Contingent("first")
    fun second() {
        fail("failed")
    }

    @Test
    @Tag("fourth")
    @Contingent("second", "third")
    fun fourth() {
    }
}
