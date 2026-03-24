package com.example.myapplication.repository

import com.example.myapplication.SupabaseClient
import com.example.myapplication.data.User
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository {
    private val auth = SupabaseClient.client.auth
    private val db = SupabaseClient.client.postgrest

    suspend fun signUp(email: String, password: String, name: String, role: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }
            
            // Create user profile in 'users' table
            val currentUser = auth.currentUserOrNull()
            if (currentUser != null) {
                val userProfile = User(
                    userId = currentUser.id,
                    name = name,
                    email = email,
                    role = role
                )
                db.from("users").insert(userProfile)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(email: String, password: String): Result<User?> = withContext(Dispatchers.IO) {
        try {
            auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            val userId = auth.currentUserOrNull()?.id
            if (userId != null) {
                val profile = db.from("users")
                    .select {
                        filter {
                            eq("user_id", userId)
                        }
                    }.decodeSingle<User>()
                Result.success(profile)
            } else {
                Result.failure(Exception("User not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getCurrentUser() = auth.currentUserOrNull()

    suspend fun signOut() {
        auth.signOut()
    }
}
