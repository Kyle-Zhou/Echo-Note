package com.example.echonote.data.entities

import kotlinx.datetime.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class Item(
    val id: Long,
    var folder_id: Long,
    var title: String,
    var summary: JsonElement,
    var created_on: LocalDateTime,
    var updated_on: LocalDateTime,
)
