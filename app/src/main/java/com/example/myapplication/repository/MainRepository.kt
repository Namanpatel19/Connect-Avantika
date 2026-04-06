package com.example.myapplication.repository

import android.util.Log
import com.example.myapplication.SupabaseClient
import com.example.myapplication.data.*
import io.github.jan.supabase.auth.*
import io.github.jan.supabase.auth.admin.*
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

    suspend fun createClub(club: Club): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            adminDb.from("clubs").insert(club)
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun updateClub(club: Club): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            adminDb.from("clubs").update(club) { filter { eq("id", club.id!!) } }
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun deleteClub(id: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            adminDb.from("clubs").delete { filter { eq("id", id) } }
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun updateClubLead(clubId: String, userId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            adminDb.from("clubs").update({ set("club_head_id", userId) }) { filter { eq("id", clubId) } }
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
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
    
    suspend fun getEvents(status: String?): List<Event> = withContext(Dispatchers.IO) {
        try {
            if (status == null) {
                db.from("events").select().decodeList<Event>()
            } else {
                db.from("events").select {
                    filter { eq("status", status) }
                }.decodeList<Event>()
            }
        } catch (e: Exception) { emptyList() }
    }

    @Suppress("UNUSED_PARAMETER")
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

    suspend fun deleteEvent(id: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            adminDb.from("events").delete { filter { eq("id", id) } }
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun registerForEvent(registration: EventRegistration): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            adminDb.from("event_registrations").insert(registration)
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun registerForEvent(eventId: String, studentId: String): Result<Unit> = registerForEvent(EventRegistration(eventId = eventId, studentId = studentId))

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
    
    suspend fun uploadClubBanner(file: File): Result<String> = withContext(Dispatchers.IO) { 
        try { 
            val path = "banners/club_${System.currentTimeMillis()}.${file.extension}"
            adminStorage.from("club banner").upload(path, file.readBytes()) { upsert = true }
            val publicUrl = adminStorage.from("club banner").publicUrl(path)
            Result.success(publicUrl) 
        } catch (e: Exception) { Result.failure(e) } 
    }

    suspend fun getClubRequests(clubId: String): List<ClubRequest> = withContext(Dispatchers.IO) { try { adminDb.from("club_requests").select { filter { eq("club_id", clubId) }; order("requested_at", Order.DESCENDING) }.decodeList<ClubRequest>() } catch (e: Exception) { emptyList() } }
    suspend fun getUserClubRequests(studentId: String): List<ClubRequest> = withContext(Dispatchers.IO) { try { adminDb.from("club_requests").select { filter { eq("student_id", studentId) } }.decodeList<ClubRequest>() } catch (e: Exception) { emptyList() } }
    suspend fun getClubByHeadId(userId: String): Club? = withContext(Dispatchers.IO) { try { adminDb.from("clubs").select { filter { eq("club_head_id", userId) } }.decodeSingleOrNull<Club>() } catch (e: Exception) { null } }
    suspend fun getEventsByClubId(clubId: String): List<Event> = withContext(Dispatchers.IO) { try { db.from("events").select { filter { eq("club_id", clubId) } }.decodeList<Event>() } catch (e: Exception) { emptyList() } }
    suspend fun getNotifications(userId: String): List<Notification> = withContext(Dispatchers.IO) { try { db.from("notifications").select { filter { eq("user_id", userId) }; order("created_at", Order.DESCENDING) }.decodeList<Notification>() } catch (e: Exception) { emptyList() } }
    suspend fun getAnnouncements(): List<Announcement> = withContext(Dispatchers.IO) { try { db.from("announcements").select().decodeList<Announcement>() } catch (e: Exception) { emptyList() } }
    
    suspend fun createAnnouncement(ann: Announcement): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            adminDb.from("announcements").insert(ann)
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun deleteAnnouncement(id: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            adminDb.from("announcements").delete { filter { eq("id", id) } }
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun getStudentProfile(userId: String): Student? = withContext(Dispatchers.IO) { try { adminDb.from("students").select { filter { eq("user_id", userId) } }.decodeSingleOrNull<Student>() } catch (e: Exception) { null } }
    
    suspend fun updateStudentProfile(student: Student): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            adminDb.from("students").update(student) { filter { eq("user_id", student.userId) } }
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun getAllStudents(): List<Student> = withContext(Dispatchers.IO) {
        try {
            adminDb.from("students").select().decodeList<Student>()
        } catch (e: Exception) {
            Log.e("MainRepository", "Students fetch failed: ${e.message}")
            emptyList()
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
        } catch (e: Exception) {
            Log.e("MainRepository", "Student search failed: ${e.message}")
            emptyList()
        }
    }

    suspend fun addStudent(user: User, student: Student): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val newUser = adminAuth.createUserWithEmail {
                email = user.email
                password = user.password!!
                autoConfirm = true
            }
            val newId = newUser.id
            adminDb.from("users").insert(user.copy(id = newId))
            adminDb.from("students").insert(student.copy(userId = newId))
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("MainRepository", "Add student failed", e)
            Result.failure(e)
        }
    }

    suspend fun deleteStudent(userId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            adminDb.from("students").delete { filter { eq("user_id", userId) } }
            adminDb.from("users").delete { filter { eq("id", userId) } }
            adminAuth.deleteUser(userId)
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("MainRepository", "Delete student failed", e)
            Result.failure(e)
        }
    }

    suspend fun getFacultyProfile(userId: String): Faculty? = withContext(Dispatchers.IO) {
        try {
            adminDb.from("faculty").select { filter { eq("user_id", userId) } }.decodeSingleOrNull<Faculty>()
        } catch (e: Exception) {
            Log.e("MainRepository", "Faculty profile fetch failed: ${e.message}")
            null
        }
    }

    suspend fun updateFacultyProfile(faculty: Faculty): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            adminDb.from("faculty").update(faculty) { filter { eq("user_id", faculty.userId) } }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("MainRepository", "Faculty profile update failed: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun getAllFaculty(): List<Faculty> = withContext(Dispatchers.IO) {
        try {
            adminDb.from("faculty").select().decodeList<Faculty>()
        } catch (e: Exception) {
            Log.e("MainRepository", "Faculty fetch failed: ${e.message}")
            emptyList()
        }
    }

    suspend fun addFaculty(user: User, faculty: Faculty): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val newUser = adminAuth.createUserWithEmail {
                email = user.email
                password = user.password!!
                autoConfirm = true
            }
            
            val newId = newUser.id
            adminDb.from("users").insert(user.copy(id = newId))
            adminDb.from("faculty").insert(faculty.copy(userId = newId))
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("MainRepository", "Add faculty failed", e)
            Result.failure(e)
        }
    }

    suspend fun deleteFaculty(userId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            adminDb.from("faculty").delete { filter { eq("user_id", userId) } }
            adminDb.from("users").delete { filter { eq("id", userId) } }
            adminAuth.deleteUser(userId)
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("MainRepository", "Delete faculty failed", e)
            Result.failure(e)
        }
    }

    suspend fun uploadProfileImage(userId: String, file: File, role: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val bucket = if (role == "student") "student_profiles" else "faculty_profiles"
            val path = "profiles/${userId}_${System.currentTimeMillis()}.${file.extension}"
            adminStorage.from(bucket).upload(path, file.readBytes()) { upsert = true }
            val publicUrl = adminStorage.from(bucket).publicUrl(path)
            
            if (role == "student") {
                adminDb.from("students").update({ set("profile_url", publicUrl) }) { filter { eq("user_id", userId) } }
            } else {
                adminDb.from("faculty").update({ set("profile_url", publicUrl) }) { filter { eq("user_id", userId) } }
            }
            
            Result.success(publicUrl)
        } catch (e: Exception) { Result.failure(e) }
    }

    // --- Study Materials ---
    suspend fun getStudyMaterials(): List<StudyMaterial> = withContext(Dispatchers.IO) {
        try {
            db.from("study_materials").select().decodeList<StudyMaterial>()
        } catch (e: Exception) {
            Log.e("MainRepository", "Study materials fetch failed: ${e.message}")
            emptyList()
        }
    }

    suspend fun uploadStudyFile(file: File): Result<String> = withContext(Dispatchers.IO) {
        try {
            val path = "study_materials/${System.currentTimeMillis()}_${file.name}"
            adminStorage.from("study materials").upload(path, file.readBytes()) { upsert = true }
            val publicUrl = adminStorage.from("study materials").publicUrl(path)
            Result.success(publicUrl)
        } catch (e: Exception) {
            Log.e("MainRepository", "Study file upload failed: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun createStudyMaterial(material: StudyMaterial): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            adminDb.from("study_materials").insert(material)
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("MainRepository", "Study material creation failed: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun uploadStudyMaterial(title: String, subject: String, batch: String, dept: String, file: File, userId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val uploadResult = uploadStudyFile(file)
            if (uploadResult.isSuccess) {
                val material = StudyMaterial(
                    title = title,
                    subject = subject,
                    batch = batch,
                    department = dept,
                    fileUrl = uploadResult.getOrNull(),
                    uploadedBy = userId
                )
                createStudyMaterial(material)
            } else {
                Result.failure(Exception("File upload failed"))
            }
        } catch (e: Exception) { Result.failure(e) }
    }
}
