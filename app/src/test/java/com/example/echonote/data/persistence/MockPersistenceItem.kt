package com.example.echonote.data.persistence

import com.example.echonote.data.entities.Item
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

class MockPersistenceItem(dateTimeCreator: () -> LocalDateTime): IPersistenceItem {
    private val localDateTime = dateTimeCreator()
    private var mockItems = mutableListOf<Item>(
        Item(1, 1, "1 1", stringToJson("test 1"), localDateTime, localDateTime),
        Item(2, 1, "2 1", stringToJson("test 2"), localDateTime, localDateTime),
        Item(3, 2, "3 2", stringToJson("test 3"), localDateTime, localDateTime),
        Item(4, 2, "4 2", stringToJson("test 4"), localDateTime, localDateTime),
    )

    private fun stringToJson(value: String): JsonElement {
        return Json.decodeFromString<JsonElement>("""{"value": "$value"}""")
    }

    override suspend fun createItem(
        folder_id: Long,
        title: String,
        summary: JsonElement,
        created_on: LocalDateTime,
        update_on: LocalDateTime
    ): Item {
        val newId = (mockItems.size + 1).toLong()
        val item = Item(newId, folder_id, title, summary, created_on, update_on)
        mockItems.add(item)
        return item
    }

    override suspend fun loadItems(folderId: Long): List<Item> {
        return mockItems.filter { it.folder_id == folderId }
    }

    override suspend fun saveItem(item: Item) {
        if(item.folder_id != 1.toLong() && item.folder_id != 2.toLong()) throw IllegalArgumentException("Folder id must be 1 or 2. Given ${item.folder_id}")
    }

    override suspend fun deleteItem(id: Long) {
        mockItems.removeIf {it.id == id}
    }
}