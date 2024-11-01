package com.example.echonote.data.entities

import kotlinx.datetime.*
import kotlinx.serialization.Serializable

@Serializable
data class Item(
    val id: Int,
    val folder_id: Int,
    var title: String,
    var summary: String,
    var created_on: LocalDateTime,
    var updated_on: LocalDateTime,
)
