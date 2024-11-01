package com.example.echonote.data.models

import com.example.echonote.data.persistence.IPersistence
import com.example.echonote.data.entities.Folder
import kotlinx.datetime.LocalDateTime

class FolderModel(private val persistence: IPersistence, private val dateTimeCreator: () -> LocalDateTime) {
    val folders = mutableListOf<Folder>()

    init {
        folders.addAll(persistence.loadFolders())
    }

    fun add(folder: Folder) {
        for(current in folders) {
            if(current.id == folder.id) {
                throw IllegalArgumentException("Folder ids must be unique")
            }
            if(current.title == folder.title) {
                throw IllegalArgumentException("Cannot add a folder with the same titles")
            }

        }
        folders.add(folder)
    }

    fun changeTitle(id: Int, title: String) {
        for(current in folders) {
            if(current.id != id && current.title == title) {
                throw IllegalArgumentException("Title $title is already in use")
            } else if(current.id == id){
                current.title = title
                current.updated_on = dateTimeCreator()
            }
        }
    }

    fun changeDescription(id: Int, description: String?) {
        val element = folders.find { it.id == id }
        if(element == null) throw IllegalArgumentException("Id doesn't exist for this folder")
        element.description = description
        element.updated_on = dateTimeCreator()
    }

    fun del(element: Folder) {
        folders.remove(
            element
        )
    }

    fun save() {
        persistence.saveFolders(folders)
    }

}