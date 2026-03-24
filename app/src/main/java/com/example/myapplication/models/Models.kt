package com.example.myapplication.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    @SerialName("user_id") val userId: String,
    val name: String,
    val email: String,
    val role: String,
    @SerialName("department_id") val departmentId: String? = null,
    @SerialName("profile_image_url") val profileImageUrl: String? = null,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class Event(
    @SerialName("event_id") val eventId: String = "",
    val title: String = "",
    val description: String? = null,
    @SerialName("event_head_id") val eventHeadId: String? = null,
    val date: String? = null,
    val location: String? = null,
    @SerialName("cover_image_url") val coverImageUrl: String? = null,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class Club(
    @SerialName("club_id") val clubId: String = "",
    val name: String = "",
    val description: String? = null,
    @SerialName("club_head_id") val clubHeadId: String? = null,
    @SerialName("cover_image_url") val coverImageUrl: String? = null,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class Student(
    @SerialName("student_id") val studentId: String = "",
    @SerialName("user_id") val userId: String = "",
    val name: String = "",
    val department: String = "",
    @SerialName("enrollment_no") val enrollmentNo: String? = null,
    val year: Int? = null
)

@Serializable
data class Announcement(
    @SerialName("announcement_id") val announcementId: String = "",
    val title: String? = null,
    val message: String? = null,
    @SerialName("posted_by") val postedBy: String? = null,
    @SerialName("target_role") val targetRole: String? = null,
    @SerialName("created_at") val createdAt: String? = null
)
