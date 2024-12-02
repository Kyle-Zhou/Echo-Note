package com.example.echonote.data.models
import com.example.echonote.data.persistence.MockPersistenceFolder
import com.example.echonote.dateTimeCreator
import com.example.echonote.utils.DESCRIPTION_LIMIT
import com.example.echonote.utils.EmptyArgumentEchoNoteException
import com.example.echonote.utils.FOLDER_TITLE_LIMIT
import com.example.echonote.utils.IllegalArgumentEchoNoteException
import com.example.echonote.utils.IllegalStateEchoNoteException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class FolderModelTest {
    private suspend fun createFolderModel(): FolderModel {
        val mockPersistence = MockPersistenceFolder(::dateTimeCreator)
        val folderModel = FolderModel(mockPersistence, ::dateTimeCreator)
        folderModel.init()
        return folderModel
    }

    @Test
    fun addGood() = runTest {
        val folderModel = createFolderModel()
        assertEquals(3, folderModel.folders.size)
        assertEquals(0, folderModel.folders.count { it.title == "Add" })

        folderModel.add("Add", null)
        assertEquals(4, folderModel.folders.size)
        assertEquals(1, folderModel.folders.count { it.title == "Add" })
    }

    @Test
    fun addSameTitle() = runTest {
        val folderModel = createFolderModel()
        assertEquals(3, folderModel.folders.size)
        assertEquals(1, folderModel.folders.count { it.title == "1" })

        try {
            folderModel.add("1", "test")
            assert(false)
        } catch (_: IllegalArgumentEchoNoteException) {
            assertEquals(3, folderModel.folders.size)
            assertEquals(1, folderModel.folders.count { it.title == "1" })
        }
    }

    @Test
    fun addBadEmpty() = runTest {
        val folderModel = createFolderModel()
        assertEquals(3, folderModel.folders.size)

        try {
            folderModel.add("", "test")
            assert(false)
        } catch (_: EmptyArgumentEchoNoteException) {
            assertEquals(3, folderModel.folders.size)
        }
    }

    @Test
    fun addTitleTooLong() = runTest {
        val folderModel = createFolderModel()
        assertEquals(3, folderModel.folders.size)
        val char = "x"
        val title = char.repeat(FOLDER_TITLE_LIMIT)

        try {
            folderModel.add(title, "test")
            assert(false)
        } catch (_: IllegalArgumentEchoNoteException) {
            assertEquals(3, folderModel.folders.size)
        }
    }

    @Test
    fun addDescriptionTooLong() = runTest {
        val folderModel = createFolderModel()
        assertEquals(3, folderModel.folders.size)
        val char = "x"
        val description = char.repeat(DESCRIPTION_LIMIT)

        try {
            folderModel.add("test", description)
            assert(false)
        } catch (_: IllegalArgumentEchoNoteException) {
            assertEquals(3, folderModel.folders.size)
        }
    }

    @Test
    fun addTitleAndDescriptionTooLong() = runTest {
        val folderModel = createFolderModel()
        assertEquals(3, folderModel.folders.size)
        val char = "x"
        val title = char.repeat(FOLDER_TITLE_LIMIT)
        val description = char.repeat(DESCRIPTION_LIMIT)

        try {
            folderModel.add(title, description)
            assert(false)
        } catch (_: IllegalArgumentEchoNoteException) {
            assertEquals(3, folderModel.folders.size)
        }
    }

    @Test
    fun delGood() = runTest {
        val folderModel = createFolderModel()
        assertEquals(3, folderModel.folders.size)
        assertNotNull(folderModel.folders.find { it.id == 3L })
        folderModel.del(3)
        assertEquals(2, folderModel.folders.size)
        assertNull(folderModel.folders.find { it.id == 3L })
    }

    @Test
    fun delBadAll() = runTest {
        val folderModel = createFolderModel()
        folderModel.del(3)
        assertEquals(2, folderModel.folders.size)
        assertEquals(0, folderModel.folders.count{it.id == 3L})

        folderModel.del(1)
        assertEquals(1, folderModel.folders.size)
        assertEquals(0, folderModel.folders.count{it.id == 1L})

        try {
            folderModel.del(2)
            assert(false)
        } catch (_: IllegalStateEchoNoteException) {
            assertEquals(1, folderModel.folders.size)
            assertEquals(1, folderModel.folders.count{it.id == 2L})
        }
    }

    @Test
    fun delNone() = runTest {
        val folderModel = createFolderModel()
        assertEquals(0, folderModel.folders.count { it.id == 10L })
        assertEquals(3, folderModel.folders.size)
 // This folder hasn't been added before
        folderModel.del(10)
        assertEquals(3, folderModel.folders.size)
        assertEquals(0, folderModel.folders.count { it.id == 10L })
    }

    @Test
    fun changeTitleGood() = runTest {
        val folderModel = createFolderModel()
        assertEquals(3, folderModel.folders.size)
        assertNotNull(folderModel.folders.find { it.id == 3L })
        folderModel.changeTitle(3, "Hello")

        assertEquals(1, folderModel.folders.count { it.title == "Hello" })
        val expectedItem = folderModel.folders.find { it.id == 3L }!!
        assertEquals("Hello", expectedItem.title)
        assertEquals(dateTimeCreator(), expectedItem.updated_on)
        assertEquals(3, folderModel.folders.size)
    }

    @Test
    fun changeTitleBad() = runTest {
        val folderModel = createFolderModel()
        assertEquals(3, folderModel.folders.size)
        try {
            folderModel.changeTitle(3, "2") // "2" title already used
            assert(false)
        } catch (_: IllegalArgumentEchoNoteException) {
            assertEquals(1, folderModel.folders.count { it.title == "2" })
            assertEquals(3, folderModel.folders.size)
        }
    }

    @Test
    fun changeTitleBadEarly() = runTest {
        val folderModel = createFolderModel()
        assertEquals(3, folderModel.folders.size)
        try {
            folderModel.changeTitle(1, "2") // "2" title already used
            assert(false)
        } catch (_: IllegalArgumentEchoNoteException) {
            assertEquals(1, folderModel.folders.count { it.title == "2" })
            assertEquals(3, folderModel.folders.size)
        }
    }

    @Test
    fun changeTitleBadEmpty() = runTest {
        val folderModel = createFolderModel()
        assertEquals(3, folderModel.folders.size)
        try {
            folderModel.changeTitle(1, "")
            assert(false)
        } catch (_: EmptyArgumentEchoNoteException) {
            assertNotEquals("", folderModel.folders.find { it.id == 1L}!!.title)
            assertEquals(3, folderModel.folders.size)
        }
    }

    @Test
    fun changeTitleTooLong() = runTest {
        val folderModel = createFolderModel()
        assertEquals(3, folderModel.folders.size)
        val char = "x"
        val title = char.repeat(FOLDER_TITLE_LIMIT)
        try {
            folderModel.changeTitle(1, title)
            assert(false)
        } catch (_: IllegalArgumentEchoNoteException) {
            assertNotEquals(title, folderModel.folders.find { it.id == 1L}!!.title)
            assertEquals(3, folderModel.folders.size)
        }
    }

    @Test
    fun changeDescriptionGood() = runTest {
        val folderModel = createFolderModel()
        assertEquals(3, folderModel.folders.size)
        folderModel.changeDescription(1, "Change description")
        assertEquals(3, folderModel.folders.size)
        val expectedItem = folderModel.folders.find { it.id == 1L }!!
        assertEquals("Change description", expectedItem.description)
        assertEquals(dateTimeCreator(), expectedItem.updated_on)
    }

    @Test
    fun changeDescriptionGoodNull() = runTest {
        val folderModel = createFolderModel()
        assertEquals(3, folderModel.folders.size)
        folderModel.changeDescription(1, null)
        assertEquals(3, folderModel.folders.size)
        val expectedItem = folderModel.folders.find { it.id == 1L }!!
        assertEquals(null, expectedItem.description)
        assertEquals(dateTimeCreator(), expectedItem.updated_on)
    }

    @Test
    fun changeDescriptionTooLong() = runTest {
        val folderModel = createFolderModel()
        assertEquals(3, folderModel.folders.size)
        val char = "x"
        val description = char.repeat(DESCRIPTION_LIMIT)
        try {
            folderModel.changeDescription(1, description)
            assert(false)
        } catch (_: IllegalArgumentEchoNoteException) {
            assertNotEquals(description, folderModel.folders.find { it.id == 1L}!!.title)
            assertEquals(3, folderModel.folders.size)
        }
    }
}