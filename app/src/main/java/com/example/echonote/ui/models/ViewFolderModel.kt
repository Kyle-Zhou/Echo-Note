// Taken with modification from mm's ViewModel.kt
// https://git.uwaterloo.ca/cs346/public/mm/-/blob/main/mobile/src/main/kotlin/net/codebot/mobile/view/ViewModel.kt
package com.example.echonote.ui.models

import androidx.compose.runtime.mutableStateListOf
import com.example.echonote.data.entities.Folder
import com.example.echonote.data.models.FolderModel
import com.example.echonote.data.models.ISubscriber

class ViewFolderModel(private val model: FolderModel) : ISubscriber {
    val folders = mutableStateListOf<Folder>()

    init {
        model.subscribe(this)
    }

    override fun update() {
        folders.clear()
        for (task in model.folders) {
            folders.add(task)
        }
    }
}