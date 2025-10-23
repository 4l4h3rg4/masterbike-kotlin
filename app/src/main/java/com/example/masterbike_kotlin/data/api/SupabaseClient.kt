package com.example.masterbike_kotlin.data.api

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.storage.Storage

object SupabaseClient {
    private const val SUPABASE_URL = "https://ztdbxrelxyrcidfwferi.supabase.co"
    private const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Inp0ZGJ4cmVseHlyY2lkZndmZXJpIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjEyNTY3NTUsImV4cCI6MjA3NjgzMjc1NX0.fuDY5ifSIzgbNbI8cBwxHF_zuO67zfOAuOp_iWL_PCc"

    val client: SupabaseClient = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_ANON_KEY
    ) {
        install(Postgrest)
        install(Auth)
        install(Storage)
    }
}