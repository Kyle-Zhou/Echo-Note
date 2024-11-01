package com.example.echonote

import kotlinx.datetime.LocalDateTime

fun dateTimeCreator(): LocalDateTime {
    val isoString = "2024-02-15T14:30:00"
    return LocalDateTime.parse(isoString)
}

fun dateTimeCreator2(): LocalDateTime {
    val isoString = "2024-03-16T14:30:00"
    return LocalDateTime.parse(isoString)
}