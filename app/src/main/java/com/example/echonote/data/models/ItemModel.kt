package com.example.echonote.data.models

import com.example.echonote.data.entities.Item
import com.example.echonote.data.persistence.IPersistence
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.json.JsonElement

class ItemModel private constructor(
    private val persistence: IPersistence,
    private val dateTimeCreator: () -> LocalDateTime,
    val items: MutableList<Item> // Must pass in the folders directly to constructor to use invoke operator below
) {

    companion object {
        suspend operator fun invoke(persistence: IPersistence, dateTimeCreator: () -> LocalDateTime, folderId: Int)
        = ItemModel(persistence, dateTimeCreator, persistence.loadItems(folderId).toMutableList())
    }

    suspend fun add(folderId: Long, title: String, summary: JsonElement): Long {
        val count = persistence.getItemsCount() + 1
        val currentTime = dateTimeCreator()
        val item = Item(count, folderId, title, summary, currentTime, currentTime)
        persistence.saveItem(item)
        items.add(item)
        return count
    }

    suspend fun changeFolder(id: Long, folderId: Long) {
        val element = items.find { it.id == id }
        if(element == null) throw IllegalArgumentException("Id doesn't exist for this item")
        element.folder_id = folderId
        element.updated_on = dateTimeCreator()
        persistence.saveItem(element)
    }

    suspend fun changeTitle(id: Long, title: String) {
        val item = items.find { it.id == id }
        if (item == null) throw IllegalArgumentException("Id $id doesn't exist")
        // Inside each folder, the title must be unique
        val folderItems = items.filter { it.folder_id == item.folder_id && it.id != id && it.title == title}
        if (folderItems.isNotEmpty()) throw IllegalArgumentException("Title is not unique in the folder")
        item.title = title
        item.updated_on = dateTimeCreator()
        persistence.saveItem(item)
    }

    suspend fun changeSummary(id: Long, summary: JsonElement) {
        val item = items.find { it.id == id }
        if(item == null) throw IllegalArgumentException("Id doesn't exist for this item")
        item.summary = summary
        item.updated_on = dateTimeCreator()
        persistence.saveItem(item)
    }

    suspend fun del(id: Long) {
        items.removeIf{it.id == id}
        persistence.saveItems(items)
    }

    suspend fun save() {
        persistence.saveItems(items)
    }

}