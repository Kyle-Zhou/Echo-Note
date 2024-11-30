package com.example.echonote.ui.models

import com.example.echonote.data.models.ItemModel
import com.example.echonote.data.persistence.MockPersistenceItem
import com.example.echonote.dateTimeCreator
import com.example.echonote.dateTimeCreator2
import com.example.echonote.utils.EmptyArgumentEchoNoteException
import com.example.echonote.utils.IllegalArgumentEchoNoteException
import com.example.echonote.utils.NotFoundEchoNoteException
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

class ViewItemModelTest {

    @Test
    fun addGood() = runTest {
        val mockPersistence = MockPersistenceItem(::dateTimeCreator)
        val itemModel = ItemModel(mockPersistence, ::dateTimeCreator, 1)
        itemModel.init()
        val viewModel = ViewItemModel(itemModel)
        assertEquals(2, viewModel.items.size)

        itemModel.add("5 2", Json.decodeFromString("""{"value":"test 5"}"""))
        assertEquals(3, viewModel.items.size)
    }

    @Test
    fun addBadTitle() = runTest {
        val mockPersistence = MockPersistenceItem(::dateTimeCreator)
        val itemModel = ItemModel(mockPersistence, ::dateTimeCreator2, 2)
        itemModel.init()
        val viewModel = ViewItemModel(itemModel)
        assertEquals(2, viewModel.items.size)

        // Title already in use in folder 2
        try {
            itemModel.add("4 2", Json.decodeFromString("""{"value":"test 5"}"""))
            assert(false)
        } catch (_: IllegalArgumentEchoNoteException) {
            assertEquals(2, viewModel.items.size)
        }
    }

    @Test
    fun changeFolderNothing() = runTest {
        val mockPersistence = MockPersistenceItem(::dateTimeCreator)
        val itemModel = ItemModel(mockPersistence, ::dateTimeCreator2, 1)
        itemModel.init()
        val viewModel = ViewItemModel(itemModel)
        assertEquals(2, viewModel.items.size)

        itemModel.changeFolder(2, 1)
        val expectedItem = viewModel.items.find { it.id == 2L }!!
        assertEquals(1, expectedItem.folder_id)
        assertEquals(2, viewModel.items.size)
    }

    @Test
    fun changeFolderGood() = runTest {
        val mockPersistence = MockPersistenceItem(::dateTimeCreator)
        val itemModel = ItemModel(mockPersistence, ::dateTimeCreator2, 1)
        itemModel.init()
        val viewModel = ViewItemModel(itemModel)
        assertEquals(2, viewModel.items.size)

        itemModel.changeFolder(2, 2)
        assertEquals(1, viewModel.items.size)
    }

    @Test
    fun changeFolderNoFolder() = runTest {
        val mockPersistence = MockPersistenceItem(::dateTimeCreator)
        val itemModel = ItemModel(mockPersistence, ::dateTimeCreator2, 1)
        itemModel.init()
        val viewModel = ViewItemModel(itemModel)
        assertEquals(2, viewModel.items.size)

        try {
            itemModel.changeFolder(4, 3) // No folder 3
            assert(false)
        } catch (_: NotFoundEchoNoteException) {
            assertEquals(2, viewModel.items.size)
        }
    }

    @Test
    fun changeFolderNoItem() = runTest {
        val mockPersistence = MockPersistenceItem(::dateTimeCreator)
        val itemModel = ItemModel(mockPersistence, ::dateTimeCreator2, 1)
        itemModel.init()
        val viewModel = ViewItemModel(itemModel)
        assertEquals(2, viewModel.items.size)

        try {
            itemModel.changeFolder(5, 2) // No item 5
            assert(false)
        } catch (_: NotFoundEchoNoteException) {
            assertEquals(2, viewModel.items.size)
        }
    }

    @Test
    fun changeTitleGood() = runTest {
        val mockPersistence = MockPersistenceItem(::dateTimeCreator)
        val itemModel = ItemModel(mockPersistence, ::dateTimeCreator2, 1)
        itemModel.init()
        val viewModel = ViewItemModel(itemModel)
        assertEquals(2, viewModel.items.size)

        itemModel.changeTitle(2, "New Title")
        val expectedItem = viewModel.items.find { it.id == 2L }!!
        assertEquals(2, viewModel.items.size)
        assertEquals("New Title", expectedItem.title)
        assertEquals(dateTimeCreator2(), expectedItem.updated_on)
    }

