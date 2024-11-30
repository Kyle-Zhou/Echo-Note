package com.example.echonote.ui.models

import com.example.echonote.data.models.FolderModel
import com.example.echonote.data.persistence.MockPersistenceFolder
import com.example.echonote.dateTimeCreator
import com.example.echonote.utils.EmptyArgumentEchoNoteException
import com.example.echonote.utils.IllegalArgumentEchoNoteException
import com.example.echonote.utils.IllegalStateEchoNoteException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class ViewFolderModelTest {
    private suspend fun createFolderModel(): FolderModel {
        val mockPersistence = MockPersistenceFolder(::dateTimeCreator)
        val folderModel = FolderModel(mockPersistence, ::dateTimeCreator)
        folderModel.init()
        return folderModel
    }

    @Test
    fun addGood() = runTest {
        val folderModel = createFolderModel()
        val viewFolderModel = ViewFolderModel(folderModel)
        assertEquals(3, viewFolderModel.folders.size)
        folderModel.add("Add", null)
        assertEquals(4, viewFolderModel.folders.size)
    }

    @Test
    fun addSameTitle() = runTest {
        val folderModel = createFolderModel()
        val viewFolderModel = ViewFolderModel(folderModel)
        assertEquals(3, viewFolderModel.folders.size)
        try {
            folderModel.add("1", "test")
            assert(false)
        } catch (_: IllegalArgumentEchoNoteException) {
            assertEquals(3, viewFolderModel.folders.size)
        }
    }

    @Test
    fun addBadEmpty() = runTest {
        val folderModel = createFolderModel()
        val viewFolderModel = ViewFolderModel(folderModel)
        assertEquals(3, viewFolderModel.folders.size)
        try {
            folderModel.add("", "test")
            assert(false)
        } catch (_: EmptyArgumentEchoNoteException) {
            assertEquals(3, viewFolderModel.folders.size)
        }
    }

    @Test
    fun delGood() = runTest {
        val folderModel = createFolderModel()
        val viewFolderModel = ViewFolderModel(folderModel)
        assertEquals(3, viewFolderModel.folders.size)
        folderModel.del(3)
        assertEquals(2, viewFolderModel.folders.size)
    }

    @Test
    fun delBadAll() = runTest {
        val folderModel = createFolderModel()
        val viewFolderModel = ViewFolderModel(folderModel)
        assertEquals(3, viewFolderModel.folders.size)
        folderModel.del(3)
        assertEquals(2, viewFolderModel.folders.size)
        folderModel.del(1)
        assertEquals(1, viewFolderModel.folders.size)
        try {
            folderModel.del(2)
            assert(false)
        } catch (_: IllegalStateEchoNoteException) {
            assertEquals(1, viewFolderModel.folders.size)
        }
    }

    @Test
    fun delNone() = runTest {
        val folderModel = createFolderModel()
        val viewFolderModel = ViewFolderModel(folderModel)
        assertEquals(3, viewFolderModel.folders.size)
        // This folder hasn't been added before
        folderModel.del(10)
        assertEquals(3, viewFolderModel.folders.size)
    }

    @Test
    fun changeTitleGood() = runTest {
        val folderModel = createFolderModel()
        val viewFolderModel = ViewFolderModel(folderModel)
        assertEquals(3, viewFolderModel.folders.size)
        folderModel.changeTitle(3, "Hello")
        assertEquals(3, viewFolderModel.folders.size)
        val expectedItem = viewFolderModel.folders.find { it.id == 3.toLong() }!!
        assertEquals("Hello", expectedItem.title)
        assertEquals(dateTimeCreator(), expectedItem.updated_on)
    }

    @Test
    fun changeTitleBad() = runTest {
        val folderModel = createFolderModel()
        val viewFolderModel = ViewFolderModel(folderModel)
        try {
            folderModel.changeTitle(3, "2") // "2" title already used
            assert(false)
        } catch (_: IllegalArgumentEchoNoteException) {
            assertEquals(3, viewFolderModel.folders.size)
        }
    }

    @Test
    fun changeTitleBadEarly() = runTest {
        val folderModel = createFolderModel()
        val viewFolderModel = ViewFolderModel(folderModel)
        try {
            folderModel.changeTitle(1, "2") // "2" title already used
            assert(false)
        } catch (_: IllegalArgumentEchoNoteException) {
            assertEquals(3, viewFolderModel.folders.size)
        }
    }

    @Test
    fun changeTitleBadEmpty() = runTest {
        val folderModel = createFolderModel()
        val viewFolderModel = ViewFolderModel(folderModel)
        try {
            folderModel.changeTitle(1, "")
            assert(false)
        } catch (_: EmptyArgumentEchoNoteException) {
            assertEquals(3, viewFolderModel.folders.size)
        }
    }

    @Test
    fun changeDescriptionGood() = runTest {
        val folderModel = createFolderModel()
        val viewFolderModel = ViewFolderModel(folderModel)
        assertEquals(3, viewFolderModel.folders.size)
        folderModel.changeDescription(1, "Change description")
        assertEquals(3, viewFolderModel.folders.size)
        val expectedItem = viewFolderModel.folders.find { it.id == 1L }!!
        assertEquals("Change description", expectedItem.description)
        assertEquals(dateTimeCreator(), expectedItem.updated_on)
    }

    @Test
    fun changeDescriptionGoodNull() = runTest {
        val folderModel = createFolderModel()
        val viewFolderModel = ViewFolderModel(folderModel)
        assertEquals(3, viewFolderModel.folders.size)
        folderModel.changeDescription(1, null)
        assertEquals(3, viewFolderModel.folders.size)
        val expectedItem = viewFolderModel.folders.find { it.id == 1L }!!
        assertEquals(null, expectedItem.description)
        assertEquals(dateTimeCreator(), expectedItem.updated_on)
    }
}