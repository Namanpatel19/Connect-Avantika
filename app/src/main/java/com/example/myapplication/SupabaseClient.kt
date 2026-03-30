package com.example.myapplication

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

object SupabaseClient {
    private const val SUPABASE_URL = "https://xgvsasaisnapzyzglgix.supabase.co"
    private const val SUPABASE_KEY = "sb_publishable_TU1Uki-YjFRNcwA-vcjigg_8spKqSUO"
    
    // NOTE: This is the service_role key. In a real app, keep this on a secure server!
    private const val SERVICE_ROLE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InhndnNhc2Fpc25hcHp5emdsZ2l4Iiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTczODEyMjIzNCwiZXhwIjoyMDUzNjk4MjM0fQ.U_H0-iZ_v_h7L-m-f_q-X_v_h7L-m-f_q-X_v_h7L-m"

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
        install(Postgrest)
    }
}
