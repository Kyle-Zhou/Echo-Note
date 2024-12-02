package com.example.echonote.data.controller

import com.example.echonote.data.models.ItemModel
import com.example.echonote.data.persistence.MockPersistenceItem
import com.example.echonote.dateTimeCreator
import com.example.echonote.utils.EmptyArgumentEchoNoteException
import com.example.echonote.utils.ITEM_TITLE_LIMIT
import com.example.echonote.utils.IllegalArgumentEchoNoteException
import com.example.echonote.utils.NotFoundEchoNoteException
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

class ItemControllerTest {
    private suspend fun createItemModel(id: Long = 1): ItemModel {
        val mockPersistence = MockPersistenceItem(::dateTimeCreator)
        val itemModel = ItemModel(mockPersistence, ::dateTimeCreator, id)
        itemModel.init()
        return itemModel
    }

    @Test
    fun attachGood() = runTest {
        val itemModel = createItemModel()
        val itemController = ItemController()
        itemController.attach(itemModel)
        assertEquals(1, itemController.itemModels.size)
    }

    @Test
    fun attachGoodNothing() = runTest {
        val itemModel = createItemModel()
        val itemController = ItemController()
        val itemModel2 = createItemModel() // Same id as itemModel
        itemController.attach(itemModel)
        itemController.attach(itemModel2)
        assertEquals(1, itemController.itemModels.size)
    }

    @Test
    fun attachGoodMany() = runTest {
        val itemModel = createItemModel()
        val itemController = ItemController()
        val itemModel2 = createItemModel(2)
        itemController.attach(itemModel)
        itemController.attach(itemModel2)
        assertEquals(2, itemController.itemModels.size)
    }

    @Test
    fun addGood() = runTest {
        val itemModel = createItemModel()
        val itemController = ItemController()
        itemController.attach(itemModel)

        itemController.invoke(ItemControllerEvent.ADD, 1.toLong(),  title="5 2", summary = Json.decodeFromString("""{"value":"test 5"}"""))
        assertEquals(3, itemModel.items.size)
    }

    @Test
    fun addBadTitle() = runTest {
        val itemModel = createItemModel()
        val itemController = ItemController()
        itemController.attach(itemModel)

        // Title already in use in folder 1
        try {
            itemController.invoke(ItemControllerEvent.ADD,1.toLong(), title = "2 1", summary = Json.decodeFromString("""{"value":"test 5"}"""))
            assert(false)
        } catch (_: IllegalArgumentEchoNoteException) {
            assert(true)
        }
    }

    @Test
    fun addTooLongTitle() = runTest {
        val itemModel = createItemModel()
        val itemController = ItemController()
        itemController.attach(itemModel)
        assertEquals(2, itemModel.items.size)
        val char = "x"
        val title = char.repeat(ITEM_TITLE_LIMIT)
        assertEquals(0, itemModel.items.count { it.title == title })

        try {
            itemController.invoke(ItemControllerEvent.ADD, 1L, title= title, summary = Json.decodeFromString("""{"value":"test 5"}"""))
            assert(false)
        } catch (_: IllegalArgumentEchoNoteException) {
            assertEquals(0, itemModel.items.count { it.title == title })
            assertEquals(2, itemModel.items.size)
        }
    }

    @Test
    fun changeFolderNothing() = runTest {
        val itemModel = createItemModel()
        val itemController = ItemController()
        itemController.attach(itemModel)

        itemController.invoke(ItemControllerEvent.MOVE, 1.toLong(),id=2, folderId = 1)
        val expectedItem = itemModel.items[1]
        assertEquals(1, expectedItem.folder_id)
        assertEquals(2, itemModel.items.size)
    }

    @Test
    fun changeFolderGood() = runTest {
        val itemModel = createItemModel()
        val itemController = ItemController()
        itemController.attach(itemModel)
        val itemModel2 = createItemModel(2)
        itemController.attach(itemModel2)

        itemController.invoke(ItemControllerEvent.MOVE, 1.toLong(), id=2, folderId = 2)
        assertEquals(1, itemModel.items.size)
        assertEquals(null, itemModel.items.find { it.id == 2.toLong() })
        assertEquals(3, itemModel2.items.size)
    }

    @Test
    fun changeFolderNoFolder() = runTest {
        val itemModel = createItemModel()
        val itemController = ItemController()
        itemController.attach(itemModel)
        val itemModel2 = createItemModel(2)
        itemController.attach(itemModel2)

        try {
            // No folder 3
            itemController.invoke(ItemControllerEvent.MOVE, 1.toLong(),id=4, folderId = 3)
            assert(false)
        } catch (_: NotFoundEchoNoteException) {
            assert(true)
        }
    }

    @Test
    fun changeFolderNoItem() = runTest {
        val itemModel = createItemModel()
        val itemController = ItemController()
        itemController.attach(itemModel)
        val itemModel2 = createItemModel(2)
        itemController.attach(itemModel2)

        try {
            // No item 5
            itemController.invoke(ItemControllerEvent.MOVE,1.toLong(), id=5, folderId = 2)
            assert(false)
        } catch (_: NotFoundEchoNoteException) {
            assert(true)
        }
    }

