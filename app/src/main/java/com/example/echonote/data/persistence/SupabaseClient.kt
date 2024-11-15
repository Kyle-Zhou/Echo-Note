package com.example.echonote.data.persistence

import com.example.echonote.data.entities.Folder
import com.example.echonote.data.entities.User
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.minimalSettings
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

object SupabaseClient: IPersistence {
    private val supabase = createSupabaseClient(
        // public project url
        supabaseUrl = "https://sxrwvweeprivkwsgimun.supabase.co",
        // public anon key
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InN4cnd2d2VlcHJpdmt3c2dpbXVuIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MjgwNzAyNzYsImV4cCI6MjA0MzY0NjI3Nn0.NNlLAQGMKQaOppISuPxkYsNzVv9thgaYLH2wwR65RE8"
    ) {
        install(Auth) {
            minimalSettings() // disables session saving and auto-refreshing
        }
        install(Postgrest)
    }

    override suspend fun loadUsers(): List<User> = withContext(Dispatchers.IO) {
        var list = listOf<User>()
        try {
            list = supabase.from("Users").select().decodeList<User>()
        } catch (e: Exception) {
            println("Error fetching users: ${e.localizedMessage}")
        }
        return@withContext list
    }

    override fun loadFolders(): List<Folder> {
        var list = listOf<Folder>()
        runBlocking {
            withContext(Dispatchers.IO) {
            list = supabase.from("Folders").select().decodeList<Folder>()
            }
        }
        return list
    }

    override fun saveFolders(folders: List<Folder>) {
        TODO("Not yet implemented")
    }

    override suspend fun signupUser(userEmail: String, userPassword: String) {
         supabase.auth.signUpWith(Email) {
            email = userEmail
            password = userPassword
        }
        getCurrentSession()
    }

    override suspend fun loginUser(userEmail: String, userPassword: String) {
        supabase.auth.signInWith(Email) {
            email = userEmail
            password = userPassword
        }
        getCurrentSession()
    }

    // Log the current session
    fun getCurrentSession() {
        val currentSession = supabase.auth.currentSessionOrNull()
        println("Current Session: ${currentSession}")
        println("Current Session Access Token: ${currentSession?.accessToken}")
    }

}
