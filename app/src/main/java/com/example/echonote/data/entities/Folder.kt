package com.example.echonote.data.entities

import kotlinx.datetime.*
import kotlinx.serialization.Serializable

@Serializable
data class Folder(
    val id: Int,
    val user_id: Int,
    var title: String,
    var description: String?,
    var created_on: LocalDateTime,
    var updated_on: LocalDateTime,
)
