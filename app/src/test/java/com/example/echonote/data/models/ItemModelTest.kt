package com.example.echonote.data.models
import com.example.echonote.data.persistence.MockPersistenceItem
import com.example.echonote.dateTimeCreator
import com.example.echonote.dateTimeCreator2
import com.example.echonote.utils.EmptyArgumentEchoNoteException
import com.example.echonote.utils.ITEM_TITLE_LIMIT
import com.example.echonote.utils.IllegalArgumentEchoNoteException
import com.example.echonote.utils.NotFoundEchoNoteException
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Assert.*
import org.junit.Test

class ItemModelTest {

    @Test
    fun addGood() = runTest {
        val mockPersistence = MockPersistenceItem(::dateTimeCreator)
        val itemModel = ItemModel(mockPersistence, ::dateTimeCreator, 1)
        itemModel.init()
        assertEquals(0, itemModel.items.count { it.title == "5 2" })
        assertEquals(2, itemModel.items.size)

        itemModel.add("5 2", Json.decodeFromString("""{"value":"test 5"}"""))
        assertEquals(3, itemModel.items.size)
        assertEquals(1, itemModel.items.count { it.title == "5 2" })
    }

    @Test
    fun addBadTitle() = runTest {
        val mockPersistence = MockPersistenceItem(::dateTimeCreator)
        val itemModel = ItemModel(mockPersistence, ::dateTimeCreator2, 2)
        itemModel.init()
        assertEquals(2, itemModel.items.size)
        assertEquals(1, itemModel.items.count { it.title == "4 2" })

        // Title already in use in folder 2
        try {
            itemModel.add("4 2", Json.decodeFromString("""{"value":"test 5"}"""))
            assert(false)
        } catch (_: IllegalArgumentEchoNoteException) {
            assertEquals(1, itemModel.items.count { it.title == "4 2" })
            assertEquals(2, itemModel.items.size)
        }
    }

    @Test
    fun addTooLongTitle() = runTest {
        val mockPersistence = MockPersistenceItem(::dateTimeCreator)
        val itemModel = ItemModel(mockPersistence, ::dateTimeCreator2, 2)
        itemModel.init()
        assertEquals(2, itemModel.items.size)
        assertEquals(1, itemModel.items.count { it.title == "4 2" })
        val char = "x"
        val title = char.repeat(ITEM_TITLE_LIMIT)
        assertEquals(0, itemModel.items.count { it.title == title })

        try {
            itemModel.add(title, Json.decodeFromString("""{"value":"test 5"}"""))
            assert(false)
        } catch (_: IllegalArgumentEchoNoteException) {
            assertEquals(0, itemModel.items.count { it.title == title })
            assertEquals(2, itemModel.items.size)
        }
    }

    @Test
    fun changeFolderNothing() = runTest {
        val mockPersistence = MockPersistenceItem(::dateTimeCreator)
        val itemModel = ItemModel(mockPersistence, ::dateTimeCreator2, 1)
        itemModel.init()
        assertEquals(2, itemModel.items.size)
        assertEquals(1, itemModel.items.count { it.id == 2L })

        itemModel.changeFolder(2, 1)
        val expectedItem = itemModel.items.find { it.id == 2L }!!
        assertEquals(1, expectedItem.folder_id)
        assertEquals(2, itemModel.items.size)
    }

    @Test
    fun changeFolderGood() = runTest {
        val mockPersistence = MockPersistenceItem(::dateTimeCreator)
        val itemModel = ItemModel(mockPersistence, ::dateTimeCreator2, 1)
        itemModel.init()
        assertEquals(1, itemModel.items.count { it.id == 2L })
        assertEquals(2, itemModel.items.size)

        itemModel.changeFolder(2, 2)
        assertEquals(0, itemModel.items.count { it.id == 2L })
        assertEquals(1, itemModel.items.size)
    }

    @Test
    fun changeFolderNoFolder() = runTest {
        val mockPersistence = MockPersistenceItem(::dateTimeCreator)
        val itemModel = ItemModel(mockPersistence, ::dateTimeCreator2, 1)
        itemModel.init()
        assertEquals(0, itemModel.items.count { it.folder_id == 3L })
        assertEquals(2, itemModel.items.size)

        try {
            itemModel.changeFolder(4, 3) // No folder 3
            assert(false)
        } catch (_: NotFoundEchoNoteException) {
            assertEquals(0, itemModel.items.count { it.folder_id == 3L })
            assertEquals(2, itemModel.items.size)
        }
    }

    @Test
    fun changeFolderNoItem() = runTest {
        val mockPersistence = MockPersistenceItem(::dateTimeCreator)
        val itemModel = ItemModel(mockPersistence, ::dateTimeCreator2, 1)
        itemModel.init()
        assertEquals(0, itemModel.items.count { it.id == 5L })
        assertEquals(2, itemModel.items.size)

        try {
            itemModel.changeFolder(5, 2) // No item 5
            assert(false)
        } catch (_: NotFoundEchoNoteException) {
            assertEquals(0, itemModel.items.count { it.id == 5L })
            assertEquals(2, itemModel.items.size)
        }
    }

    @Test
    fun changeTitleGood() = runTest {
        val mockPersistence = MockPersistenceItem(::dateTimeCreator)
        val itemModel = ItemModel(mockPersistence, ::dateTimeCreator2, 1)
        itemModel.init()
        assertEquals(1, itemModel.items.count { it.id == 2L })
        assertEquals(0, itemModel.items.count { it.title == "New Title" })
        assertEquals(2, itemModel.items.size)

        itemModel.changeTitle(2, "New Title")
        assertEquals(1, itemModel.items.count { it.title == "New Title" })
        val expectedItem = itemModel.items[1]
        assertEquals("New Title", expectedItem.title)
        assertEquals(dateTimeCreator2(), expectedItem.updated_on)
        assertEquals(2, itemModel.items.size)
    }

