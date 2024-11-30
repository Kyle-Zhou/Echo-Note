package com.example.echonote.data.persistence

import com.example.echonote.data.entities.Folder
import kotlinx.datetime.LocalDateTime

class MockPersistenceFolder(dateTimeCreator: () -> LocalDateTime): IPersistenceFolder {
    private val localDateTime = dateTimeCreator()
    private var mockFolders = mutableListOf(
        Folder(1, "1", "1", "test 1", localDateTime, localDateTime),
        Folder(2, "1", "2", "test 2", localDateTime, localDateTime),
        Folder(3, "1", "3", "test 3", localDateTime, localDateTime)
    )

    override suspend fun createFolder(
        title: String,
        description: String?,
        created_on: LocalDateTime,
        update_on: LocalDateTime
    ): Folder {
        val newId = (mockFolders.size + 1).toLong()
        val folder = Folder(newId, "1", title, description, created_on, update_on)
        mockFolders.add(folder)
        return folder
    }

    override suspend fun loadFolders(): List<Folder> {
        return mockFolders
    }

    override suspend fun saveFolder(folder: Folder) {

    }

    override suspend fun deleteFolder(id: Long) {

    }
}