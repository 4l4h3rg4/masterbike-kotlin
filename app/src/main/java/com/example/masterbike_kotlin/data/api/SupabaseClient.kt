package com.example.masterbike_kotlin.data.api

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.storage.Storage

object SupabaseClient {
    // TODO: Replace with your actual Supabase URL and anon key
    private const val SUPABASE_URL = "https://your-project-ref.supabase.co"
    private const val SUPABASE_ANON_KEY = "your-anon-key"

    val client: SupabaseClient = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_ANON_KEY
    ) {
        install(Postgrest)
        install(Auth)
        install(Storage)
    }
}