package com.example.echonote.data.models
import com.example.echonote.data.persistence.MockPersistence
import com.example.echonote.data.entities.Folder
import com.example.echonote.dateTimeCreator
import com.example.echonote.dateTimeCreator2
import org.junit.Assert.assertEquals
import org.junit.Test

class FolderModelTest {
    @Test
    fun addGood() {
        val mockPersistence = MockPersistence(::dateTimeCreator)
        val folderModel = FolderModel(mockPersistence, ::dateTimeCreator)
        val newFolder = Folder(4, 1, "Add", null, dateTimeCreator2(), dateTimeCreator2())
        folderModel.add(newFolder)
        assertEquals(4, folderModel.folders.size)
        folderModel.save()
    }

    @Test
    fun addSameId() {
        val mockPersistence = MockPersistence(::dateTimeCreator)
        val folderModel = FolderModel(mockPersistence, ::dateTimeCreator)
        val newFolder = Folder(1, 1, "Add", null, dateTimeCreator2(), dateTimeCreator2())
        try {
            folderModel.add(newFolder)
            assert(false)
        } catch (e: IllegalArgumentException) {
            assert(true)
        }
        folderModel.save()
    }

    @Test
    fun addSameTitle() {
        val mockPersistence = MockPersistence(::dateTimeCreator)
        val folderModel = FolderModel(mockPersistence, ::dateTimeCreator)
        val newFolder = Folder(3, 1, "test 1", null, dateTimeCreator2(), dateTimeCreator2())
        try {
            folderModel.add(newFolder)
            assert(false)
        } catch (e: IllegalArgumentException) {
            assert(true)
        }
        folderModel.save()
    }

    @Test
    fun delGood() {
        val mockPersistence = MockPersistence(::dateTimeCreator)
        val folderModel = FolderModel(mockPersistence, ::dateTimeCreator)
        val localDateTime = dateTimeCreator()
        val folder = Folder(3, 1, "3", "test 3", localDateTime, localDateTime)
        folderModel.del(folder)
        assertEquals(2, folderModel.folders.size)
        folderModel.save()
    }

    @Test
    fun changeTitleGood() {
        val mockPersistence = MockPersistence(::dateTimeCreator)
        val folderModel = FolderModel(mockPersistence, ::dateTimeCreator)
        folderModel.changeTitle(3, "Hello")
        val expectedItem = folderModel.folders[2]
        assertEquals("Hello", expectedItem.title)
        assertEquals(dateTimeCreator(), expectedItem.updated_on)
        folderModel.save()
    }

    @Test
    fun changeTitleBad() {
        val mockPersistence = MockPersistence(::dateTimeCreator)
        val folderModel = FolderModel(mockPersistence, ::dateTimeCreator)
        try {
            folderModel.changeTitle(3, "2") // "2" title already used
            assert(false)
        } catch (e: IllegalArgumentException) {
            assert(true)
        }
        folderModel.save()
    }

    @Test
    fun changeDescriptionGood() {
        val mockPersistence = MockPersistence(::dateTimeCreator)
        val folderModel = FolderModel(mockPersistence, ::dateTimeCreator)
        folderModel.changeDescription(1, "Change description")
        val expectedItem = folderModel.folders[0]
        assertEquals("Change description", expectedItem.description)
        assertEquals(dateTimeCreator(), expectedItem.updated_on)
        folderModel.save()
    }

    @Test
    fun changeDescriptionGoodNull() {
        val mockPersistence = MockPersistence(::dateTimeCreator)
        val folderModel = FolderModel(mockPersistence, ::dateTimeCreator)
        folderModel.changeDescription(1, null)
        val expectedItem = folderModel.folders[0]
        assertEquals(null, expectedItem.description)
        assertEquals(dateTimeCreator(), expectedItem.updated_on)
        folderModel.save()
    }

}