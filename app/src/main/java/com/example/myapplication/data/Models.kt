package com.example.myapplication.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Transient

@Serializable
data class User(
    val id: String,
    val email: String,
    @Transient val password: String? = null,
    val role: String,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class Student(
    @SerialName("user_id") val userId: String,
    val name: String,
    val enrollment: String,
    val department: String? = null,
    val contact: String? = null,
    val batch: String? = null,
    @SerialName("photo_url") val photoUrl: String? = null
)

@Serializable
data class Faculty(
    @SerialName("user_id") val userId: String,
    val name: String,
    val department: String? = null,
    val contact: String? = null,
    @SerialName("photo_url") val photoUrl: String? = null
)

@Serializable
data class Event(
    val id: String? = null,
    val title: String,
    val description: String? = null,
    @SerialName("club_id") val clubId: String? = null,
    @SerialName("created_by") val createdBy: String? = null,
    val status: String = "pending", // pending, approved, rejected
    @SerialName("event_date") val eventDate: String? = null,
    @SerialName("banner_url") val bannerUrl: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("dean_id") val deanId: String? = null, // The dean selected for approval
    @SerialName("entry_fee") val entryFee: Double? = 0.0,
    @SerialName("is_paid") val isPaid: Boolean = false
)

@Serializable
data class EventRegistration(
    val id: String? = null,
    @SerialName("event_id") val eventId: String,
    @SerialName("student_id") val studentId: String,
    val email: String? = null,
    val contact: String? = null,
    val confirmed: Boolean = false,
    @SerialName("registered_at") val registeredAt: String? = null
)

@Serializable
data class Club(
    val id: String? = null,
    val name: String,
    val description: String? = null,
    @SerialName("club_head_id") val clubHeadId: String? = null,
    @SerialName("banner_url") val bannerUrl: String? = null,
    val category: String? = "Other",
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class ClubRequest(
    val id: String? = null,
    @SerialName("club_id") val clubId: String,
    @SerialName("student_id") val studentId: String,
    val status: String = "pending",
    @SerialName("interview_date") val interviewDate: String? = null,
    @SerialName("interview_time") val interviewTime: String? = null,
    @SerialName("venue") val interviewVenue: String? = null,
    @SerialName("requested_at") val requestedAt: String? = null
)

@Serializable
data class ClubMember(
    val id: String? = null,
    @SerialName("club_id") val clubId: String,
    @SerialName("student_id") val studentId: String,
    @SerialName("joined_at") val joinedAt: String? = null
)

@Serializable
data class Notification(
    val id: String? = null,
    @SerialName("user_id") val userId: String,
    val title: String,
    val message: String,
    @SerialName("is_read") val isRead: Boolean = false,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class Announcement(
    val id: String? = null,
    val title: String,
    val content: String? = null,
    @SerialName("created_by") val createdBy: String? = null,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class StudyMaterial(
    val id: String? = null,
    val title: String,
    val subject: String? = null,
    val batch: String? = null,
    val department: String? = null,
    @SerialName("file_url") val fileUrl: String? = null,
    @SerialName("uploaded_by") val uploadedBy: String? = null,
    @SerialName("created_at") val createdAt: String? = null
)
