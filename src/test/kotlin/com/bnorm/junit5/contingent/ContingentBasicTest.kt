package com.bnorm.junit5.contingent

import com.bnorm.junit5.contingent.templates.ContingentBasicTemplate
import org.junit.jupiter.api.Test
import org.junit.platform.engine.discovery.DiscoverySelectors.selectClass
import org.junit.platform.testkit.engine.EngineTestKit
import org.junit.platform.testkit.engine.EventConditions.*
import org.junit.platform.testkit.engine.Events
import org.junit.platform.testkit.engine.TestExecutionResultConditions.message


class ContingentBasicTest {
    @Test
    fun basic() {
        val testEvents: Events = EngineTestKit
            .engine("junit-jupiter")
            .selectors(selectClass(ContingentBasicTemplate::class.java))
            .execute()
            .testEvents()

        testEvents.assertEventsMatchExactly(
            event(test("first"), skippedWithReason("disabled")),
            event(test("second"), started()),
            event(test("second"), finishedWithFailure(message("failed"))),
            event(test("third"), started()),
            event(test("third"), finishedWithFailure(message("Test blocked by failed gate(s): [second]"))),
            event(test("fourth"), started()),
            event(test("fourth"), finishedWithFailure(message("Test blocked by failed gate(s): [second, third]")))
        )
    }
}
