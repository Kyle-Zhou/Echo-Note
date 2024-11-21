package com.example.echonote.data.persistence

import com.example.echonote.data.entities.Folder
import com.example.echonote.data.entities.Item

interface IPersistence {

    fun setCurrentUser(userId: String)
    fun getCurrentUser(): String?

    fun getCurrentUserID(): String
    fun getName(): String

//    TODO: Separate into multiple interfaces
    suspend fun loadFolders(): List<Folder>
    suspend fun saveFolders(folders: List<Folder>)

    suspend fun loadItems(folderId: Long): List<Item>
    suspend fun saveItems(items: List<Item>)
    suspend fun saveItem(item: Item)

//    TODO: Remove this when we switch to using UUIDs for ids
    suspend fun getItemsCount(): Long
    suspend fun getFoldersCount(): Long

    suspend fun signupUser(userEmail: String, userPassword: String, userName: String)
    suspend fun loginUser(userEmail: String, userPassword: String)
}