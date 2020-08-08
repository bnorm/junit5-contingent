package com.bnorm.junit5.contingent

import com.bnorm.junit5.contingent.internal.ContingentCallback
import com.bnorm.junit5.contingent.internal.ContingentSortOrder
import org.junit.jupiter.api.TestMethodOrder
import org.junit.jupiter.api.extension.ExtendWith

/**
 * Class-level extension annotation which must be applied for [Contingent]
 * annotations to be used.
 */
@TestMethodOrder(ContingentSortOrder::class)
@ExtendWith(ContingentCallback::class)
@Target(AnnotationTarget.CLASS)
annotation class ContingentExtension
