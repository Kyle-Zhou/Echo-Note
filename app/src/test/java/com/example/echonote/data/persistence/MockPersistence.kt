package com.example.echonote.data.persistence

import com.example.echonote.data.entities.Folder
import com.example.echonote.data.entities.User
import kotlinx.datetime.LocalDateTime

class MockPersistence(private val dateTimeCreator: () -> LocalDateTime): IPersistence {
    override fun loadUsers(): List<User> {
        TODO("Not yet implemented")
    }

    override fun loadFolders(): List<Folder> {
        val localDateTime = dateTimeCreator()
        val list = mutableListOf(
            Folder(1, 1, "1", "test 1", localDateTime, localDateTime),
            Folder(2, 1, "2", "test 2", localDateTime, localDateTime),
            Folder(3, 1, "3", "test 3", localDateTime, localDateTime)
        )
        return list
    }

    override fun saveFolders(folders: List<Folder>) {

    }
}