    @Test
    fun changeTitleNoItem() = runTest {
        val mockPersistence = MockPersistenceItem(::dateTimeCreator)
        val itemModel = ItemModel(mockPersistence, ::dateTimeCreator2, 1)
        itemModel.init()
        assertNull(itemModel.items.find { it.id == 5L })
        assertEquals(2, itemModel.items.size)

        try {
            itemModel.changeTitle(5, "Item 5") // No item 5
            assert(false)
        } catch (_: NotFoundEchoNoteException) {
            assertNull(itemModel.items.find { it.id == 5L })
            assertEquals(2, itemModel.items.size)
        }
    }

    @Test
    fun changeTitleBadEmptyTitle() = runTest {
        val mockPersistence = MockPersistenceItem(::dateTimeCreator)
        val itemModel = ItemModel(mockPersistence, ::dateTimeCreator2, 1)
        itemModel.init()
        assertEquals(2, itemModel.items.size)

        try {
            itemModel.changeTitle(2, "")
            assert(false)
        } catch (_: EmptyArgumentEchoNoteException) {
            assertNotEquals("", itemModel.items.find { it.id == 2L }!!.title)
            assertEquals(2, itemModel.items.size)
        }
    }

    @Test
    fun changeTitleDuplicate() = runTest {
        val mockPersistence = MockPersistenceItem(::dateTimeCreator)
        val itemModel = ItemModel(mockPersistence, ::dateTimeCreator2, 2)
        itemModel.init()
        assertEquals(1, itemModel.items.count { it.title == "3 2" })
        assertEquals(2, itemModel.items.size)

        try {
            itemModel.changeTitle(4, "3 2") // "3 2" already in use in folder 2
            assert(false)
        } catch (_: IllegalArgumentEchoNoteException) {
            assertEquals(1, itemModel.items.count { it.title == "3 2" })
            assertEquals(2, itemModel.items.size)
        }
    }

    @Test
    fun changeTitleTooLong() = runTest {
        val mockPersistence = MockPersistenceItem(::dateTimeCreator)
        val itemModel = ItemModel(mockPersistence, ::dateTimeCreator2, 2)
        itemModel.init()
        assertEquals(2, itemModel.items.size)
        assertEquals(1, itemModel.items.count { it.title == "4 2" })
        val char = "x"
        val title = char.repeat(ITEM_TITLE_LIMIT)
        assertEquals(0, itemModel.items.count { it.title == title })

        try {
            itemModel.changeTitle(4, title)
            assert(false)
        } catch (_: IllegalArgumentEchoNoteException) {
            assertEquals(0, itemModel.items.count { it.title == title })
            assertEquals(2, itemModel.items.size)
        }
    }

    @Test
    fun changeSummaryGood() = runTest {
        val mockPersistence = MockPersistenceItem(::dateTimeCreator)
        val itemModel = ItemModel(mockPersistence, ::dateTimeCreator2, 1)
        itemModel.init()
        assertNotNull(itemModel.items.find { it.id == 1L })
        assertEquals(2, itemModel.items.size)

        itemModel.changeSummary(1, Json.decodeFromString("""{"value":"New Summary"}"""))
        val expectedItem = itemModel.items.find { it.id == 1L }!!
        assertEquals("""{"value":"New Summary"}""", expectedItem.summary.toString())
        assertEquals(dateTimeCreator2(), expectedItem.updated_on)
        assertEquals(2, itemModel.items.size)
    }

    @Test
    fun changeSummaryInvalidId() = runTest {
        val mockPersistence = MockPersistenceItem(::dateTimeCreator)
        val itemModel = ItemModel(mockPersistence, ::dateTimeCreator2, 1)
        itemModel.init()
        assertNull(itemModel.items.find { it.id == 5L })
        assertEquals(2, itemModel.items.size)

        try {
            itemModel.changeSummary(5, Json.decodeFromString("""{"value":"New Summary"}""")) // no id 5
            assert(false)
        } catch (_: NotFoundEchoNoteException) {
            assertNull(itemModel.items.find { it.id == 5L })
            assertEquals(2, itemModel.items.size)
        }
    }

    @Test
    fun delGood() = runTest {
        val mockPersistence = MockPersistenceItem(::dateTimeCreator)
        val itemModel = ItemModel(mockPersistence, ::dateTimeCreator2, 1)
        itemModel.init()
        assertEquals(1, itemModel.items.count { it.id == 2L })
        assertEquals(2, itemModel.items.size)

        itemModel.del(2) // Id 2 exists
        assertNull(itemModel.items.find { it.id == 2L })
        assertEquals(1, itemModel.items.size)
    }

    @Test
    fun delNone() = runTest {
        val mockPersistence = MockPersistenceItem(::dateTimeCreator)
        val itemModel = ItemModel(mockPersistence, ::dateTimeCreator2, 1)
        itemModel.init()
        assertNull(itemModel.items.find { it.id == 10L })
        assertEquals(2, itemModel.items.size)

        // Id 10 doesn't exist
        itemModel.del(10)
        assertNull(itemModel.items.find { it.id == 10L })
        assertEquals(2, itemModel.items.size)
    }
}