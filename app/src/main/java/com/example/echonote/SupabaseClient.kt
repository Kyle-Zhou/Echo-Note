package com.example.echonote

import com.example.echonote.models.User
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object SupabaseClient {
    private val supabase = createSupabaseClient(
        // public project url
        supabaseUrl = "https://sxrwvweeprivkwsgimun.supabase.co",
        // public anon key
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InN4cnd2d2VlcHJpdmt3c2dpbXVuIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MjgwNzAyNzYsImV4cCI6MjA0MzY0NjI3Nn0.NNlLAQGMKQaOppISuPxkYsNzVv9thgaYLH2wwR65RE8"
    ) {
        install(Postgrest)
    }

    suspend fun getUsers(): List<User> {
        return withContext(Dispatchers.IO) {
            supabase.from("Users")
                .select().decodeList<User>()
        }
    }
}
