package com.example.echonote.data.controller

import com.example.echonote.data.models.FolderModel

enum class FolderControllerEvent {
    ADD, DEL, RENAME, CHANGE_DESC, SAVE
}

class FolderController (private val folderModel: FolderModel) {
    suspend fun invoke(event: FolderControllerEvent, id: Long=0, title: String="", description: String?=null) {
        when (event) {
            FolderControllerEvent.ADD -> folderModel.add(title, description)
            FolderControllerEvent.DEL -> folderModel.del(id)
            FolderControllerEvent.RENAME -> folderModel.changeTitle(id, title)
            FolderControllerEvent.CHANGE_DESC -> folderModel.changeDescription(id, description)
            FolderControllerEvent.SAVE -> folderModel.save()
        }
    }
}