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

    // --- Auth & User ---
    suspend fun getUserRole(userId: String): String? = withContext(Dispatchers.IO) {
        try {
            val user = db.from("users").select {
                filter { eq("id", userId) }
            }.decodeSingleOrNull<User>()
            user?.role
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getAllUsers(): List<User> = withContext(Dispatchers.IO) {
        try {
            db.from("users").select().decodeList<User>()
        } catch (e: Exception) { emptyList() }
    }

    // --- Events ---
    suspend fun getEvents(status: String? = "approved"): List<Event> = withContext(Dispatchers.IO) {
        db.from("events").select {
            status?.let { filter { eq("status", it) } }
        }.decodeList<Event>()
    }

    suspend fun getAllEvents(): List<Event> = withContext(Dispatchers.IO) {
        db.from("events").select().decodeList<Event>()
    }

    suspend fun createEvent(event: Event): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            db.from("events").insert(event)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createEventRequest(event: Event): Result<Unit> = createEvent(event)

    suspend fun updateEventStatus(eventId: String, status: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            db.from("events").update({
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
            db.from("events").delete { filter { eq("id", eventId) } }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- Clubs ---
    suspend fun getClubs(): List<Club> = withContext(Dispatchers.IO) {
        db.from("clubs").select().decodeList<Club>()
    }

    suspend fun createClub(club: Club): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            db.from("clubs").insert(club)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateClub(club: Club): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            db.from("clubs").update(club) { filter { eq("id", club.id!!) } }
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun deleteClub(clubId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            db.from("clubs").delete { filter { eq("id", clubId) } }
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
            db.from("club_requests").update({ set("status", status) }) { filter { eq("id", requestId) } }
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun uploadClubBanner(clubId: String, file: File): Result<String> = withContext(Dispatchers.IO) {
        try {
            val path = "banners/${clubId}_${System.currentTimeMillis()}.${file.extension}"
            storage.from("club banner").upload(path, file.readBytes())
            val publicUrl = storage.from("club banner").publicUrl(path)
            db.from("clubs").update({ set("banner_url", publicUrl) }) { filter { eq("id", clubId) } }
            Result.success(publicUrl)
        } catch (e: Exception) { Result.failure(e) }
    }

    // --- Announcements ---
    suspend fun getAnnouncements(): List<Announcement> = withContext(Dispatchers.IO) {
        db.from("announcements").select().decodeList<Announcement>()
    }

    suspend fun createAnnouncement(announcement: Announcement): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            db.from("announcements").insert(announcement)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteAnnouncement(id: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            db.from("announcements").delete { filter { eq("id", id) } }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- Students ---
    suspend fun getAllStudents(): List<Student> = withContext(Dispatchers.IO) {
        try {
            db.from("students").select().decodeList<Student>()
        } catch (e: Exception) { emptyList() }
    }

    suspend fun addStudent(user: User, student: Student, autoConfirm: Boolean = true): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val newUser = adminAuth.createUserWithEmail {
                email = user.email
                password = user.password ?: "Welcome@123"
            }
            val userId = newUser.id
            db.from("users").insert(user.copy(id = userId, password = null))
            db.from("students").insert(student.copy(userId = userId))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteStudent(userId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            db.from("students").delete { filter { eq("user_id", userId) } }
            db.from("users").delete { filter { eq("id", userId) } }
            try { adminAuth.deleteUser(userId) } catch (e: Exception) {}
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchStudents(query: String): List<Student> = withContext(Dispatchers.IO) {
        db.from("students").select {
            filter {
                or {
                    ilike("name", "%$query%")
                    ilike("enrollment", "%$query%")
                }
            }
        }.decodeList<Student>()
    }

    // --- Faculty ---
    suspend fun getAllFaculty(): List<Faculty> = withContext(Dispatchers.IO) {
        try {
            db.from("faculty").select().decodeList<Faculty>()
        } catch (e: Exception) { emptyList() }
    }

    suspend fun addFaculty(user: User, faculty: Faculty): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val newUser = adminAuth.createUserWithEmail {
                email = user.email
                password = user.password ?: "Welcome@123"
            }
            val userId = newUser.id
            db.from("users").insert(user.copy(id = userId, password = null))
            db.from("faculty").insert(faculty.copy(userId = userId))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteFaculty(userId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            db.from("faculty").delete { filter { eq("user_id", userId) } }
            db.from("users").delete { filter { eq("id", userId) } }
            try { adminAuth.deleteUser(userId) } catch (e: Exception) {}
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- Study Materials ---
    suspend fun getStudyMaterials(): List<StudyMaterial> = withContext(Dispatchers.IO) {
        db.from("study_materials").select().decodeList<StudyMaterial>()
    }

    suspend fun uploadStudyMaterial(title: String, subject: String, file: File, uploaderId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val fileName = "${System.currentTimeMillis()}_${file.name}"
            val path = "materials/$fileName"
            storage.from("study-materials").upload(path, file.readBytes())
            val publicUrl = storage.from("study-materials").publicUrl(path)
            
            val material = StudyMaterial(title = title, subject = subject, fileUrl = publicUrl, facultyId = uploaderId)
            db.from("study_materials").insert(material)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateStudentProfile(student: Student): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            db.from("students").update(student) { filter { eq("user_id", student.userId) } }
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun updateFacultyProfile(faculty: Faculty): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            db.from("faculty").update(faculty) { filter { eq("user_id", faculty.userId) } }
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun getStudentProfile(userId: String): Student? = withContext(Dispatchers.IO) {
        try { db.from("students").select { filter { eq("user_id", userId) } }.decodeSingleOrNull<Student>() } catch (e: Exception) { null }
    }

    suspend fun getFacultyProfile(userId: String): Faculty? = withContext(Dispatchers.IO) {
        try { db.from("faculty").select { filter { eq("user_id", userId) } }.decodeSingleOrNull<Faculty>() } catch (e: Exception) { null }
    }

    suspend fun uploadProfileImage(userId: String, file: File, role: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val path = "profiles/${userId}_${System.currentTimeMillis()}.${file.extension}"
            storage.from("profiles").upload(path, file.readBytes())
            val publicUrl = storage.from("profiles").publicUrl(path)
            if (role == "student") db.from("students").update({ set("photo_url", publicUrl) }) { filter { eq("user_id", userId) } }
            else db.from("faculty").update({ set("photo_url", publicUrl) }) { filter { eq("user_id", userId) } }
            Result.success(publicUrl)
        } catch (e: Exception) { Result.failure(e) }
    }
    
    suspend fun registerForEvent(eventId: String, studentId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try { db.from("event_registrations").insert(EventRegistration(eventId = eventId, studentId = studentId)); Result.success(Unit) } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun joinClub(clubId: String, studentId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try { db.from("club_requests").insert(ClubRequest(clubId = clubId, studentId = studentId)); Result.success(Unit) } catch (e: Exception) { Result.failure(e) }
    }
}
