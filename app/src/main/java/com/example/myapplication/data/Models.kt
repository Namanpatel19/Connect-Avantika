package com.example.myapplication.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class User(
    @SerialName("user_id") val userId: String,
    val name: String,
    val email: String,
    val role: String,
    @SerialName("department_id") val departmentId: Int? = null,
    @SerialName("profile_image_url") val profileImageUrl: String? = null
)

@Serializable
data class Student(
    @SerialName("student_id") val studentId: Int,
    @SerialName("user_id") val userId: String,
    @SerialName("enrollment_no") val enrollmentNo: String,
    val year: Int
)

@Serializable
data class Faculty(
    @SerialName("faculty_id") val facultyId: Int,
    @SerialName("user_id") val userId: String,
    val designation: String
)

@Serializable
data class Department(
    @SerialName("department_id") val departmentId: Int,
    @SerialName("department_name") val departmentName: String,
    @SerialName("hod_id") val hodId: Int? = null
)

@Serializable
data class Event(
    @SerialName("event_id") val eventId: Int? = null,
    val title: String,
    val description: String,
    @SerialName("event_date") val eventDate: String,
    val location: String
)

@Serializable
data class EventRegistration(
    @SerialName("reg_id") val regId: Int? = null,
    @SerialName("event_id") val eventId: Int,
    @SerialName("student_id") val studentId: Int,
    val status: String = "Registered"
)

@Serializable
data class Club(
    @SerialName("club_id") val clubId: Int? = null,
    val name: String,
    val description: String,
    @SerialName("club_head_id") val clubHeadId: Int? = null
)

@Serializable
data class ClubMember(
    val id: Int? = null,
    @SerialName("club_id") val clubId: Int,
    @SerialName("student_id") val studentId: Int,
    val role: String = "Member"
)

@Serializable
data class Announcement(
    @SerialName("announcement_id") val announcementId: Int? = null,
    val title: String,
    val message: String,
    @SerialName("target_role") val targetRole: String
)

@Serializable
data class Academic(
    @SerialName("academic_id") val academicId: Int? = null,
    @SerialName("student_id") val studentId: Int,
    val subject: String,
    @SerialName("attendance_percentage") val attendancePercentage: Double,
    val marks: Int
)

@Serializable
data class StudyMaterial(
    @SerialName("material_id") val materialId: Int? = null,
    val title: String,
    @SerialName("file_url") val fileUrl: String,
    val subject: String,
    @SerialName("faculty_id") val facultyId: Int
)
