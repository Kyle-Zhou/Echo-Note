package com.example.echonote.data.persistence

import com.example.echonote.data.entities.Folder
import com.example.echonote.data.entities.User

interface IPersistence {

    // TODO: Remove this
    suspend fun loadUsers(): List<User>

//    TODO: Add more
    fun loadFolders(): List<Folder>
    fun saveFolders(folders: List<Folder>)

    suspend fun signupUser(userEmail: String, userPassword: String)
    suspend fun loginUser(userEmail: String, userPassword: String)

}