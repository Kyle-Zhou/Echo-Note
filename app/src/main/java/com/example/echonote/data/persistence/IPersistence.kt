package com.example.echonote.data.persistence

import com.example.echonote.data.entities.Folder
import com.example.echonote.data.entities.User

interface IPersistence {

    // TODO: Remove this
    fun loadUsers(): List<User>

//    TODO: Add more
    fun loadFolders(): List<Folder>
    fun saveFolders(folders: List<Folder>)
}