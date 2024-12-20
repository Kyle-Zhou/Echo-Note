package com.example.echonote.data.controller

import com.example.echonote.data.models.ItemModel
import com.example.echonote.utils.IllegalArgumentEchoNoteException
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.Json

enum class ItemControllerEvent {
    ADD, DEL, RENAME, MOVE, CHANGE_CONTENT
}

class ItemController {
    val itemModels = mutableListOf<ItemModel>()

    fun getItemModel(currentFolderId: Long): ItemModel {
        val itemModel = itemModels.find { it.folderId == currentFolderId }
        if(itemModel == null) throw IllegalArgumentEchoNoteException("ItemModel with folderId: $currentFolderId doesn't exist in the ItemController")
        return itemModel
    }

    suspend fun invoke(
        event: ItemControllerEvent,
        currentFolderId: Long,
        id: Long = 0,
        title: String = "",
        summary: JsonElement = Json.decodeFromString("""{}"""),
        folderId: Long = 0
    ) {
        val itemModel = getItemModel(currentFolderId)
        when(event) {
            ItemControllerEvent.ADD -> itemModel.add(title, summary)
            ItemControllerEvent.DEL -> itemModel.del(id)
            ItemControllerEvent.MOVE -> {
//                If the currentFolderId is the same as the target do nothing
                if(currentFolderId == folderId) return

                val item = itemModel.get(id)
                val recipientItemModel = getItemModel(folderId)
                if (recipientItemModel.items.find { it.title == item.title } != null)
                    throw IllegalArgumentEchoNoteException("Each item in a given folder must have a unique title")
                itemModel.changeFolder(id, folderId)
                recipientItemModel.moveIn(item)
            }
            ItemControllerEvent.RENAME -> itemModel.changeTitle(id, title)
            ItemControllerEvent.CHANGE_CONTENT -> itemModel.changeSummary(id, summary)
        }
    }

    /**
     * Attach the itemModel if it doesn't exist. Otherwise it does nothing
     */
    fun attach(itemModel: ItemModel){
        try {
            getItemModel(itemModel.folderId)
        } catch (_: IllegalArgumentEchoNoteException) {
            itemModels.add(itemModel)
        }
    }
}