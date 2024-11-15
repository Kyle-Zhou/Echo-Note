package com.example.echonote.data.models

import com.example.echonote.data.persistence.IPersistence
import com.example.echonote.data.entities.Folder
import kotlinx.datetime.LocalDateTime

class FolderModel private constructor(
    private val persistence: IPersistence,
    private val dateTimeCreator: () -> LocalDateTime,
    val folders: MutableList<Folder> // Must pass in the folders directly to constructor to use invoke operator below
    ) {

    companion object {
        suspend operator fun invoke(persistence: IPersistence, dateTimeCreator: () -> LocalDateTime)
        = FolderModel(persistence, dateTimeCreator, persistence.loadFolders().toMutableList())
    }

    suspend fun add(title: String, description: String?): Long {
        val element = folders.find { it.title == title }
        if (element != null) throw IllegalArgumentException("Title already in use")
        val count = persistence.getFoldersCount() + 1
        val currentTime = dateTimeCreator()
        val folder = Folder(count, persistence.getCurrentUser()!!, title, description, currentTime, currentTime)
        folders.add(folder)
        persistence.saveFolders(folders)
        return count
    }

    suspend fun changeTitle(id: Long, title: String) {
        for(current in folders) {
            if(current.id != id && current.title == title) {
                throw IllegalArgumentException("Title $title is already in use")
            } else if(current.id == id){
                current.title = title
                current.updated_on = dateTimeCreator()
                persistence.saveFolders(folders)
                return
            }
        }
    }

    suspend fun changeDescription(id: Long, description: String?) {
        val element = folders.find { it.id == id }
        if(element == null) throw IllegalArgumentException("Id doesn't exist for this folder")
        element.description = description
        element.updated_on = dateTimeCreator()
        persistence.saveFolders(folders)
    }

    suspend fun del(id: Long) {
        folders.removeIf{it.id == id}
        persistence.saveFolders(folders)
    }

    suspend fun save() {
        persistence.saveFolders(folders)
    }

}