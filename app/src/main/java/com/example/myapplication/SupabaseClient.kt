package com.example.myapplication

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.serializer.KotlinXSerializer
import kotlinx.serialization.json.Json
import io.ktor.client.plugins.*
import io.github.jan.supabase.annotations.SupabaseInternal

object SupabaseClient {
    private const val SUPABASE_URL = "SUPABASE_URL_S"
    private const val SUPABASE_KEY = "SUPABASE_PUBLIC_KEY"

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

    val adminClient = client
}
