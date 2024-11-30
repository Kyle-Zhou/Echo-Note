package com.example.echonote.data.persistence

import com.example.echonote.data.entities.Folder
import kotlinx.datetime.LocalDateTime

interface IPersistenceFolder {
    suspend fun createFolder(title: String, description: String?, created_on: LocalDateTime, update_on: LocalDateTime): Folder
    suspend fun loadFolders(): List<Folder>
    suspend fun saveFolder(folder: Folder)
    suspend fun deleteFolder(id: Long)
}