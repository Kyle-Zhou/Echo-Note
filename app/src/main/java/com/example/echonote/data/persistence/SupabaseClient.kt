package com.example.echonote.data.persistence

import com.example.echonote.data.entities.Folder
import com.example.echonote.data.entities.Item
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.minimalSettings
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.postgrest.query.PostgrestQueryBuilder
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.time.Duration.Companion.minutes
import java.util.UUID


object SupabaseClient: IPersistenceItem, IPersistenceFolder, IAuth {
    @Serializable
    private data class TempItem (
        var folder_id: Long,
        var title: String,
        var summary: JsonElement,
        var created_on: LocalDateTime,
        var updated_on: LocalDateTime,
    )

    @Serializable
    private data class TempFolder (
        val user_id: String,
        var title: String,
        var description: String?,
        var created_on: LocalDateTime,
        var updated_on: LocalDateTime,
    )

    private val supabase = createSupabaseClient(
        // public project url
        supabaseUrl = "https://sxrwvweeprivkwsgimun.supabase.co",
        // public anon key
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InN4cnd2d2VlcHJpdmt3c2dpbXVuIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MjgwNzAyNzYsImV4cCI6MjA0MzY0NjI3Nn0.NNlLAQGMKQaOppISuPxkYsNzVv9thgaYLH2wwR65RE8"
    ) {
        install(Auth) {
//            minimalSettings() // disables session saving and auto-refreshing
        }
        install(Postgrest)
        install(Storage)
    }

    override suspend fun createFolder(
        title: String,
        description: String?,
        created_on: LocalDateTime,
        update_on: LocalDateTime
    ): Folder {
        val tempFolder = TempFolder(getCurrentUserID(), title, description, created_on, update_on)
        val folder = getFoldersTable().insert(tempFolder) {
            select()
        }.decodeSingle<Folder>()
        return folder
    }

    override suspend fun loadFolders(): List<Folder> {
        return getFoldersTable().select{
            filter { eq("user_id", getCurrentUserID()) }
            order("id", order = Order.ASCENDING)
        }.decodeList<Folder>()
    }

    override suspend fun saveFolder(folder: Folder) {
        getFoldersTable().update({
            set("title", folder.title)
            set("description", folder.description)
            set("updated_on", folder.updated_on)
        }) {
            filter { eq("id", folder.id) }
        }
    }

    override suspend fun deleteFolder(id: Long) {
        getFoldersTable().delete {
            filter { eq("id", id) }
        }
    }

    override suspend fun createItem(
        folder_id: Long,
        title: String,
        summary: JsonElement,
        created_on: LocalDateTime,
        update_on: LocalDateTime
    ): Item {
        val tempItem = TempItem(folder_id, title, summary, created_on, update_on)
        val item = getItemsTable().insert(tempItem) {
            select()
        }.decodeSingle<Item>()
        return item
    }

    override suspend fun loadItems(folderId: Long): List<Item> {
        return getItemsTable().select{
            filter { eq("folder_id", folderId) }
            order("id", order = Order.ASCENDING)
        }.decodeList<Item>()
    }

    override suspend fun saveItem(item: Item) {
        getItemsTable().update({
            set("folder_id", item.folder_id)
            set("title", item.title)
            set("summary", item.summary)
            set("updated_on", item.updated_on)
        }) {
            filter { eq("id", item.id) }
        }
    }

    override suspend fun deleteItem(id: Long) {
        getItemsTable().delete{
            filter { eq("id", id) }
        }
    }

    private fun getFoldersTable(): PostgrestQueryBuilder {
        return supabase.from("Folders")
    }

    private fun getItemsTable(): PostgrestQueryBuilder {
        return supabase.from("Items")
    }

    override suspend fun signupUser(userEmail: String, userPassword: String, userName: String) {
         supabase.auth.signUpWith(Email) {
            email = userEmail
            password = userPassword
             data = buildJsonObject {
                 put("first_name", JsonPrimitive(userName))
             }
         }
        logCurrentSession()
    }

    override suspend fun loginUser(userEmail: String, userPassword: String) {
        supabase.auth.signInWith(Email) {
            email = userEmail
            password = userPassword
        }
        logCurrentSession()
    }

    override suspend fun logoutUser() {
        supabase.auth.signOut()
        logCurrentSession()
    }

    suspend fun uploadAudioFileAndGetUrl(filePath: String, fileData: ByteArray) : String {
        val bucket = supabase.storage.from("audio-storage")
        bucket.upload(filePath, fileData) {
            upsert = false
        }
        val url = bucket.createSignedUrl(path = filePath, expiresIn = 60.minutes)
        return url
    }

    fun logCurrentSession() {
        val currentSession = supabase.auth.currentSessionOrNull()
        println("Current Session: $currentSession")
        println("Current Session Access Token: ${currentSession?.accessToken}")
    }

    override fun getCurrentUserID() : String {
        val currentSession = supabase.auth.currentSessionOrNull()
        val uuid = currentSession?.user?.id ?: "unknown UUID"
        println("UUID: $uuid")
        return uuid
    }

    override fun getName() : String {
        val currentSession = supabase.auth.currentSessionOrNull()
        val name = currentSession?.user?.userMetadata?.get("first_name")?.jsonPrimitive?.content ?: "unknown"
        println("Name: $name")
        return name
    }

    private fun getProfilesTable(): PostgrestQueryBuilder {
        return supabase.from("profiles")
    }

    override suspend fun editUserName(newUserName: String) {
        // update display name field in auth table
        supabase.auth.updateUser {
            data {
                put("first_name", JsonPrimitive(newUserName)) // vs. name?
            }
        }
        // update profiles table in postgres
        val uuid = UUID.fromString(getCurrentUserID())
        getProfilesTable().update({
            set("first_name", newUserName)
        }) {
            filter { eq("id", uuid) }
        }
    }


}
