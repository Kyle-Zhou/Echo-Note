package com.example.echonote.data.entities
import com.example.echonote.dateTimeCreator
import com.example.echonote.dateTimeCreator2
import org.junit.Assert.assertEquals
import org.junit.Test

class FolderTest {
    @Test
    fun create1() {
        val currentTime = dateTimeCreator()
        val folder = Folder(id=1, user_id = "1", title = "Title", description = null, created_on = currentTime, updated_on = currentTime)
        assertEquals(1, folder.id)
        assertEquals("1", folder.user_id)
        assertEquals("Title", folder.title)
        assertEquals(null, folder.description)
        assertEquals(currentTime, folder.created_on)
        assertEquals(currentTime, folder.updated_on)
    }

    @Test
    fun create2() {
        val currentTime = dateTimeCreator()
        val folder = Folder(id=1, user_id = "1", title = "Title", description = "null", created_on = currentTime, updated_on = currentTime)
        assertEquals(1, folder.id)
        assertEquals("1", folder.user_id)
        assertEquals("Title", folder.title)
        assertEquals("null", folder.description)
        assertEquals(currentTime, folder.created_on)
        assertEquals(currentTime, folder.updated_on)
    }

    @Test
    fun modify() {
        val currentTime = dateTimeCreator()
        val folder = Folder(id=1, user_id = "1", title = "Title", description = "null", created_on = currentTime, updated_on = currentTime)
        assertEquals(1, folder.id)
        assertEquals("1", folder.user_id)
        assertEquals("Title", folder.title)
        assertEquals("null", folder.description)
        assertEquals(currentTime, folder.created_on)
        assertEquals(currentTime, folder.updated_on)
        folder.title = "New title"
        folder.description = "New description"
        val newCurrentTime = dateTimeCreator2()
        folder.updated_on = newCurrentTime
        assertEquals(1, folder.id)
        assertEquals("1", folder.user_id)
        assertEquals("New title", folder.title)
        assertEquals("New description", folder.description)
        assertEquals(currentTime, folder.created_on)
        assertEquals(newCurrentTime, folder.updated_on)
    }
}