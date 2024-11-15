package com.example.echonote.data.persistence

import com.example.echonote.data.entities.Folder
import com.example.echonote.data.entities.Item
import com.example.echonote.data.entities.User
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

class MockPersistence(private val dateTimeCreator: () -> LocalDateTime): IPersistence {
    private val mockItems = listOf(
        Item(1, 1, "1 1", stringToJson("test 1"), dateTimeCreator(), dateTimeCreator()),
        Item(2, 1, "2 1", stringToJson("test 2"), dateTimeCreator(), dateTimeCreator()),
        Item(3, 2, "3 2", stringToJson("test 3"), dateTimeCreator(), dateTimeCreator()),
        Item(4, 2, "4 2", stringToJson("test 4"), dateTimeCreator(), dateTimeCreator()),
    )

    private fun stringToJson(value: String): JsonElement {
        return Json.decodeFromString<JsonElement>("""{"value": "$value"}""")
    }

    override suspend fun loadUsers(): List<User> {
        TODO("Not yet implemented")
    }

    override fun getCurrentUser(): Int? {
        return 1
    }

    override fun setCurrentUser(userId: Int) {

    }

    override suspend fun loadFolders(): List<Folder> {
        val localDateTime = dateTimeCreator()
        val list = mutableListOf(
            Folder(1, 1, "1", "test 1", localDateTime, localDateTime),
            Folder(2, 1, "2", "test 2", localDateTime, localDateTime),
            Folder(3, 1, "3", "test 3", localDateTime, localDateTime)
        )
        return list
    }

    override suspend fun saveFolders(folders: List<Folder>) {

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

    override suspend fun getFoldersCount(): Long {
        return 2
    }

    override suspend fun signupUser(userEmail: String, userPassword: String) {
        TODO("Not yet implemented")
    }

    override suspend fun loginUser(userEmail: String, userPassword: String) {
        TODO("Not yet implemented")
    }

    override suspend fun getItemsCount(): Long {
        return mockItems.size.toLong()
    }
}