    @Test
    fun changeTitleNoItem() = runTest {
        val mockPersistence = MockPersistenceItem(::dateTimeCreator)
        val itemModel = ItemModel(mockPersistence, ::dateTimeCreator2, 1)
        itemModel.init()
        val viewModel = ViewItemModel(itemModel)
        assertEquals(2, viewModel.items.size)

        try {
            itemModel.changeTitle(5, "Item 5") // No item 5
            assert(false)
        } catch (_: NotFoundEchoNoteException) {
            assertEquals(2, viewModel.items.size)
        }
    }

    @Test
    fun changeTitleBadEmptyTitle() = runTest {
        val mockPersistence = MockPersistenceItem(::dateTimeCreator)
        val itemModel = ItemModel(mockPersistence, ::dateTimeCreator2, 1)
        itemModel.init()
        val viewModel = ViewItemModel(itemModel)
        assertEquals(2, viewModel.items.size)

        try {
            itemModel.changeTitle(2, "")
            assert(false)
        } catch (_: EmptyArgumentEchoNoteException) {
            assertEquals(2, viewModel.items.size)
        }
    }

    @Test
    fun changeTitleDuplicate() = runTest {
        val mockPersistence = MockPersistenceItem(::dateTimeCreator)
        val itemModel = ItemModel(mockPersistence, ::dateTimeCreator2, 2)
        itemModel.init()
        val viewModel = ViewItemModel(itemModel)
        assertEquals(2, viewModel.items.size)

        try {
            itemModel.changeTitle(4, "3 2") // "3 2" already in use in folder 2
            assert(false)
        } catch (_: IllegalArgumentEchoNoteException) {
            assertEquals(2, viewModel.items.size)
        }
    }

    @Test
    fun changeSummaryGood() = runTest {
        val mockPersistence = MockPersistenceItem(::dateTimeCreator)
        val itemModel = ItemModel(mockPersistence, ::dateTimeCreator2, 1)
        itemModel.init()
        val viewModel = ViewItemModel(itemModel)
        assertEquals(2, viewModel.items.size)

        itemModel.changeSummary(1, Json.decodeFromString("""{"value":"New Summary"}"""))
        val expectedItem = viewModel.items.find { it.id == 1L }!!
        assertEquals(2, viewModel.items.size)
        assertEquals("""{"value":"New Summary"}""", expectedItem.summary.toString())
        assertEquals(dateTimeCreator2(), expectedItem.updated_on)
    }

    @Test
    fun changeSummaryInvalidId() = runTest {
        val mockPersistence = MockPersistenceItem(::dateTimeCreator)
        val itemModel = ItemModel(mockPersistence, ::dateTimeCreator2, 1)
        itemModel.init()
        val viewModel = ViewItemModel(itemModel)
        assertEquals(2, viewModel.items.size)

        try {
            itemModel.changeSummary(5, Json.decodeFromString("""{"value":"New Summary"}""")) // no id 5
            assert(false)
        } catch (_: NotFoundEchoNoteException) {
            assertEquals(2, viewModel.items.size)
        }
    }

    @Test
    fun delGood() = runTest {
        val mockPersistence = MockPersistenceItem(::dateTimeCreator)
        val itemModel = ItemModel(mockPersistence, ::dateTimeCreator2, 1)
        itemModel.init()
        val viewModel = ViewItemModel(itemModel)
        assertEquals(2, viewModel.items.size)

        itemModel.del(2) // Id 4 exists
        assertEquals(1, viewModel.items.size)
    }

    @Test
    fun delNone() = runTest {
        val mockPersistence = MockPersistenceItem(::dateTimeCreator)
        val itemModel = ItemModel(mockPersistence, ::dateTimeCreator2, 1)
        itemModel.init()
        val viewModel = ViewItemModel(itemModel)
        assertEquals(2, viewModel.items.size)

        // Id 10 doesn't exist
        itemModel.del(10)
        assertEquals(2, viewModel.items.size)
    }
}