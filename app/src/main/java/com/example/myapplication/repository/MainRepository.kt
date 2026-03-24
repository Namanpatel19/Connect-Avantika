package com.example.myapplication.repository

import com.example.myapplication.SupabaseClient
import com.example.myapplication.data.*
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class MainRepository {
    private val db = SupabaseClient.client.postgrest
    private val storage = SupabaseClient.client.storage

    // Events
    suspend fun getEvents(): List<Event> = withContext(Dispatchers.IO) {
        db.from("events").select().decodeList<Event>()
    }

    suspend fun registerForEvent(eventId: Int, studentId: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val registration = EventRegistration(eventId = eventId, studentId = studentId)
            db.from("event_registrations").insert(registration)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Clubs
    suspend fun getClubs(): List<Club> = withContext(Dispatchers.IO) {
        db.from("clubs").select().decodeList<Club>()
    }

    suspend fun joinClub(clubId: Int, studentId: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val membership = ClubMember(clubId = clubId, studentId = studentId)
            db.from("club_members").insert(membership)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Announcements
    suspend fun getAnnouncements(role: String): List<Announcement> = withContext(Dispatchers.IO) {
        db.from("announcements").select {
            filter {
                eq("target_role", role)
            }
        }.decodeList<Announcement>()
    }

    // Academics
    suspend fun getAcademics(studentId: Int): List<Academic> = withContext(Dispatchers.IO) {
        db.from("academics").select {
            filter {
                eq("student_id", studentId)
            }
        }.decodeList<Academic>()
    }

    // Study Materials & Storage
    suspend fun uploadStudyMaterial(file: File, fileName: String, title: String, subject: String, facultyId: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val bucket = storage.from("materials")
            bucket.upload(fileName, file.readBytes())
            val fileUrl = bucket.publicUrl(fileName)
            
            val material = StudyMaterial(
                title = title,
                fileUrl = fileUrl,
                subject = subject,
                facultyId = facultyId
            )
            db.from("study_materials").insert(material)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getStudyMaterials(): List<StudyMaterial> = withContext(Dispatchers.IO) {
        db.from("study_materials").select().decodeList<StudyMaterial>()
    }
}
