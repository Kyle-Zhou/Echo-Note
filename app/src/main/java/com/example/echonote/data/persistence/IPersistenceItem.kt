package com.example.echonote.data.persistence

import com.example.echonote.data.entities.Item
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.json.JsonElement

interface IPersistenceItem {
    suspend fun createItem(folder_id: Long, title: String, summary: JsonElement, created_on: LocalDateTime, update_on: LocalDateTime): Item
    suspend fun loadItems(folderId: Long): List<Item>
    suspend fun saveItem(item: Item)
    suspend fun deleteItem(id: Long)
}