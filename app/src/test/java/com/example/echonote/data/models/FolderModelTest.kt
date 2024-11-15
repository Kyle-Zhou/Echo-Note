package com.example.echonote.data.models
import com.example.echonote.data.persistence.MockPersistence
import com.example.echonote.dateTimeCreator
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class FolderModelTest {
    private suspend fun createFolderModel(): FolderModel {
        val mockPersistence = MockPersistence(::dateTimeCreator)
        val folderModel = FolderModel(mockPersistence, ::dateTimeCreator)
        return folderModel
    }

    @Test
    fun addGood() = runTest {
        val folderModel = createFolderModel()
        folderModel.add("Add", null)
        assertEquals(4, folderModel.folders.size)
        folderModel.save()
    }

    @Test
    fun addSameTitle() = runTest {
        val folderModel = createFolderModel()
        println(folderModel.folders)
        try {
            folderModel.add("1", "test")
            assert(false)
        } catch (e: IllegalArgumentException) {
            assert(true)
        }
        folderModel.save()
    }

    @Test
    fun delGood() = runTest {
        val folderModel = createFolderModel()
        folderModel.del(3)
        assertEquals(2, folderModel.folders.size)
        folderModel.save()
    }

    @Test
    fun delNone() = runTest {
        val folderModel = createFolderModel()
 // This folder hasn't been added before
        folderModel.del(10)
        assertEquals(3, folderModel.folders.size)
        folderModel.save()
    }

    @Test
    fun changeTitleGood() = runTest {
        val folderModel = createFolderModel()
        folderModel.changeTitle(3, "Hello")
        val expectedItem = folderModel.folders[2]
        assertEquals("Hello", expectedItem.title)
        assertEquals(dateTimeCreator(), expectedItem.updated_on)
        folderModel.save()
    }

    @Test
    fun changeTitleBad() = runTest {
        val folderModel = createFolderModel()
        try {
            folderModel.changeTitle(3, "2") // "2" title already used
            assert(false)
        } catch (e: IllegalArgumentException) {
            assert(true)
        }
        folderModel.save()
    }

    @Test
    fun changeDescriptionGood() = runTest {
        val folderModel = createFolderModel()
        folderModel.changeDescription(1, "Change description")
        val expectedItem = folderModel.folders[0]
        assertEquals("Change description", expectedItem.description)
        assertEquals(dateTimeCreator(), expectedItem.updated_on)
        folderModel.save()
    }

    @Test
    fun changeDescriptionGoodNull() = runTest {
        val folderModel = createFolderModel()
        folderModel.changeDescription(1, null)
        val expectedItem = folderModel.folders[0]
        assertEquals(null, expectedItem.description)
        assertEquals(dateTimeCreator(), expectedItem.updated_on)
        folderModel.save()
    }

}