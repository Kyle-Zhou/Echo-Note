package com.example.echonote.data.models

import com.example.echonote.data.entities.Folder
import com.example.echonote.data.persistence.IPersistenceFolder
import com.example.echonote.utils.DESCRIPTION_LIMIT
import com.example.echonote.utils.EmptyArgumentEchoNoteException
import com.example.echonote.utils.FOLDER_TITLE_LIMIT
import com.example.echonote.utils.IllegalArgumentEchoNoteException
import com.example.echonote.utils.IllegalStateEchoNoteException
import com.example.echonote.utils.NotFoundEchoNoteException
import kotlinx.datetime.LocalDateTime

class FolderModel(
        private val persistence: IPersistenceFolder,
        private val dateTimeCreator: () -> LocalDateTime
    ): IPublisher() {
    val folders: MutableList<Folder> = emptyList<Folder>().toMutableList()

    /**
     * Returns true if this call initialized the model
     */
    suspend fun init() {
        folders.clear()
        folders.addAll(persistence.loadFolders().toMutableList())
        notifySubscribers()
    }

    suspend fun add(title: String, description: String?) {
        if (title.isEmpty()) throw EmptyArgumentEchoNoteException("Title must not be empty")
        if (title.length >= FOLDER_TITLE_LIMIT) throw IllegalArgumentEchoNoteException("Folder title must be under $FOLDER_TITLE_LIMIT characters")
        if (description != null && description.length >= DESCRIPTION_LIMIT) throw IllegalArgumentEchoNoteException("Description must be under $DESCRIPTION_LIMIT characters")
        val element = folders.find { it.title == title }
        if (element != null) throw IllegalArgumentEchoNoteException("Title already in use")
        val currentTime = dateTimeCreator()
        val folder = persistence.createFolder(title, description, currentTime, currentTime)
        folders.add(folder)
        notifySubscribers()
    }

    suspend fun changeTitle(id: Long, title: String) {
        if(title.isEmpty()) throw EmptyArgumentEchoNoteException("Title must not be empty")
        if (title.length >= FOLDER_TITLE_LIMIT) throw IllegalArgumentEchoNoteException("Folder title must be under $FOLDER_TITLE_LIMIT characters")
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
        if (description != null && description.length >= DESCRIPTION_LIMIT) throw IllegalArgumentEchoNoteException("Description must be under $DESCRIPTION_LIMIT characters")
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
        folders.removeIf { it.id == id }
        notifySubscribers()
    }
}