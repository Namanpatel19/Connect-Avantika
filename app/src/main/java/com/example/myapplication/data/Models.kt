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
    val status: String = "pending",
    @SerialName("event_date") val eventDate: String? = null,
    @SerialName("created_at") val createdAt: String? = null
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
    @SerialName("faculty_id") val facultyId: String? = null,
    val title: String,
    @SerialName("file_url") val fileUrl: String,
    val subject: String? = null,
    val batch: String? = null,
    val department: String? = null,
    @SerialName("file_name") val fileName: String? = null,
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
    @SerialName("requested_at") val requestedAt: String? = null
)

@Serializable
data class EventRegistration(
    val id: String? = null,
    @SerialName("event_id") val eventId: String,
    @SerialName("student_id") val studentId: String,
    @SerialName("registered_at") val registeredAt: String? = null
)
