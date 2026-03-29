package com.example.myapplication.models

import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    val id: String,
    val name: String,
    val email: String,
    val role: String, // student, faculty, club_head, dean
    val profile_image: String? = null,
    val department: String? = null
)
