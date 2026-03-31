package com.example.myapplication.repository

import android.util.Log
import com.example.myapplication.SupabaseClient
import com.example.myapplication.data.*
import io.github.jan.supabase.auth.*
import io.github.jan.supabase.postgrest.postgrest
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

    // --- Auth & User ---
    suspend fun getUserRole(userId: String): String? = withContext(Dispatchers.IO) {
        try {
            val user = db.from("users").select {
                filter { eq("id", userId) }
            }.decodeSingleOrNull<User>()
            
            if (user != null) return@withContext user.role

            val adminUser = adminDb.from("users").select {
                filter { eq("id", userId) }
            }.decodeSingleOrNull<User>()
            
            adminUser?.role
        } catch (e: Exception) {
            Log.e("MainRepository", "Error fetching user role for $userId: ${e.message}")
            null
        }
    }

    suspend fun getAllUsers(): List<User> = withContext(Dispatchers.IO) {
        try {
            adminDb.from("users").select().decodeList<User>()
        } catch (e: Exception) { emptyList() }
    }

    // --- Events ---
    suspend fun getEvents(status: String? = "approved"): List<Event> = withContext(Dispatchers.IO) {
        try {
            db.from("events").select {
                status?.let { filter { eq("status", it) } }
            }.decodeList<Event>()
        } catch (e: Exception) { emptyList() }
    }

    suspend fun getAllEvents(): List<Event> = withContext(Dispatchers.IO) {
        try {
            db.from("events").select().decodeList<Event>()
        } catch (e: Exception) { emptyList() }
    }

    suspend fun createEvent(event: Event): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val database = if (event.status == "approved") adminDb else db
            database.from("events").insert(event)
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("MainRepository", "Error creating event: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun updateEventStatus(eventId: String, status: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            adminDb.from("events").update({
                set("status", status)
            }) {
                filter { eq("id", eventId) }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteEvent(eventId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            adminDb.from("events").delete { filter { eq("id", eventId) } }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- Clubs ---
    suspend fun getClubs(): List<Club> = withContext(Dispatchers.IO) {
        try {
            db.from("clubs").select().decodeList<Club>()
        } catch (e: Exception) { emptyList() }
    }

    suspend fun createClub(club: Club): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            adminDb.from("clubs").insert(club)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateClub(club: Club): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            adminDb.from("clubs").update(club) { filter { eq("id", club.id!!) } }
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun deleteClub(clubId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            adminDb.from("clubs").delete { filter { eq("id", clubId) } }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getClubByHeadId(userId: String): Club? = withContext(Dispatchers.IO) {
        try {
            db.from("clubs").select {
                filter { eq("club_head_id", userId) }
            }.decodeSingleOrNull<Club>()
        } catch (e: Exception) { null }
    }

    suspend fun getEventsByClubId(clubId: String): List<Event> = withContext(Dispatchers.IO) {
        try {
            db.from("events").select {
                filter { eq("club_id", clubId) }
            }.decodeList<Event>()
        } catch (e: Exception) { emptyList() }
    }

    suspend fun getClubRequests(clubId: String): List<ClubRequest> = withContext(Dispatchers.IO) {
        try {
            db.from("club_requests").select {
                filter { eq("club_id", clubId) }
            }.decodeList<ClubRequest>()
        } catch (e: Exception) { emptyList() }
    }

    suspend fun updateClubRequest(requestId: String, status: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            adminDb.from("club_requests").update({ set("status", status) }) { filter { eq("id", requestId) } }
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun uploadClubBanner(clubId: String, file: File): Result<String> = withContext(Dispatchers.IO) {
        try {
            val path = "banners/${clubId}_${System.currentTimeMillis()}.${file.extension}"
            storage.from("club banner").upload(path, file.readBytes())
            val publicUrl = storage.from("club banner").publicUrl(path)
            adminDb.from("clubs").update({ set("banner_url", publicUrl) }) { filter { eq("id", clubId) } }
            Result.success(publicUrl)
        } catch (e: Exception) { Result.failure(e) }
    }

    // --- Announcements ---
    suspend fun getAnnouncements(): List<Announcement> = withContext(Dispatchers.IO) {
        try {
            db.from("announcements").select().decodeList<Announcement>()
        } catch (e: Exception) { emptyList() }
    }

    suspend fun createAnnouncement(announcement: Announcement): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            adminDb.from("announcements").insert(announcement)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteAnnouncement(id: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            adminDb.from("announcements").delete { filter { eq("id", id) } }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- Students ---
    suspend fun getAllStudents(): List<Student> = withContext(Dispatchers.IO) {
        try {
            adminDb.from("students").select().decodeList<Student>()
        } catch (e: Exception) { emptyList() }
    }

    suspend fun addStudent(user: User, student: Student, autoConfirm: Boolean = true): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val newUser = adminAuth.createUserWithEmail {
                email = user.email
                password = user.password ?: "Welcome@123"
            }
            val userId = newUser.id
            adminDb.from("users").insert(user.copy(id = userId, password = null))
            adminDb.from("students").insert(student.copy(userId = userId))
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("MainRepository", "Error creating student user: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun deleteStudent(userId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            adminDb.from("students").delete { filter { eq("user_id", userId) } }
            adminDb.from("users").delete { filter { eq("id", userId) } }
            try { adminAuth.deleteUser(userId) } catch (e: Exception) {}
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchStudents(query: String): List<Student> = withContext(Dispatchers.IO) {
        try {
            adminDb.from("students").select {
                filter {
                    or {
                        ilike("name", "%$query%")
                        ilike("enrollment", "%$query%")
                    }
                }
            }.decodeList<Student>()
        } catch (e: Exception) { emptyList() }
    }

    // --- Faculty ---
    suspend fun getAllFaculty(): List<Faculty> = withContext(Dispatchers.IO) {
        try {
            adminDb.from("faculty").select().decodeList<Faculty>()
        } catch (e: Exception) { emptyList() }
    }

    suspend fun addFaculty(user: User, faculty: Faculty): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val newUser = adminAuth.createUserWithEmail {
                email = user.email
                password = user.password ?: "Welcome@123"
            }
            val userId = newUser.id
            adminDb.from("users").insert(user.copy(id = userId, password = null))
            adminDb.from("faculty").insert(faculty.copy(userId = userId))
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("MainRepository", "Error adding faculty: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun deleteFaculty(userId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            adminDb.from("faculty").delete { filter { eq("user_id", userId) } }
            adminDb.from("users").delete { filter { eq("id", userId) } }
            try { adminAuth.deleteUser(userId) } catch (e: Exception) {}
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateStudentProfile(student: Student): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            adminDb.from("students").update(student) { filter { eq("user_id", student.userId) } }
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun updateFacultyProfile(faculty: Faculty): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            adminDb.from("faculty").update(faculty) { filter { eq("user_id", faculty.userId) } }
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun getStudentProfile(userId: String): Student? = withContext(Dispatchers.IO) {
        try { adminDb.from("students").select { filter { eq("user_id", userId) } }.decodeSingleOrNull<Student>() } catch (e: Exception) { null }
    }

    suspend fun getFacultyProfile(userId: String): Faculty? = withContext(Dispatchers.IO) {
        try { adminDb.from("faculty").select { filter { eq("user_id", userId) } }.decodeSingleOrNull<Faculty>() } catch (e: Exception) { null }
    }

    suspend fun uploadProfileImage(userId: String, file: File, role: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val path = "profiles/${userId}_${System.currentTimeMillis()}.${file.extension}"
            storage.from("profiles").upload(path, file.readBytes())
            val publicUrl = storage.from("profiles").publicUrl(path)
            if (role == "student") adminDb.from("students").update({ set("photo_url", publicUrl) }) { filter { eq("user_id", userId) } }
            else adminDb.from("faculty").update({ set("photo_url", publicUrl) }) { filter { eq("user_id", userId) } }
            Result.success(publicUrl)
        } catch (e: Exception) { Result.failure(e) }
    }
    
    suspend fun registerForEvent(eventId: String, studentId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try { adminDb.from("event_registrations").insert(EventRegistration(eventId = eventId, studentId = studentId)); Result.success(Unit) } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun joinClub(clubId: String, studentId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try { adminDb.from("club_requests").insert(ClubRequest(clubId = clubId, studentId = studentId)); Result.success(Unit) } catch (e: Exception) { Result.failure(e) }
    }
}
