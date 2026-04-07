package com.example.myapplication

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.user.UserSession
import io.github.jan.supabase.auth.SessionManager
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.serializer.KotlinXSerializer
import kotlinx.serialization.json.Json
import io.ktor.client.plugins.*
import io.github.jan.supabase.annotations.SupabaseInternal

object SupabaseClient {
    private const val SUPABASE_URL = "https://xgvsasaisnapzyzglgix.supabase.co"
    private const val SUPABASE_KEY = "sb_publishable_TU1Uki-YjFRNcwA-vcjigg_8spKqSUO"
    private const val SERVICE_ROLE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InhndnNhc2Fpc25hcHp5emdsZ2l4Iiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc3NDQxNjE4NiwiZXhwIjoyMDg5OTkyMTg2fQ._WxgafYq-ECa3nPfkOkM-ro06zdDX91WUUYCRGS5IGI"

    @OptIn(SupabaseInternal::class)
    val client = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_KEY
    ) {
        httpConfig {
            install(HttpTimeout) {
                requestTimeoutMillis = 90000
                connectTimeoutMillis = 90000
                socketTimeoutMillis = 90000
            }
        }
        install(Auth)
        install(Postgrest) {
            serializer = KotlinXSerializer(Json {
                ignoreUnknownKeys = true
                encodeDefaults = false
            })
        }
        install(Storage)
    }

    @OptIn(SupabaseInternal::class)
    val adminClient = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SERVICE_ROLE_KEY
    ) {
        httpConfig {
            install(HttpTimeout) {
                requestTimeoutMillis = 90000
                connectTimeoutMillis = 90000
                socketTimeoutMillis = 90000
            }
        }
        install(Auth) {
            sessionManager = object : SessionManager {
                override suspend fun saveSession(session: UserSession) {}
                override suspend fun loadSession(): UserSession? = null
                override suspend fun deleteSession() {}
            }
        }
        install(Postgrest) {
            serializer = KotlinXSerializer(Json {
                ignoreUnknownKeys = true
                encodeDefaults = false
            })
        }
        install(Storage)
    }
}
