package com.example.echonote.data.persistence

import com.example.echonote.data.entities.Folder
import com.example.echonote.data.entities.Item
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

class MockPersistence(dateTimeCreator: () -> LocalDateTime): IPersistence {
    private val localDateTime = dateTimeCreator()
    private var mockItems = mutableListOf<Item>(
        Item(1, 1, "1 1", stringToJson("test 1"), localDateTime, localDateTime),
        Item(2, 1, "2 1", stringToJson("test 2"), localDateTime, localDateTime),
        Item(3, 2, "3 2", stringToJson("test 3"), localDateTime, localDateTime),
        Item(4, 2, "4 2", stringToJson("test 4"), localDateTime, localDateTime),
    )
    private var mockFolders = mutableListOf(
        Folder(1, "1", "1", "test 1", localDateTime, localDateTime),
        Folder(2, "1", "2", "test 2", localDateTime, localDateTime),
        Folder(3, "1", "3", "test 3", localDateTime, localDateTime)
    )

    private fun stringToJson(value: String): JsonElement {
        return Json.decodeFromString<JsonElement>("""{"value": "$value"}""")
    }

    override fun getCurrentUser(): String? {
        return "1"
    }

    override fun getCurrentUserID(): String {
        TODO("Not yet implemented")
    }

    override fun getName(): String {
        TODO("Not yet implemented")
    }

    override suspend fun createFolder(
        title: String,
        description: String?,
        created_on: LocalDateTime,
        update_on: LocalDateTime
    ): Folder {
        val newId = (mockFolders.size + 1).toLong()
        val folder = Folder(newId, getCurrentUser()!!, title, description, created_on, update_on)
        mockFolders.add(folder)
        return folder
    }

    override suspend fun getFolder(folderId: Long): Folder {
        return mockFolders.find { it.id == folderId }
            ?: throw IllegalArgumentException("Folder not found for folderId=$folderId")
    }
    override fun setCurrentUser(userId: String) {

    }

    override suspend fun loadFolders(): List<Folder> {
        return mockFolders
    }

    override suspend fun saveFolders(folders: List<Folder>) {

    }

    override suspend fun saveFolder(folder: Folder) {

    }

    override suspend fun deleteFolder(id: Long) {

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

    override suspend fun getItem(itemId: Long): Item {
        return mockItems.find { it.id == itemId }
            ?: throw IllegalArgumentException("Item not found for itemId=$itemId")
    }

    override suspend fun loadItems(folderId: Long): List<Item> {
        return mockItems.filter { it.folder_id == folderId }
    }

    override suspend fun saveItems(items: List<Item>) {
        for(item in items) {
            saveItem(item)
        }
    }

    override suspend fun saveItem(item: Item) {
        if(item.folder_id != 1.toLong() && item.folder_id != 2.toLong()) throw IllegalArgumentException("Folder id must be 1 or 2. Given ${item.folder_id}")
    }

    override suspend fun deleteItem(id: Long) {
        mockItems.removeIf {it.id == id}
    }

    override suspend fun signupUser(userEmail: String, userPassword: String, userName: String) {
        TODO("Not yet implemented")
    }

    override suspend fun loginUser(userEmail: String, userPassword: String) {
        TODO("Not yet implemented")
    }

}