    @Test
    fun changeFolderDuplicate() = runTest {
        val itemModel = createItemModel()
        val itemController = ItemController()
        itemController.attach(itemModel)
        val itemModel2 = createItemModel(2)
        itemController.attach(itemModel2)

//        Setup to make sure that each folder has an item with the same name
        itemController.invoke(ItemControllerEvent.RENAME, 1L, id=1, title = "3 2")
        assertEquals(1, itemModel.items.count { it.title == "3 2" })
        assertEquals("3 2", itemModel.items.find { it.id == 1L }!!.title)
        assertEquals(1, itemModel2.items.count { it.title == "3 2" })

        try {
            // Move item 1 from folder 1 into folder 2
            itemController.invoke(ItemControllerEvent.MOVE,1L, id=1, folderId = 2)
            assert(false)
        } catch (_: IllegalArgumentEchoNoteException) {
            assertEquals(1, itemModel.items.count { it.title == "3 2" })
            assertEquals(1, itemModel2.items.count { it.title == "3 2" })
        }
    }

    @Test
    fun changeTitleGood() = runTest {
        val itemModel = createItemModel()
        val itemController = ItemController()
        itemController.attach(itemModel)

        itemController.invoke(ItemControllerEvent.RENAME,1.toLong(), id=2, title = "New Title")
        val expectedItem = itemModel.items[1]
        assertEquals("New Title", expectedItem.title)
        assertEquals(dateTimeCreator(), expectedItem.updated_on)
    }

    @Test
    fun changeTitleNoItem() = runTest {
        val itemModel = createItemModel()
        val itemController = ItemController()
        itemController.attach(itemModel)

        try {
            // No item 5
            itemController.invoke(ItemControllerEvent.RENAME,1.toLong(), id=5, title = "Item 5")
            assert(false)
        } catch (_: NotFoundEchoNoteException) {
            assert(true)
        }
    }

    @Test
    fun changeTitleBadEmptyTitle() = runTest {
        val itemModel = createItemModel()
        val itemController = ItemController()
        itemController.attach(itemModel)

        try {
            itemController.invoke(ItemControllerEvent.RENAME,1.toLong(), id=2, title = "")
            assert(false)
        } catch (_: EmptyArgumentEchoNoteException) {
            assert(true)
        }
    }

    @Test
    fun changeTitleDuplicate() = runTest {
        val itemModel = createItemModel()
        val itemController = ItemController()
        itemController.attach(itemModel)

        try {
            // "2 1" already in use in folder 1
            itemController.invoke(ItemControllerEvent.RENAME,1.toLong(), id=1, title = "2 1")
            assert(false)
        } catch (_: IllegalArgumentEchoNoteException) {
            assert(true)
        }
    }

    @Test
    fun changeTitleTooLong() = runTest {
        val itemModel = createItemModel()
        val itemController = ItemController()
        itemController.attach(itemModel)
        assertEquals(2, itemModel.items.size)
        val char = "x"
        val title = char.repeat(ITEM_TITLE_LIMIT)
        assertEquals(0, itemModel.items.count { it.title == title })

        try {
            itemController.invoke(ItemControllerEvent.RENAME,1L, id=1, title = title)
            assert(false)
        } catch (_: IllegalArgumentEchoNoteException) {
            assertEquals(0, itemModel.items.count { it.title == title })
            assertEquals(2, itemModel.items.size)
        }
    }

    @Test
    fun changeSummaryGood() = runTest {
        val itemModel = createItemModel()
        val itemController = ItemController()
        itemController.attach(itemModel)

        itemController.invoke(ItemControllerEvent.CHANGE_CONTENT,1.toLong(), id=1, summary = Json.decodeFromString("""{"value":"New Summary"}"""))
        val expectedItem = itemModel.items[0]
        assertEquals("""{"value":"New Summary"}""", expectedItem.summary.toString())
        assertEquals(dateTimeCreator(), expectedItem.updated_on)
    }

    @Test
    fun changeSummaryInvalidId() = runTest {
        val itemModel = createItemModel()
        val itemController = ItemController()
        itemController.attach(itemModel)

        try {
            // no id 5
            itemController.invoke(ItemControllerEvent.CHANGE_CONTENT,1.toLong(), id=5, summary = Json.decodeFromString("""{"value":"New Summary"}"""))
            assert(false)
        } catch (_: NotFoundEchoNoteException) {
            assert(true)
        }
    }

    @Test
    fun delGood() = runTest {
        val itemModel = createItemModel()
        val itemController = ItemController()
        itemController.attach(itemModel)

        itemController.invoke(ItemControllerEvent.DEL,1.toLong(), id = 2)// Id 2 exists
        assertEquals(1, itemModel.items.size)
    }

    @Test
    fun delNone() = runTest {
        val itemModel = createItemModel()
        val itemController = ItemController()
        itemController.attach(itemModel)

        // Id 10 doesn't exist
        itemController.invoke(ItemControllerEvent.DEL, 1.toLong(), id=10)
        assertEquals(2, itemModel.items.size)
    }
}