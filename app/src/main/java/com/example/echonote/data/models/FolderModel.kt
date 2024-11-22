package com.example.echonote.data.models

import com.example.echonote.data.persistence.IPersistence
import com.example.echonote.data.entities.Folder
import kotlinx.datetime.LocalDateTime

class FolderModel(
        private val persistence: IPersistence,
        private val dateTimeCreator: () -> LocalDateTime
    ): IPublisher() {
    val folders: MutableList<Folder> = emptyList<Folder>().toMutableList()
    var isInitialized = false

    /**
     * Returns true if this call initialized the model
     */
    suspend fun init(): Boolean {
        if (isInitialized) {
            return false
        }
        folders.addAll(persistence.loadFolders().toMutableList())
        isInitialized = true
        notifySubscribers()
        return true
    }

    suspend fun add(title: String, description: String?) {
        val element = folders.find { it.title == title }
        if (element != null) throw IllegalArgumentException("Title already in use")
        val currentTime = dateTimeCreator()
        val folder = persistence.createFolder(title, description, currentTime, currentTime)
        folders.add(folder)
        notifySubscribers()
    }

    suspend fun changeTitle(id: Long, title: String) {
        val element = folders.find { it.id != id && it.title == title }
        if (element != null) throw IllegalArgumentException("Title $title is already in use")
        val current = folders.find { it.id == id }
        if(current == null) throw IllegalArgumentException("No id $id exists")
        current.title = title
        current.updated_on = dateTimeCreator()
        persistence.saveFolders(folders)
        notifySubscribers()
    }

    suspend fun changeDescription(id: Long, description: String?) {
        val element = folders.find { it.id == id }
        if(element == null) throw IllegalArgumentException("Id doesn't exist for this folder")
        element.description = description
        element.updated_on = dateTimeCreator()
        persistence.saveFolders(folders)
        notifySubscribers()
    }

    suspend fun del(id: Long) {
        persistence.deleteFolder(id)
        folders.removeIf{it.id == id}
        notifySubscribers()
    }

    suspend fun save() {
        persistence.saveFolders(folders)
    }

}