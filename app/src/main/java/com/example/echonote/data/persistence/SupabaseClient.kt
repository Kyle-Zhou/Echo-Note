package com.example.echonote.data.persistence

import com.example.echonote.data.entities.Folder
import com.example.echonote.data.entities.User
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
        install(Postgrest)
    }

    override fun loadUsers(): List<User> {
        var list = listOf<User>()
        runBlocking {
            withContext(Dispatchers.IO) {
                list = supabase.from("Users")
                    .select().decodeList<User>()
            }
        }
        return list
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
}
