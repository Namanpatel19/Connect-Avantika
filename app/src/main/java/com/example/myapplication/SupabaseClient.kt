package com.example.myapplication

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

object SupabaseClient {
    private const val SUPABASE_URL = "https://xgvsasaisnapzyzglgix.supabase.co"
    private const val SUPABASE_KEY = "sb_publishable_TU1Uki-YjFRNcwA-vcjigg_8spKqSUO"
    
    // IMPORTANT: In a production app, the service role key should NEVER be stored in the client.
    // Replace with your actual service_role key for admin functionality.
    private const val SERVICE_ROLE_KEY = "YOUR_SERVICE_ROLE_KEY"

    val client = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_KEY
    ) {
        install(Auth)
        install(Postgrest)
        install(Storage)
    }

    val adminClient = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SERVICE_ROLE_KEY
    ) {
        install(Auth)
    }
}
