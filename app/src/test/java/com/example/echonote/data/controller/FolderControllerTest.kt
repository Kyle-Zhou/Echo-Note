package com.example.echonote.data.controller
import com.example.echonote.data.models.FolderModel
import com.example.echonote.data.persistence.MockPersistence
import com.example.echonote.dateTimeCreator
import com.example.echonote.utils.IllegalArgumentEchoNoteException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class FolderControllerTest {
    private suspend fun createFolderModel(): FolderModel {
        val mockPersistence = MockPersistence(::dateTimeCreator)
        val folderModel = FolderModel(mockPersistence, ::dateTimeCreator)
        folderModel.init()
        return folderModel
    }

    @Test
    fun addGood() = runTest {
        val folderModel = createFolderModel()
        val folderController = FolderController(folderModel)
        folderController.invoke(FolderControllerEvent.ADD, title = "Add")
        assertEquals(4, folderModel.folders.size)
        folderController.invoke(FolderControllerEvent.SAVE)
    }

    @Test
    fun addSameTitle() = runTest {
        val folderModel = createFolderModel()
        val folderController = FolderController(folderModel)
        try {
            folderController.invoke(FolderControllerEvent.ADD, title =  "1", description = "test")
            assert(false)
        } catch (_: IllegalArgumentEchoNoteException) {
            assert(true)
        }
    }

    @Test
    fun delGood() = runTest {
        val folderModel = createFolderModel()
        val folderController = FolderController(folderModel)
        folderController.invoke(FolderControllerEvent.DEL, 3)
        assertEquals(2, folderModel.folders.size)
    }

    @Test
    fun delNone() = runTest {
        val folderModel = createFolderModel()
        val folderController = FolderController(folderModel)
        // This folder hasn't been added before
        folderController.invoke(FolderControllerEvent.DEL, 10)
        assertEquals(3, folderModel.folders.size)
        folderController.invoke(FolderControllerEvent.SAVE)
    }

    @Test
    fun changeTitleGood() = runTest {
        val folderModel = createFolderModel()
        val folderController = FolderController(folderModel)
        folderController.invoke(FolderControllerEvent.RENAME, 3, "Hello")
        val expectedItem = folderModel.folders[2]
        assertEquals("Hello", expectedItem.title)
        assertEquals(dateTimeCreator(), expectedItem.updated_on)
        folderController.invoke(FolderControllerEvent.SAVE)
    }

    @Test
    fun changeTitleBad() = runTest {
        val folderModel = createFolderModel()
        val folderController = FolderController(folderModel)
        try {
            folderController.invoke(FolderControllerEvent.RENAME, 3, "2") // "2" title already used
            assert(false)
        } catch (_: IllegalArgumentEchoNoteException) {
            assert(true)
        }
        folderController.invoke(FolderControllerEvent.SAVE)
    }

    @Test
    fun changeTitleBadEmpty() = runTest {
        val folderModel = createFolderModel()
        val folderController = FolderController(folderModel)
        try {
            folderController.invoke(FolderControllerEvent.RENAME, 3, "")
            assert(false)
        } catch (_: IllegalArgumentEchoNoteException) {
            assert(true)
        }
        folderController.invoke(FolderControllerEvent.SAVE)
    }

    @Test
    fun changeDescriptionGood() = runTest {
        val folderModel = createFolderModel()
        val folderController = FolderController(folderModel)
        folderController.invoke(FolderControllerEvent.CHANGE_DESC, 1, description = "Change description")
        val expectedItem = folderModel.folders[0]
        assertEquals("Change description", expectedItem.description)
        assertEquals(dateTimeCreator(), expectedItem.updated_on)
        folderController.invoke(FolderControllerEvent.SAVE)
    }

    @Test
    fun changeDescriptionGoodNull() = runTest {
        val folderModel = createFolderModel()
        val folderController = FolderController(folderModel)
        folderController.invoke(FolderControllerEvent.CHANGE_DESC, 1, description = null)
        val expectedItem = folderModel.folders[0]
        assertEquals(null, expectedItem.description)
        assertEquals(dateTimeCreator(), expectedItem.updated_on)
        folderController.invoke(FolderControllerEvent.SAVE)
    }

}