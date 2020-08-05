package com.bnorm.junit5.contingent

annotation class Contingent(
    vararg val values: String,

    /** If the test should be skipped instead of failed. */
    val skip: Boolean = false
)
