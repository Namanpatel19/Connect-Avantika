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
import java.util.UUID

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
            val result = adminDb.from("users").select {
                filter { 
                    or {
                        eq("role", "dean")
                        eq("role", "Dean")
                    }
                }
            }.decodeList<User>()
            Log.d("MainRepository", "getAllDeans: found ${result.size} deans")
            result
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
        } catch (e: Exception) { 
            Log.e("MainRepository", "Create club failed: ${e.message}")
            Result.failure(e) 
        }
    }

    suspend fun updateClub(club: Club): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            adminDb.from("clubs").update(club) { filter { eq("id", club.id!!) } }
            Result.success(Unit)
        } catch (e: Exception) { 
            Log.e("MainRepository", "Update club failed: ${e.message}")
            Result.failure(e) 
        }
    }

    suspend fun deleteClub(id: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            adminDb.from("clubs").delete { filter { eq("id", id) } }
            Result.success(Unit)
        } catch (e: Exception) { 
            Log.e("MainRepository", "Delete club failed: ${e.message}")
            Result.failure(e) 
        }
    }

    suspend fun updateClubLead(clubId: String, userId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            adminDb.from("clubs").update(mapOf("club_head_id" to userId)) { filter { eq("id", clubId) } }
            Result.success(Unit)
        } catch (e: Exception) { 
            Log.e("MainRepository", "Update club lead failed: ${e.message}")
            Result.failure(e) 
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
            Log.e("MainRepository", "Join club failed: ${e.message}", e)
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
        } catch (e: Exception) { 
            Log.e("MainRepository", "Update club request status failed: ${e.message}")
            Result.failure(e) 
        }
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

    suspend fun getApprovedEvents(): List<Event> = withContext(Dispatchers.IO) {
        try {
            db.from("events").select {
                filter { eq("status", "approved") }
            }.decodeList<Event>()
        } catch (e: Exception) { emptyList() }
    }

    suspend fun createEvent(event: Event, deanId: String?): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // 1. Generate ID locally
            val newEventId = UUID.randomUUID().toString()
            
            // Map the Kotlin Event model to a database map
            val eventMap = mutableMapOf<String, Any?>(
                "id" to newEventId,
                "title" to event.title,
                "description" to event.description,
                "club_id" to event.clubId,
                "created_by" to event.createdBy,
                "status" to "pending",
                "event_date" to event.eventDate,
                "banner_url" to event.bannerUrl
            )
            
            Log.d("MainRepository", "Inserting event: $eventMap")
            
            // 2. Insert the event
            adminDb.from("events").insert(eventMap)
            
            // 3. Create entry in event_approvals
            if (deanId != null) {
                // Use a list of map to bypass potential 'columns' query param issues in some SDK versions
                val approvalData = listOf(mapOf(
                    "id" to UUID.randomUUID().toString(),
                    "event_id" to newEventId,
                    "dean_id" to deanId,
                    "status" to "pending"
                ))
                Log.d("MainRepository", "Inserting approval list: $approvalData")
                adminDb.from("event_approvals").insert(approvalData)
            }
            
            Result.success(Unit)
        } catch (e: Exception) { 
            Log.e("MainRepository", "Create event failed: ${e.message}", e)
            Result.failure(e) 
        }
    }

    suspend fun updateEventStatus(eventId: String, status: String, deanId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // Update the master status in events table
            adminDb.from("events").update(mapOf("status" to status)) { 
                filter { eq("id", eventId) } 
            }
            
            // Update the record in event_approvals table
            adminDb.from("event_approvals").update(mapOf("status" to status)) {
                filter { 
                    eq("event_id", eventId)
                    eq("dean_id", deanId)
                }
            }
            
            Result.success(Unit)
        } catch (e: Exception) { 
            Log.e("MainRepository", "Update event status failed: ${e.message}")
            Result.failure(e) 
        }
    }

    // --- Other methods (getStudentProfile, etc.) ---
    suspend fun getStudentProfile(userId: String): Student? = withContext(Dispatchers.IO) { try { adminDb.from("students").select { filter { eq("user_id", userId) } }.decodeSingleOrNull<Student>() } catch (e: Exception) { null } }
    suspend fun getFacultyProfile(userId: String): Faculty? = withContext(Dispatchers.IO) { try { adminDb.from("faculty").select { filter { eq("user_id", userId) } }.decodeSingleOrNull<Faculty>() } catch (e: Exception) { null } }
    suspend fun uploadProfileImage(userId: String, file: File, role: String): Result<String> = withContext(Dispatchers.IO) { try { val bucket = "student photo"; val path = "profiles/${userId}_${System.currentTimeMillis()}.${file.extension}"; adminStorage.from(bucket).upload(path, file.readBytes()) { upsert = true }; val publicUrl = adminStorage.from(bucket).publicUrl(path); if (role == "student") adminDb.from("students").update(mapOf("photo_url" to publicUrl)) { filter { eq("user_id", userId) } } else adminDb.from("faculty").update(mapOf("photo_url" to publicUrl)) { filter { eq("user_id", userId) } }; Result.success(publicUrl) } catch (e: Exception) { Log.e("MainRepository", "Upload profile image failed: ${e.message}"); Result.failure(e) } }
    suspend fun uploadEventBanner(file: File): Result<String> = withContext(Dispatchers.IO) { try { val path = "banners/event_${System.currentTimeMillis()}.${file.extension}"; adminStorage.from("event banner").upload(path, file.readBytes()) { upsert = true }; val publicUrl = adminStorage.from("event banner").publicUrl(path); Result.success(publicUrl) } catch (e: Exception) { Result.failure(e) } }
    suspend fun uploadClubBanner(file: File): Result<String> = withContext(Dispatchers.IO) { try { val path = "banners/club_${System.currentTimeMillis()}.${file.extension}"; adminStorage.from("club banner").upload(path, file.readBytes()) { upsert = true }; val publicUrl = adminStorage.from("club banner").publicUrl(path); Result.success(publicUrl) } catch (e: Exception) { Result.failure(e) } }
    suspend fun getClubByHeadId(userId: String): Club? = withContext(Dispatchers.IO) { try { adminDb.from("clubs").select { filter { eq("club_head_id", userId) } }.decodeSingleOrNull<Club>() } catch (e: Exception) { Log.e("MainRepository", "getClubByHeadId failed: ${e.message}"); null } }
    suspend fun getClubMembers(clubId: String): List<ClubMember> = withContext(Dispatchers.IO) { try { adminDb.from("club_members").select { filter { eq("club_id", clubId) } }.decodeList<ClubMember>() } catch (e: Exception) { emptyList() } }
    suspend fun getEventsByClubId(clubId: String): List<Event> = withContext(Dispatchers.IO) { try { db.from("events").select { filter { eq("club_id", clubId) } }.decodeList<Event>() } catch (e: Exception) { emptyList() } }
    suspend fun getClubRequests(clubId: String): List<ClubRequest> = withContext(Dispatchers.IO) { try { adminDb.from("club_requests").select { filter { eq("club_id", clubId) } }.decodeList<ClubRequest>() } catch (e: Exception) { emptyList() } }
    suspend fun getUserClubRequests(studentId: String): List<ClubRequest> = withContext(Dispatchers.IO) { try { adminDb.from("club_requests").select { filter { eq("student_id", studentId) } }.decodeList<ClubRequest>() } catch (e: Exception) { emptyList() } }
    suspend fun getNotifications(userId: String): List<Notification> = withContext(Dispatchers.IO) { try { db.from("notifications").select { filter { eq("user_id", userId) } }.decodeList<Notification>() } catch (e: Exception) { emptyList() } }
    suspend fun getAnnouncements(): List<Announcement> = withContext(Dispatchers.IO) { try { db.from("announcements").select().decodeList<Announcement>() } catch (e: Exception) { emptyList() } }
    suspend fun getStudyMaterials(): List<StudyMaterial> = withContext(Dispatchers.IO) { try { db.from("study_materials").select().decodeList<StudyMaterial>() } catch (e: Exception) { emptyList() } }
    suspend fun uploadStudyFile(file: File): Result<String> = withContext(Dispatchers.IO) { try { val path = "study_materials/${System.currentTimeMillis()}_${file.name}"; adminStorage.from("study material").upload(path, file.readBytes()) { upsert = true }; val publicUrl = adminStorage.from("study material").publicUrl(path); Result.success(publicUrl) } catch (e: Exception) { Log.e("MainRepository", "Upload study file failed: ${e.message}"); Result.failure(e) } }
    suspend fun createStudyMaterial(material: StudyMaterial): Result<Unit> = withContext(Dispatchers.IO) { try { adminDb.from("study_materials").insert(material); Result.success(Unit) } catch (e: Exception) { Result.failure(e) } }
    suspend fun uploadStudyMaterial(title: String, subject: String, batch: String, department: String, file: File, userId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val uploadResult = uploadStudyFile(file)
            if (uploadResult.isSuccess) {
                val fileUrl = uploadResult.getOrThrow()
                createStudyMaterial(StudyMaterial(title = title, subject = subject, fileUrl = fileUrl, uploadedBy = userId))
            } else {
                Result.failure(uploadResult.exceptionOrNull() ?: Exception("Upload failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun sendNotification(userId: String, title: String, message: String): Result<Unit> = withContext(Dispatchers.IO) { try { adminDb.from("notifications").insert(Notification(userId = userId, title = title, message = message)); Result.success(Unit) } catch (e: Exception) { Result.failure(e) } }
    suspend fun addClubMember(clubId: String, studentId: String): Result<Unit> = withContext(Dispatchers.IO) { try { adminDb.from("club_members").insert(ClubMember(clubId = clubId, studentId = studentId)); Result.success(Unit) } catch (e: Exception) { Result.failure(e) } }
    suspend fun updateStudentProfile(student: Student): Result<Unit> = withContext(Dispatchers.IO) { try { adminDb.from("students").update(student) { filter { eq("user_id", student.userId) } }; Result.success(Unit) } catch (e: Exception) { Result.failure(e) } }
    suspend fun updateFacultyProfile(faculty: Faculty): Result<Unit> = withContext(Dispatchers.IO) { try { adminDb.from("faculty").update(faculty) { filter { eq("user_id", faculty.userId) } }; Result.success(Unit) } catch (e: Exception) { Result.failure(e) } }
    suspend fun getAllStudents(): List<Student> = withContext(Dispatchers.IO) { try { adminDb.from("students").select().decodeList<Student>() } catch (e: Exception) { emptyList() } }
    suspend fun getAllFaculty(): List<Faculty> = withContext(Dispatchers.IO) { try { adminDb.from("faculty").select().decodeList<Faculty>() } catch (e: Exception) { emptyList() } }
    suspend fun deleteStudent(userId: String): Result<Unit> = withContext(Dispatchers.IO) { try { adminDb.from("students").delete { filter { eq("user_id", userId) } }; Result.success(Unit) } catch (e: Exception) { Result.failure(e) } }
    suspend fun deleteFaculty(userId: String): Result<Unit> = withContext(Dispatchers.IO) { try { adminDb.from("faculty").delete { filter { eq("user_id", userId) } }; Result.success(Unit) } catch (e: Exception) { Result.failure(e) } }
    suspend fun searchStudents(query: String): List<Student> = withContext(Dispatchers.IO) { try { adminDb.from("students").select { filter { or { ilike("name", "%$query%"); ilike("enrollment", "%$query%") } } }.decodeList<Student>() } catch (e: Exception) { emptyList() } }
    suspend fun addStudent(user: User, student: Student): Result<Unit> = withContext(Dispatchers.IO) { try { adminDb.from("users").insert(user); adminDb.from("students").insert(student); Result.success(Unit) } catch (e: Exception) { Result.failure(e) } }
    suspend fun addFaculty(user: User, faculty: Faculty): Result<Unit> = withContext(Dispatchers.IO) { try { adminDb.from("users").insert(user); adminDb.from("faculty").insert(faculty); Result.success(Unit) } catch (e: Exception) { Result.failure(e) } }
    suspend fun createAnnouncement(ann: Announcement): Result<Unit> = withContext(Dispatchers.IO) { try { adminDb.from("announcements").insert(ann); Result.success(Unit) } catch (e: Exception) { Result.failure(e) } }
    suspend fun deleteAnnouncement(id: String): Result<Unit> = withContext(Dispatchers.IO) { try { adminDb.from("announcements").delete { filter { eq("id", id) } }; Result.success(Unit) } catch (e: Exception) { Result.failure(e) } }
    suspend fun registerForEvent(reg: EventRegistration): Result<Unit> = withContext(Dispatchers.IO) { try { adminDb.from("event_registrations").insert(reg); Result.success(Unit) } catch (e: Exception) { Result.failure(e) } }
    suspend fun getEventRegistrations(eventId: String): List<EventRegistration> = withContext(Dispatchers.IO) { try { adminDb.from("event_registrations").select { filter { eq("event_id", eventId) } }.decodeList<EventRegistration>() } catch (e: Exception) { emptyList() } }
    suspend fun deleteEvent(id: String): Result<Unit> = withContext(Dispatchers.IO) { try { adminDb.from("events").delete { filter { eq("id", id) } }; Result.success(Unit) } catch (e: Exception) { Result.failure(e) } }
    suspend fun getEventsForDean(deanId: String): List<Event> = withContext(Dispatchers.IO) { try { val approvals = adminDb.from("event_approvals").select { filter { eq("dean_id", deanId); eq("status", "pending") } }.decodeList<EventApproval>(); val ids = approvals.map { it.eventId }; if (ids.isEmpty()) return@withContext emptyList(); adminDb.from("events").select { filter { isIn("id", ids) } }.decodeList<Event>() } catch (e: Exception) { emptyList() } }
    suspend fun getStudentClubMemberships(studentId: String): List<ClubMember> = withContext(Dispatchers.IO) { try { adminDb.from("club_members").select { filter { eq("student_id", studentId) } }.decodeList<ClubMember>() } catch (e: Exception) { emptyList() } }
    suspend fun getStudentEventRegistrations(studentId: String): List<EventRegistration> = withContext(Dispatchers.IO) { try { adminDb.from("event_registrations").select { filter { eq("student_id", studentId) } }.decodeList<EventRegistration>() } catch (e: Exception) { emptyList() } }
    suspend fun getUserEmail(userId: String): String? = withContext(Dispatchers.IO) { try { adminDb.from("users").select { filter { eq("id", userId) } }.decodeSingleOrNull<User>()?.email } catch (e: Exception) { null } }
}
