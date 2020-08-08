package com.bnorm.junit5.contingent

/**
 * Marks the current [Test][org.junit.jupiter.api.Test] as being contingent on
 * the success of other tests. Tests are matched using values specified by this
 * annotation against other tests with equal [Tag][org.junit.jupiter.api.Tag]
 * values. If any test with any matching Tag fails, this test will be failed.
 */
@Target(AnnotationTarget.FUNCTION)
annotation class Contingent(
    vararg val values: String
)
