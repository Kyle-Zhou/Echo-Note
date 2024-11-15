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
        itemModel.add(2, "5 2", Json.decodeFromString("""{"value":"test 5"}"""))
        assertEquals(5, itemModel.items.size)
        itemModel.save()
    }

    @Test
    fun addBadFolderId() = runTest {
        val mockPersistence = MockPersistence(::dateTimeCreator)
        val itemModel = ItemModel(mockPersistence, ::dateTimeCreator2, 1)
        try {
            itemModel.add(3, "5 2", Json.decodeFromString("""{"value":"test 5"}"""))
            assert(false)
        } catch (_: IllegalArgumentException) {
            assert(true)
        }
        itemModel.save()
    }

    @Test
    fun addBadTitle() = runTest {
        val mockPersistence = MockPersistence(::dateTimeCreator)
        val itemModel = ItemModel(mockPersistence, ::dateTimeCreator2, 1)
        // Title already in use in folder 2
        try {
            itemModel.add(2, "4 2", Json.decodeFromString("""{"value":"test 5"}"""))
            assert(false)
        } catch (_: IllegalArgumentException) {
            assert(true)
        }
        itemModel.save()
    }

    @Test
    fun changeFolderGood() = runTest {
        val mockPersistence = MockPersistence(::dateTimeCreator)
        val itemModel = ItemModel(mockPersistence, ::dateTimeCreator2, 1)
        itemModel.changeFolder(4, 1)
        val expectedItem = itemModel.items[3]
        assertEquals(1, expectedItem.folder_id)
        assertEquals(dateTimeCreator2(), expectedItem.updated_on)
        itemModel.save()
    }

    @Test
    fun changeFolderNoFolder() = runTest {
        val mockPersistence = MockPersistence(::dateTimeCreator)
        val itemModel = ItemModel(mockPersistence, ::dateTimeCreator2, 1)

        try {
            itemModel.changeFolder(4, 3) // No folder 3
            assert(false)
        } catch (_: IllegalArgumentException) {
            assert(true)
        }

        try {
            itemModel.save()
            assert(false)
        } catch (_: IllegalArgumentException) {
            assert(true)
        }
    }

    @Test
    fun changeFolderNoItem() = runTest {
        val mockPersistence = MockPersistence(::dateTimeCreator)
        val itemModel = ItemModel(mockPersistence, ::dateTimeCreator2, 1)
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
        itemModel.changeTitle(4, "New Title")
        val expectedItem = itemModel.items[3]
        assertEquals("New Title", expectedItem.title)
        assertEquals(dateTimeCreator2(), expectedItem.updated_on)
        itemModel.save()
    }

    @Test
    fun changeTitleGoodDistinctFolders() = runTest {
        val mockPersistence = MockPersistence(::dateTimeCreator)
        val itemModel = ItemModel(mockPersistence, ::dateTimeCreator2, 1)
        itemModel.changeTitle(4, "2 1") // "2 1" is in use in folder 1 but not in folder 2 which has this item
        val expectedItem = itemModel.items[3]
        assertEquals("2 1", expectedItem.title)
        assertEquals(dateTimeCreator2(), expectedItem.updated_on)
        itemModel.save()
    }

    @Test
    fun changeTitleNoItem() = runTest {
        val mockPersistence = MockPersistence(::dateTimeCreator)
        val itemModel = ItemModel(mockPersistence, ::dateTimeCreator2, 1)
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
        itemModel.changeSummary(4, Json.decodeFromString("""{"value":"New Summary"}"""))
        val expectedItem = itemModel.items[3]
        assertEquals("""{"value":"New Summary"}""", expectedItem.summary.toString())
        assertEquals(dateTimeCreator2(), expectedItem.updated_on)
        itemModel.save()
    }

    @Test
    fun changeSummaryInvalidId() = runTest {
        val mockPersistence = MockPersistence(::dateTimeCreator)
        val itemModel = ItemModel(mockPersistence, ::dateTimeCreator2, 1)
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
        itemModel.del(4) // Id 4 exists
        assertEquals(3, itemModel.items.size)
        itemModel.save()
    }

    @Test
    fun delNone() = runTest {
        val mockPersistence = MockPersistence(::dateTimeCreator)
        val itemModel = ItemModel(mockPersistence, ::dateTimeCreator2, 1)
        // Id 10 doesn't exist
        itemModel.del(10)
        assertEquals(4, itemModel.items.size)
        itemModel.save()
    }
}