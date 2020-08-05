package com.bnorm.junit5.contingent

import org.junit.jupiter.api.TestMethodOrder
import org.junit.jupiter.api.extension.ExtendWith

@TestMethodOrder(ContingentSortOrder::class)
@ExtendWith(ContingentCallback::class)
annotation class ContingentExtension
