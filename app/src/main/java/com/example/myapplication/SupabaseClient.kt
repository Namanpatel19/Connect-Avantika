package com.example.myapplication

import android.util.Log
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.SessionManager
import io.github.jan.supabase.auth.user.UserSession
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.serializer.KotlinXSerializer
import kotlinx.serialization.json.Json
import io.ktor.client.plugins.*
import io.github.jan.supabase.annotations.SupabaseInternal

object SupabaseClient {
    private const val TAG = "SupabaseClient"
    private val SUPABASE_URL = BuildConfig.SUPABASE_URL_S
    private val SUPABASE_KEY = BuildConfig.SUPABASE_PUBLIC_KEY
    private val SUPABASE_ADMIN_KEY = BuildConfig.SUPABASE_ADMIN_KEY

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

    // Admin client strictly using the Service Role Key with a separate session manager
    // We use a completely separate instance to prevent session sharing
    @OptIn(SupabaseInternal::class)
    val adminClient = if (SUPABASE_ADMIN_KEY.isNotEmpty()) {
        Log.d(TAG, "Initializing Admin Client with Service Role Key")
        createSupabaseClient(
            supabaseUrl = SUPABASE_URL,
            supabaseKey = SUPABASE_ADMIN_KEY
        ) {
            httpConfig {
                install(HttpTimeout) {
                    requestTimeoutMillis = 90000
                }
            }
            install(Auth) {
                // Completely isolate the admin client's session logic
                // Returning null from loadSession ensures it always uses the supabaseKey (Service Role Key)
                sessionManager = object : SessionManager {
                    override suspend fun saveSession(session: UserSession) {
                        Log.d(TAG, "Admin client saveSession ignored")
                    }
                    override suspend fun loadSession(): UserSession? {
                        Log.d(TAG, "Admin client loadSession returning null")
                        return null
                    }
                    override suspend fun deleteSession() {
                        Log.d(TAG, "Admin client deleteSession ignored")
                    }
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
    } else {
        Log.e(TAG, "SUPABASE_ADMIN_KEY is empty! Admin operations will fail.")
        client
    }
}
