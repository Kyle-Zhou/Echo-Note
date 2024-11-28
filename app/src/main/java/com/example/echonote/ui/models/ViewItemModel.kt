// Taken with modification from mm's ViewModel.kt
// https://git.uwaterloo.ca/cs346/public/mm/-/blob/main/mobile/src/main/kotlin/net/codebot/mobile/view/ViewModel.kt
package com.example.echonote.ui.models

import androidx.compose.runtime.mutableStateListOf
import com.example.echonote.data.entities.Item
import com.example.echonote.data.models.ISubscriber
import com.example.echonote.data.models.ItemModel

class ViewItemModel(private val model: ItemModel) : ISubscriber {
    val items = mutableStateListOf<Item>()

    init {
        model.subscribe(this)
    }

    override fun update() {
        items.clear()
        for (task in model.items) {
            items.add(task)
        }
    }
}