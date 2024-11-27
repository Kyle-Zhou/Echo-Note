package com.example.echonote.data.models

import com.example.echonote.data.persistence.IPersistence
import com.example.echonote.data.entities.Folder
import com.example.echonote.utils.EmptyArgumentEchoNoteException
import com.example.echonote.utils.IllegalArgumentEchoNoteException
import com.example.echonote.utils.IllegalStateEchoNoteException
import com.example.echonote.utils.NotFoundEchoNoteException
import kotlinx.datetime.LocalDateTime

class FolderModel(
        val persistence: IPersistence,
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
        if (title.isEmpty()) throw EmptyArgumentEchoNoteException("Title must not be empty")
        val element = folders.find { it.title == title }
        if (element != null) throw IllegalArgumentEchoNoteException("Title already in use")
        val currentTime = dateTimeCreator()
        val folder = persistence.createFolder(title, description, currentTime, currentTime)
        folders.add(folder)
        notifySubscribers()
    }

    fun getFolder(folderId: Long): Folder {
        val current = folders.find { it.id == folderId }
        if(current == null) throw IllegalArgumentException("No id $folderId exists")
        return current;
    }

    suspend fun changeTitle(id: Long, title: String) {
        val element = folders.find { it.id != id && it.title == title }
        if (element != null) throw IllegalArgumentEchoNoteException("Title $title is already in use")
        val current = folders.find { it.id == id }
        if(current == null) throw NotFoundEchoNoteException("No id $id exists")
        current.title = title
        current.updated_on = dateTimeCreator()
        persistence.saveFolder(current)
        notifySubscribers()
    }

    suspend fun changeDescription(id: Long, description: String?) {
        val element = folders.find { it.id == id }
        if(element == null) throw NotFoundEchoNoteException("Id doesn't exist for this folder")
        element.description = description
        element.updated_on = dateTimeCreator()
        persistence.saveFolder(element)
        notifySubscribers()
    }

    suspend fun del(id: Long) {
        if (folders.size <= 1) throw IllegalStateEchoNoteException("Cannot delete all folders")
        persistence.deleteFolder(id)
        folders.removeIf{it.id == id}
        notifySubscribers()
    }

    suspend fun save() {
        persistence.saveFolders(folders)
    }

}