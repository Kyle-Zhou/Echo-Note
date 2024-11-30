package com.example.echonote.data.entities
import com.example.echonote.dateTimeCreator
import com.example.echonote.dateTimeCreator2
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import org.junit.Assert.assertEquals
import org.junit.Test

class ItemTest {
    @Test
    fun create() {
        val currentTime = dateTimeCreator()
        val item = Item(id=1, folder_id = 1, title = "Title", summary = Json.decodeFromString("""{}"""), created_on = currentTime, updated_on = currentTime)
        assertEquals(1, item.id)
        assertEquals(1, item.folder_id)
        assertEquals("Title", item.title)
        assertEquals(Json.decodeFromString<JsonElement>("""{}"""), item.summary)
        assertEquals(currentTime, item.created_on)
        assertEquals(currentTime, item.updated_on)
    }

    @Test
    fun modify() {
        val currentTime = dateTimeCreator()
        val item = Item(id=1, folder_id = 1, title = "Title", summary = Json.decodeFromString("""{}"""), created_on = currentTime, updated_on = currentTime)
        assertEquals(1, item.id)
        assertEquals(1, item.folder_id)
        assertEquals("Title", item.title)
        assertEquals(Json.decodeFromString<JsonElement>("""{}"""), item.summary)
        assertEquals(currentTime, item.created_on)
        assertEquals(currentTime, item.updated_on)
        item.title = "New title"
        item.folder_id = 2
        item.summary = Json.decodeFromString<JsonElement>("""{"summary": "summary"}""")
        val newCurrentTime = dateTimeCreator2()
        item.updated_on = newCurrentTime
        assertEquals(1, item.id)
        assertEquals(2, item.folder_id)
        assertEquals("New title", item.title)
        assertEquals(Json.decodeFromString<JsonElement>("""{"summary": "summary"}"""), item.summary)
        assertEquals(currentTime, item.created_on)
        assertEquals(newCurrentTime, item.updated_on)
    }
}