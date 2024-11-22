package com.example.echonote.data.models

import com.example.echonote.data.entities.Item
import com.example.echonote.data.persistence.IPersistence
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.json.JsonElement

// Each ItemModel is only responsible for managing its own folder
class ItemModel(
    private val persistence: IPersistence,
    private val dateTimeCreator: () -> LocalDateTime,
    val folderId: Long
): IPublisher() {
    val items: MutableList<Item> = emptyList<Item>().toMutableList()
    var isInitialized = false

    /**
     * Returns true if this call initialized the model
     */
    suspend fun init(): Boolean {
        if (isInitialized) {
            return false
        }
        items.addAll(persistence.loadItems(folderId).toMutableList())
        isInitialized = true
        notifySubscribers()
        return true
    }

    suspend fun add(title: String, summary: JsonElement) {
        val possibleItems = items.filter { it.title == title }
        if(possibleItems.isNotEmpty()) throw IllegalArgumentException("Title must be unique")
        val currentTime = dateTimeCreator()
        val item = persistence.createItem(folderId, title, summary, currentTime, currentTime)
        items.add(item)
        notifySubscribers()
    }

    suspend fun changeFolder(id: Long, folderId: Long) {
//        Attempting to change the folder to the current so we don't do anything
        if(folderId == this.folderId) return

        val element = items.find { it.id == id }
        if(element == null) throw IllegalArgumentException("Id doesn't exist for this item")
        element.folder_id = folderId
        element.updated_on = dateTimeCreator()
        persistence.saveItem(element)
        items.removeIf{it.id == id}
        notifySubscribers()
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
        notifySubscribers()
    }

    suspend fun del(id: Long) {
        persistence.deleteItem(id)
        items.removeIf{it.id == id}
        notifySubscribers()
    }

    suspend fun save() {
        persistence.saveItems(items)
    }

}