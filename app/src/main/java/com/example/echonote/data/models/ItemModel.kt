package com.example.echonote.data.models

import com.example.echonote.data.entities.Item
import com.example.echonote.data.persistence.IPersistenceItem
import com.example.echonote.utils.EmptyArgumentEchoNoteException
import com.example.echonote.utils.IllegalArgumentEchoNoteException
import com.example.echonote.utils.NotFoundEchoNoteException
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.json.JsonElement

// Each ItemModel is only responsible for managing its own folder
class ItemModel(
    private val persistence: IPersistenceItem,
    private val dateTimeCreator: () -> LocalDateTime,
    val folderId: Long
): IPublisher() {
    val items: MutableList<Item> = emptyList<Item>().toMutableList()

    /**
     * Returns true if this call initialized the model
     */
    suspend fun init() {
        items.clear()
        items.addAll(persistence.loadItems(folderId).toMutableList())
        notifySubscribers()
    }

    suspend fun add(title: String, summary: JsonElement) {
        if (title.isEmpty()) throw EmptyArgumentEchoNoteException("Title must not be empty")

        val possibleItems = items.filter { it.title == title }
        if(possibleItems.isNotEmpty()) throw IllegalArgumentEchoNoteException("Title must be unique")

        val currentTime = dateTimeCreator()
        val item = persistence.createItem(folderId, title, summary, currentTime, currentTime)
        items.add(item)
        notifySubscribers()
    }

    fun get(id: Long): Item {
        val element = items.find { it.id == id }
        if(element == null) throw NotFoundEchoNoteException("Id $id doesn't exist for this item")
        return element
    }

    /**
     * Low level operation to add a item manually to the in-memory items list for the model
     *
     * @warning This method doesn't perform any checks and doesn't add the item to the persistence
     */
    fun moveIn(item: Item) {
        items.add(item)
        notifySubscribers()
    }

    suspend fun changeFolder(id: Long, folderId: Long) {
//        Attempting to change the folder to the current so we don't do anything
        if(folderId == this.folderId) return
        val element = get(id)

        element.folder_id = folderId
        element.updated_on = dateTimeCreator()
        persistence.saveItem(element)
        items.removeIf{it.id == id}
        notifySubscribers()
    }

    suspend fun changeTitle(id: Long, title: String) {
        if (title.isEmpty()) throw EmptyArgumentEchoNoteException("Title must not be empty")
        val item = get(id)

        // Inside each folder, the title must be unique
        val folderItems = items.filter { it.folder_id == item.folder_id && it.id != id && it.title == title}
        if (folderItems.isNotEmpty()) throw IllegalArgumentEchoNoteException("Title is not unique in the folder")

        item.title = title
        item.updated_on = dateTimeCreator()
        persistence.saveItem(item)
        notifySubscribers()
    }

    suspend fun changeSummary(id: Long, summary: JsonElement) {
        val item = get(id)
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
}