package com.networthtracker.presentation

import java.math.BigDecimal
import java.math.RoundingMode

fun String.trimToNearestThousandth(): String =
    BigDecimal(this.toDouble()).setScale(3, RoundingMode.HALF_UP).stripTrailingZeros()
        .toPlainString()
