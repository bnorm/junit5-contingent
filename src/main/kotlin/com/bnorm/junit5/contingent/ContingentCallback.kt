package com.bnorm.junit5.contingent

import org.junit.jupiter.api.Assumptions
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.fail
import org.opentest4j.IncompleteExecutionException
import java.lang.reflect.Method

class ContingentCallback : BeforeEachCallback, AfterEachCallback {
    companion object {
        private val NAMESPACE = ExtensionContext.Namespace.create(ContingentCallback::class.java)

        private val ExtensionContext.store: ExtensionContext.Store
            get() = root.getStore(NAMESPACE)

        private val ExtensionContext.testContext: GatedTestContext
            get() = store.getOrComputeIfAbsent(requiredTestClass, { GatedTestContext() }, GatedTestContext::class.java)
    }

    override fun beforeEach(context: ExtensionContext) {
        context.testContext.beforeTestMethod(context.requiredTestMethod)
    }

    override fun afterEach(context: ExtensionContext) {
        context.testContext.afterTestMethod(context.requiredTestMethod, context.executionException.orElse(null))
    }

    private class GatedTestContext {
        private val failed = mutableSetOf<String>()

        fun beforeTestMethod(testMethod: Method) {
            val annotation: Contingent? = testMethod.getAnnotation(Contingent::class.java)
            if (annotation != null) {
                val failed = annotation.values.intersect(failed)
                if (failed.isNotEmpty()) {
                    Assumptions.assumeFalse(annotation.skip, "Test blocked by failed gate(s): $failed")
                    fail("Test blocked by failed gate(s): $failed")
                }
            }
        }

        fun afterTestMethod(testMethod: Method, testException: Throwable?) {
            if (testException != null && testException !is IncompleteExecutionException) {
                val annotations = testMethod.getAnnotationsByType(Tag::class.java)
                failed.addAll(annotations.map { it.value })
            }
        }
    }
}
