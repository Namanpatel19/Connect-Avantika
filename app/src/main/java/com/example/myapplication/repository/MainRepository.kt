package com.example.myapplication.repository

import android.util.Log
import com.example.myapplication.SupabaseClient
import com.example.myapplication.data.*
import io.github.jan.supabase.auth.*
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class MainRepository {
    private val db = SupabaseClient.client.postgrest
    private val auth = SupabaseClient.client.auth
    private val storage = SupabaseClient.client.storage
    private val adminAuth = SupabaseClient.adminClient.auth.admin
    private val adminDb = SupabaseClient.adminClient.postgrest
    private val adminStorage = SupabaseClient.adminClient.storage

    // --- Auth & User ---
    suspend fun getUserRole(userId: String): String? = withContext(Dispatchers.IO) {
        try {
            val user = adminDb.from("users").select {
                filter { eq("id", userId) }
            }.decodeSingleOrNull<User>()
            user?.role
        } catch (e: Exception) {
            Log.e("MainRepository", "Role fetch failed: ${e.message}")
            null
        }
    }

    suspend fun getUserByEmail(email: String): User? = withContext(Dispatchers.IO) {
        try {
            adminDb.from("users").select {
                filter { eq("email", email) }
            }.decodeSingleOrNull<User>()
        } catch (e: Exception) { 
            Log.e("MainRepository", "User fetch by email failed: ${e.message}")
            null 
        }
    }

    suspend fun getAllDeans(): List<User> = withContext(Dispatchers.IO) {
        try {
            adminDb.from("users").select {
                filter { eq("role", "dean") }
            }.decodeList<User>()
        } catch (e: Exception) {
            Log.e("MainRepository", "Deans fetch failed: ${e.message}")
            emptyList()
        }
    }

    // --- Clubs & Join Logic ---
    suspend fun getClubs(): List<Club> = withContext(Dispatchers.IO) {
        try {
            db.from("clubs").select().decodeList<Club>()
        } catch (e: Exception) { 
            Log.e("MainRepository", "Clubs fetch failed: ${e.message}")
            emptyList() 
        }
    }

    suspend fun joinClub(clubId: String, studentId: String): Result<Unit> = withContext(Dispatchers.IO) {
        if (studentId.isBlank()) return@withContext Result.failure(Exception("User ID missing"))
        try {
            val existing = adminDb.from("club_requests").select {
                filter {
                    eq("club_id", clubId)
                    eq("student_id", studentId)
                }
            }.decodeSingleOrNull<ClubRequest>()

            if (existing != null) {
                if (existing.status == "rejected") {
                    adminDb.from("club_requests").update(mapOf(
                        "status" to "pending",
                        "interview_date" to null,
                        "interview_time" to null,
                        "venue" to null
                    )) {
                        filter { eq("id", existing.id!!) }
                    }
                    return@withContext Result.success(Unit)
                }
                return@withContext Result.failure(Exception("Already applied"))
            }

            adminDb.from("club_requests").insert(ClubRequest(clubId = clubId, studentId = studentId))
            Result.success(Unit)
        } catch (e: Exception) { 
            Log.e("MainRepository", "Join club failed", e)
            Result.failure(e) 
        }
    }

    suspend fun updateClubRequestStatus(requestId: String, status: String, date: String? = null, time: String? = null, venue: String? = null): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val updates = mutableMapOf<String, Any?>("status" to status)
            if (date != null) updates["interview_date"] = date
            if (time != null) updates["interview_time"] = time
            if (venue != null) updates["venue"] = venue
            adminDb.from("club_requests").update(updates) { filter { eq("id", requestId) } }
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun addClubMember(clubId: String, studentId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val existing = adminDb.from("club_members").select { filter { eq("club_id", clubId); eq("student_id", studentId) } }.decodeSingleOrNull<ClubMember>()
            if (existing != null) return@withContext Result.success(Unit)
            adminDb.from("club_members").insert(ClubMember(clubId = clubId, studentId = studentId))
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    // --- Events ---
    suspend fun getAllEvents(): List<Event> = withContext(Dispatchers.IO) { try { db.from("events").select().decodeList<Event>() } catch (e: Exception) { emptyList() } }
    
    suspend fun getEventsForDean(deanId: String): List<Event> = withContext(Dispatchers.IO) {
        try {
            adminDb.from("events").select {
                filter { eq("dean_id", deanId); eq("status", "pending") }
            }.decodeList<Event>()
        } catch (e: Exception) { emptyList() }
    }

    suspend fun getApprovedEvents(): List<Event> = withContext(Dispatchers.IO) {
        try {
            db.from("events").select {
                filter { eq("status", "approved") }
            }.decodeList<Event>()
        } catch (e: Exception) { emptyList() }
    }

    suspend fun createEvent(event: Event): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            adminDb.from("events").insert(event)
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun updateEventStatus(eventId: String, status: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            adminDb.from("events").update({ set("status", status) }) { filter { eq("id", eventId) } }
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun registerForEvent(registration: EventRegistration): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            adminDb.from("event_registrations").insert(registration)
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun getEventRegistrations(eventId: String): List<EventRegistration> = withContext(Dispatchers.IO) {
        try {
            adminDb.from("event_registrations").select {
                filter { eq("event_id", eventId) }
            }.decodeList<EventRegistration>()
        } catch (e: Exception) { emptyList() }
    }

    // --- Others ---
    suspend fun sendNotification(userId: String, title: String, message: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            adminDb.from("notifications").insert(Notification(userId = userId, title = title, message = message))
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun uploadEventBanner(file: File): Result<String> = withContext(Dispatchers.IO) { try { val path = "banners/event_${System.currentTimeMillis()}.${file.extension}"; adminStorage.from("event banner").upload(path, file.readBytes()) { upsert = true }; val publicUrl = adminStorage.from("event banner").publicUrl(path); Result.success(publicUrl) } catch (e: Exception) { Result.failure(e) } }
    suspend fun getClubRequests(clubId: String): List<ClubRequest> = withContext(Dispatchers.IO) { try { adminDb.from("club_requests").select { filter { eq("club_id", clubId) }; order("requested_at", Order.DESCENDING) }.decodeList<ClubRequest>() } catch (e: Exception) { emptyList() } }
    suspend fun getUserClubRequests(studentId: String): List<ClubRequest> = withContext(Dispatchers.IO) { try { adminDb.from("club_requests").select { filter { eq("student_id", studentId) } }.decodeList<ClubRequest>() } catch (e: Exception) { emptyList() } }
    suspend fun getClubByHeadId(userId: String): Club? = withContext(Dispatchers.IO) { try { adminDb.from("clubs").select { filter { eq("club_head_id", userId) } }.decodeSingleOrNull<Club>() } catch (e: Exception) { null } }
    suspend fun getEventsByClubId(clubId: String): List<Event> = withContext(Dispatchers.IO) { try { db.from("events").select { filter { eq("club_id", clubId) } }.decodeList<Event>() } catch (e: Exception) { emptyList() } }
    suspend fun getNotifications(userId: String): List<Notification> = withContext(Dispatchers.IO) { try { db.from("notifications").select { filter { eq("user_id", userId) }; order("created_at", Order.DESCENDING) }.decodeList<Notification>() } catch (e: Exception) { emptyList() } }
    suspend fun getAnnouncements(): List<Announcement> = withContext(Dispatchers.IO) { try { db.from("announcements").select().decodeList<Announcement>() } catch (e: Exception) { emptyList() } }
    suspend fun getAllStudents(): List<Student> = withContext(Dispatchers.IO) { try { adminDb.from("students").select().decodeList<Student>() } catch (e: Exception) { emptyList() } }
}
