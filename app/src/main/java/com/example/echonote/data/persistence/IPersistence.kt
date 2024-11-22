package com.example.echonote.data.persistence

import com.example.echonote.data.entities.Folder
import com.example.echonote.data.entities.Item
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.json.JsonElement

interface IPersistence {

    fun setCurrentUser(userId: String)
    fun getCurrentUser(): String?

    fun getCurrentUserID(): String
    fun getName(): String

//    TODO: Separate into multiple interfaces
    suspend fun createFolder(title: String, description: String?, created_on: LocalDateTime, update_on: LocalDateTime): Folder
    suspend fun loadFolders(): List<Folder>
    suspend fun saveFolders(folders: List<Folder>)
    suspend fun deleteFolder(id: Long)

    suspend fun createItem(folder_id: Long, title: String, summary: JsonElement, created_on: LocalDateTime, update_on: LocalDateTime): Item
    suspend fun loadItems(folderId: Long): List<Item>
    suspend fun saveItems(items: List<Item>)
    suspend fun saveItem(item: Item)
    suspend fun deleteItem(id: Long)

    suspend fun signupUser(userEmail: String, userPassword: String, userName: String)
    suspend fun loginUser(userEmail: String, userPassword: String)
}