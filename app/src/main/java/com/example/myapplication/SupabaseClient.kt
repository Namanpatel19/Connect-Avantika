package com.example.myapplication

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

object SupabaseClient {
    private const val SUPABASE_URL = "https://hnplrokuxkeioteisoot.supabase.co"
    private const val SUPABASE_KEY = "sb_publishable_kwI47Xt0UpmONl39OBUaPg_Mj_OMy3I"

    val client = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_KEY
    ) {
        install(Auth)
        install(Postgrest)
        install(Storage)
    }
}