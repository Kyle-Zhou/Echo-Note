package com.example.echonote.data.controller
import com.example.echonote.data.models.FolderModel
import com.example.echonote.data.persistence.MockPersistenceFolder
import com.example.echonote.data.persistence.MockPersistenceItem
import com.example.echonote.dateTimeCreator
import com.example.echonote.utils.DESCRIPTION_LIMIT
import com.example.echonote.utils.EmptyArgumentEchoNoteException
import com.example.echonote.utils.FOLDER_TITLE_LIMIT
import com.example.echonote.utils.IllegalArgumentEchoNoteException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class FolderControllerTest {
    private suspend fun createFolderModel(): FolderModel {
        val mockPersistence = MockPersistenceFolder(::dateTimeCreator)
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
    fun addTitleTooLong() = runTest {
        val folderModel = createFolderModel()
        val folderController = FolderController(folderModel)
        assertEquals(3, folderModel.folders.size)
        val char = "x"
        val title = char.repeat(FOLDER_TITLE_LIMIT)

        try {
            folderController.invoke(FolderControllerEvent.ADD, title=title, description = "test")
            assert(false)
        } catch (_: IllegalArgumentEchoNoteException) {
            assertEquals(3, folderModel.folders.size)
        }
    }

    @Test
    fun addDescriptionTooLong() = runTest {
        val folderModel = createFolderModel()
        val folderController = FolderController(folderModel)
        assertEquals(3, folderModel.folders.size)
        val char = "x"
        val description = char.repeat(DESCRIPTION_LIMIT)

        try {
            folderController.invoke(FolderControllerEvent.ADD, title="test", description = description)
            assert(false)
        } catch (_: IllegalArgumentEchoNoteException) {
            assertEquals(3, folderModel.folders.size)
        }
    }

    @Test
    fun addTitleAndDescriptionTooLong() = runTest {
        val folderModel = createFolderModel()
        val folderController = FolderController(folderModel)
        assertEquals(3, folderModel.folders.size)
        val char = "x"
        val title = char.repeat(FOLDER_TITLE_LIMIT)
        val description = char.repeat(DESCRIPTION_LIMIT)

        try {
            folderController.invoke(FolderControllerEvent.ADD, title=title, description = description)
            assert(false)
        } catch (_: IllegalArgumentEchoNoteException) {
            assertEquals(3, folderModel.folders.size)
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
    }

    @Test
    fun changeTitleGood() = runTest {
        val folderModel = createFolderModel()
        val folderController = FolderController(folderModel)
        folderController.invoke(FolderControllerEvent.RENAME, 3, "Hello")
        val expectedItem = folderModel.folders[2]
        assertEquals("Hello", expectedItem.title)
        assertEquals(dateTimeCreator(), expectedItem.updated_on)
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
    }

    @Test
    fun changeTitleBadEmpty() = runTest {
        val folderModel = createFolderModel()
        val folderController = FolderController(folderModel)
        try {
            folderController.invoke(FolderControllerEvent.RENAME, 3, "")
            assert(false)
        } catch (_: EmptyArgumentEchoNoteException) {
            assert(true)
        }
    }

    @Test
    fun changeTitleTooLong() = runTest {
        val folderModel = createFolderModel()
        assertEquals(3, folderModel.folders.size)
        val folderController = FolderController(folderModel)
        val char = "x"
        val title = char.repeat(FOLDER_TITLE_LIMIT)
        try {
            folderController.invoke(FolderControllerEvent.RENAME, 1, title)
            assert(false)
        } catch (_: IllegalArgumentEchoNoteException) {
            assertNotEquals(title, folderModel.folders.find { it.id == 1L}!!.title)
            assertEquals(3, folderModel.folders.size)
        }
    }

    @Test
    fun changeDescriptionGood() = runTest {
        val folderModel = createFolderModel()
        val folderController = FolderController(folderModel)
        folderController.invoke(FolderControllerEvent.CHANGE_DESC, 1, description = "Change description")
        val expectedItem = folderModel.folders[0]
        assertEquals("Change description", expectedItem.description)
        assertEquals(dateTimeCreator(), expectedItem.updated_on)
    }

    @Test
    fun changeDescriptionGoodNull() = runTest {
        val folderModel = createFolderModel()
        val folderController = FolderController(folderModel)
        folderController.invoke(FolderControllerEvent.CHANGE_DESC, 1, description = null)
        val expectedItem = folderModel.folders[0]
        assertEquals(null, expectedItem.description)
        assertEquals(dateTimeCreator(), expectedItem.updated_on)
    }

    @Test
    fun changeDescriptionTooLong() = runTest {
        val folderModel = createFolderModel()
        assertEquals(3, folderModel.folders.size)
        val folderController = FolderController(folderModel)
        val char = "x"
        val description = char.repeat(DESCRIPTION_LIMIT)
        try {
            folderController.invoke(FolderControllerEvent.CHANGE_DESC, 1, description=description)
            assert(false)
        } catch (_: IllegalArgumentEchoNoteException) {
            assertNotEquals(description, folderModel.folders.find { it.id == 1L}!!.title)
            assertEquals(3, folderModel.folders.size)
        }
    }

}