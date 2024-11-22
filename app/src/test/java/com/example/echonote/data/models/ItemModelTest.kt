package com.example.echonote.data.models
import com.example.echonote.data.persistence.MockPersistence
import com.example.echonote.data.entities.Item
import com.example.echonote.dateTimeCreator
import com.example.echonote.dateTimeCreator2
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

class ItemModelTest {

    @Test
    fun addGood() = runTest {
        val mockPersistence = MockPersistence(::dateTimeCreator)
        val itemModel = ItemModel(mockPersistence, ::dateTimeCreator, 1)
        itemModel.init()

        itemModel.add("5 2", Json.decodeFromString("""{"value":"test 5"}"""))
        assertEquals(3, itemModel.items.size)
        itemModel.save()
    }

    @Test
    fun addBadTitle() = runTest {
        val mockPersistence = MockPersistence(::dateTimeCreator)
        val itemModel = ItemModel(mockPersistence, ::dateTimeCreator2, 2)
        itemModel.init()

        // Title already in use in folder 2
        try {
            itemModel.add("4 2", Json.decodeFromString("""{"value":"test 5"}"""))
            assert(false)
        } catch (_: IllegalArgumentException) {
            assert(true)
        }
        itemModel.save()
    }

    @Test
    fun changeFolderNothing() = runTest {
        val mockPersistence = MockPersistence(::dateTimeCreator)
        val itemModel = ItemModel(mockPersistence, ::dateTimeCreator2, 1)
        itemModel.init()

        itemModel.changeFolder(2, 1)
        val expectedItem = itemModel.items[1]
        assertEquals(1, expectedItem.folder_id)
        itemModel.save()
    }

    @Test
    fun changeFolderGood() = runTest {
        val mockPersistence = MockPersistence(::dateTimeCreator)
        val itemModel = ItemModel(mockPersistence, ::dateTimeCreator2, 1)
        itemModel.init()

        itemModel.changeFolder(2, 2)
        assertEquals(1, itemModel.items.size)
        itemModel.save()
    }

    @Test
    fun changeFolderNoFolder() = runTest {
        val mockPersistence = MockPersistence(::dateTimeCreator)
        val itemModel = ItemModel(mockPersistence, ::dateTimeCreator2, 1)
        itemModel.init()

        try {
            itemModel.changeFolder(4, 3) // No folder 3
            assert(false)
        } catch (_: IllegalArgumentException) {
            assert(true)
        }
        itemModel.save()
    }

    @Test
    fun changeFolderNoItem() = runTest {
        val mockPersistence = MockPersistence(::dateTimeCreator)
        val itemModel = ItemModel(mockPersistence, ::dateTimeCreator2, 1)
        itemModel.init()

        try {
            itemModel.changeFolder(5, 2) // No item 5
            assert(false)
        } catch (_: IllegalArgumentException) {
            assert(true)
        }
        itemModel.save()
    }

    @Test
    fun changeTitleGood() = runTest {
        val mockPersistence = MockPersistence(::dateTimeCreator)
        val itemModel = ItemModel(mockPersistence, ::dateTimeCreator2, 1)
        itemModel.init()

        itemModel.changeTitle(2, "New Title")
        val expectedItem = itemModel.items[1]
        assertEquals("New Title", expectedItem.title)
        assertEquals(dateTimeCreator2(), expectedItem.updated_on)
        itemModel.save()
    }

    @Test
    fun changeTitleNoItem() = runTest {
        val mockPersistence = MockPersistence(::dateTimeCreator)
        val itemModel = ItemModel(mockPersistence, ::dateTimeCreator2, 1)
        itemModel.init()

        try {
            itemModel.changeTitle(5, "") // No item 5
            assert(false)
        } catch (_: IllegalArgumentException) {
            assert(true)
        }
        itemModel.save()
    }

    @Test
    fun changeTitleDuplicate() = runTest {
        val mockPersistence = MockPersistence(::dateTimeCreator)
        val itemModel = ItemModel(mockPersistence, ::dateTimeCreator2, 1)
        itemModel.init()

        try {
            itemModel.changeTitle(4, "3 2") // "3 2" already in use in folder 2
            assert(false)
        } catch (_: IllegalArgumentException) {
            assert(true)
        }
        itemModel.save()
    }

    @Test
    fun changeSummaryGood() = runTest {
        val mockPersistence = MockPersistence(::dateTimeCreator)
        val itemModel = ItemModel(mockPersistence, ::dateTimeCreator2, 1)
        itemModel.init()

        itemModel.changeSummary(1, Json.decodeFromString("""{"value":"New Summary"}"""))
        val expectedItem = itemModel.items[0]
        assertEquals("""{"value":"New Summary"}""", expectedItem.summary.toString())
        assertEquals(dateTimeCreator2(), expectedItem.updated_on)
        itemModel.save()
    }

    @Test
    fun changeSummaryInvalidId() = runTest {
        val mockPersistence = MockPersistence(::dateTimeCreator)
        val itemModel = ItemModel(mockPersistence, ::dateTimeCreator2, 1)
        itemModel.init()

        try {
            itemModel.changeSummary(5, Json.decodeFromString("""{"value":"New Summary"}""")) // no id 5
            assert(false)
        } catch (_: IllegalArgumentException) {
            assert(true)
        }
        itemModel.save()
    }

    @Test
    fun delGood() = runTest {
        val mockPersistence = MockPersistence(::dateTimeCreator)
        val itemModel = ItemModel(mockPersistence, ::dateTimeCreator2, 1)
        itemModel.init()

        itemModel.del(2) // Id 4 exists
        assertEquals(1, itemModel.items.size)
        itemModel.save()
    }

    @Test
    fun delNone() = runTest {
        val mockPersistence = MockPersistence(::dateTimeCreator)
        val itemModel = ItemModel(mockPersistence, ::dateTimeCreator2, 1)
        itemModel.init()

        // Id 10 doesn't exist
        itemModel.del(10)
        assertEquals(2, itemModel.items.size)
        itemModel.save()
    }
}