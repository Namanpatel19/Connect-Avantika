package com.example.myapplication.repository

import android.util.Log
import com.example.myapplication.SupabaseClient
import com.example.myapplication.data.*
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.io.File
import java.util.UUID

class MainRepository {
    private val db = SupabaseClient.client.postgrest
    private val storage = SupabaseClient.client.storage
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

    suspend fun getAllUsers(): List<User> = withContext(Dispatchers.IO) {
        try {
            adminDb.from("users").select().decodeList<User>()
        } catch (e: Exception) {
            Log.e("MainRepository", "All users fetch failed: ${e.message}")
            emptyList()
        }
    }

    suspend fun addDean(user: User): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            adminDb.from("users").insert(user.copy(role = "dean"))
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("MainRepository", "Add dean failed: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun deleteDean(userId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            adminDb.from("users").delete { filter { eq("id", userId) } }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("MainRepository", "Delete dean failed: ${e.message}")
            Result.failure(e)
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
            adminDb.from("clubs").update(buildJsonObject { put("club_head_id", userId) }) { 
                filter { eq("id", clubId) } 
            }
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
                    adminDb.from("club_requests").update(buildJsonObject {
                        put("status", "pending")
                        put("interview_date", null as String?)
                        put("interview_time", null as String?)
                        put("venue", null as String?)
                    }) {
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
            adminDb.from("club_requests").update(buildJsonObject {
                put("status", status)
                if (date != null) put("interview_date", date)
                if (time != null) put("interview_time", time)
                if (venue != null) put("venue", venue)
            }) { 
                filter { eq("id", requestId) } 
            }
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
            val newEventId = UUID.randomUUID().toString()
            val eventToInsert = event.copy(id = newEventId, status = "pending")
            adminDb.from("events").insert(eventToInsert)
            if (deanId != null) {
                val approval = EventApproval(
                    id = UUID.randomUUID().toString(),
                    eventId = newEventId,
                    deanId = deanId,
                    status = "pending"
                )
                adminDb.from("event_approvals").insert(approval)
            }
            Result.success(Unit)
        } catch (e: Exception) { 
            Log.e("MainRepository", "Create event failed: ${e.message}", e)
            Result.failure(e) 
        }
    }

    suspend fun updateEventStatus(eventId: String, status: String, deanId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            adminDb.from("events").update(buildJsonObject { put("status", status) }) { 
                filter { eq("id", eventId) } 
            }
            adminDb.from("event_approvals").update(buildJsonObject { put("status", status) }) {
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

    // --- Other methods ---
    suspend fun getStudentProfile(userId: String): Student? = withContext(Dispatchers.IO) { try { adminDb.from("students").select { filter { eq("user_id", userId) } }.decodeSingleOrNull<Student>() } catch (e: Exception) { null } }
    suspend fun getFacultyProfile(userId: String): Faculty? = withContext(Dispatchers.IO) { try { adminDb.from("faculty").select { filter { eq("user_id", userId) } }.decodeSingleOrNull<Faculty>() } catch (e: Exception) { null } }
    suspend fun uploadProfileImage(userId: String, file: File, role: String): Result<String> = withContext(Dispatchers.IO) { try { val bucket = "student photo"; val path = "profiles/${userId}_${System.currentTimeMillis()}.${file.extension}"; adminStorage.from(bucket).upload(path, file.readBytes()) { upsert = true }; val publicUrl = adminStorage.from(bucket).publicUrl(path); if (role == "student") adminDb.from("students").update(buildJsonObject { put("photo_url", publicUrl) }) { filter { eq("user_id", userId) } } else adminDb.from("faculty").update(buildJsonObject { put("photo_url", publicUrl) }) { filter { eq("user_id", userId) } }; Result.success(publicUrl) } catch (e: Exception) { Log.e("MainRepository", "Upload profile image failed: ${e.message}"); Result.failure(e) } }
    suspend fun uploadEventBanner(file: File): Result<String> = withContext(Dispatchers.IO) { try { val path = "banners/event_${System.currentTimeMillis()}.${file.extension}"; adminStorage.from("event banner").upload(path, file.readBytes()) { upsert = true }; val publicUrl = adminStorage.from("event banner").publicUrl(path); Result.success(publicUrl) } catch (e: Exception) { Result.failure(e) } }
    suspend fun uploadClubBanner(file: File): Result<String> = withContext(Dispatchers.IO) { try { val path = "banners/club_${System.currentTimeMillis()}.${file.extension}"; adminStorage.from("club banner").upload(path, file.readBytes()) { upsert = true }; val publicUrl = adminStorage.from("club banner").publicUrl(path); Result.success(publicUrl) } catch (e: Exception) { Result.failure(e) } }
    suspend fun getClubByHeadId(userId: String): Club? = withContext(Dispatchers.IO) { try { adminDb.from("clubs").select { filter { eq("club_head_id", userId) } }.decodeSingleOrNull<Club>() } catch (e: Exception) { null } }
    suspend fun getClubMembers(clubId: String): List<ClubMember> = withContext(Dispatchers.IO) { try { adminDb.from("club_members").select { filter { eq("club_id", clubId) } }.decodeList<ClubMember>() } catch (e: Exception) { emptyList() } }
    suspend fun getEventsByClubId(clubId: String): List<Event> = withContext(Dispatchers.IO) { try { adminDb.from("events").select { filter { eq("club_id", clubId) } }.decodeList<Event>() } catch (e: Exception) { emptyList() } }
    suspend fun registerForEvent(reg: EventRegistration): Result<Unit> = withContext(Dispatchers.IO) { try { adminDb.from("event_registrations").insert(reg); Result.success(Unit) } catch (e: Exception) { Result.failure(e) } }
    suspend fun getEventRegistrations(eventId: String): List<EventRegistration> = withContext(Dispatchers.IO) { try { adminDb.from("event_registrations").select { filter { eq("event_id", eventId) } }.decodeList<EventRegistration>() } catch (e: Exception) { emptyList() } }
    suspend fun deleteEvent(id: String): Result<Unit> = withContext(Dispatchers.IO) { try { adminDb.from("events").delete { filter { eq("id", id) } }; Result.success(Unit) } catch (e: Exception) { Result.failure(e) } }
    suspend fun getEventsForDean(deanId: String): List<Event> = withContext(Dispatchers.IO) { try { val approvals = adminDb.from("event_approvals").select { filter { eq("dean_id", deanId); eq("status", "pending") } }.decodeList<EventApproval>(); val ids = approvals.map { it.eventId }; if (ids.isEmpty()) return@withContext emptyList(); adminDb.from("events").select { filter { isIn("id", ids) } }.decodeList<Event>() } catch (e: Exception) { emptyList() } }
    suspend fun getStudentClubMemberships(studentId: String): List<ClubMember> = withContext(Dispatchers.IO) { try { adminDb.from("club_members").select { filter { eq("student_id", studentId) } }.decodeList<ClubMember>() } catch (e: Exception) { emptyList() } }
    suspend fun getStudentEventRegistrations(studentId: String): List<EventRegistration> = withContext(Dispatchers.IO) { try { adminDb.from("event_registrations").select { filter { eq("student_id", studentId) } }.decodeList<EventRegistration>() } catch (e: Exception) { emptyList() } }
    suspend fun getUserEmail(userId: String): String? = withContext(Dispatchers.IO) { try { adminDb.from("users").select { filter { eq("id", userId) } }.decodeSingleOrNull<User>()?.email } catch (e: Exception) { null } }

    // --- Ranks & Points ---
    suspend fun getLeaderboard(): List<Pair<Student, UserPoint>> = withContext(Dispatchers.IO) {
        try {
            val points = adminDb.from("user_points").select {
                order("total_points", Order.DESCENDING)
            }.decodeList<UserPoint>()
            
            val students = adminDb.from("students").select().decodeList<Student>()
            val studentMap = students.associateBy { it.userId }
            
            points.mapNotNull { point ->
                studentMap[point.userId]?.let { student ->
                    Pair(student, point)
                }
            }
        } catch (e: Exception) {
            Log.e("MainRepository", "Leaderboard fetch failed: ${e.message}")
            emptyList()
        }
    }

    // --- Notifications ---
    suspend fun sendNotification(userId: String, title: String, message: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            adminDb.from("notifications").insert(Notification(userId = userId, title = title, message = message))
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("MainRepository", "Send notification failed: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun getNotifications(userId: String): List<Notification> = withContext(Dispatchers.IO) {
        try {
            adminDb.from("notifications").select {
                filter { eq("user_id", userId) }
                order("created_at", Order.DESCENDING)
            }.decodeList<Notification>()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // --- Students ---
    suspend fun getAllStudents(): List<Student> = withContext(Dispatchers.IO) {
        try {
            adminDb.from("students").select().decodeList<Student>()
        } catch (e: Exception) {
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
            emptyList()
        }
    }

    suspend fun addStudent(user: User, student: Student): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            adminDb.from("users").insert(user)
            adminDb.from("students").insert(student)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteStudent(userId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            adminDb.from("users").delete { filter { eq("id", userId) } }
            adminDb.from("students").delete { filter { eq("user_id", userId) } }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateStudentProfile(student: Student): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            adminDb.from("students").update(student) { filter { eq("user_id", student.userId) } }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- Faculty ---
    suspend fun getAllFaculty(): List<Faculty> = withContext(Dispatchers.IO) {
        try {
            adminDb.from("faculty").select().decodeList<Faculty>()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addFaculty(user: User, faculty: Faculty): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            adminDb.from("users").insert(user)
            adminDb.from("faculty").insert(faculty)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteFaculty(userId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            adminDb.from("users").delete { filter { eq("id", userId) } }
            adminDb.from("faculty").delete { filter { eq("user_id", userId) } }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateFacultyProfile(faculty: Faculty): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            adminDb.from("faculty").update(faculty) { filter { eq("user_id", faculty.userId) } }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- Announcements ---
    suspend fun getAnnouncements(): List<Announcement> = withContext(Dispatchers.IO) {
        try {
            db.from("announcements").select {
                order("created_at", Order.DESCENDING)
            }.decodeList<Announcement>()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun createAnnouncement(ann: Announcement): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            adminDb.from("announcements").insert(ann)
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

    // --- Club Requests & Members ---
    suspend fun getClubRequests(clubId: String): List<ClubRequest> = withContext(Dispatchers.IO) {
        try {
            adminDb.from("club_requests").select {
                filter { eq("club_id", clubId) }
            }.decodeList<ClubRequest>()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getUserClubRequests(userId: String): List<ClubRequest> = withContext(Dispatchers.IO) {
        try {
            adminDb.from("club_requests").select {
                filter { eq("student_id", userId) }
            }.decodeList<ClubRequest>()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addClubMember(clubId: String, studentId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            adminDb.from("club_members").insert(ClubMember(clubId = clubId, studentId = studentId))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteClubMember(clubId: String, studentId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            adminDb.from("club_members").delete {
                filter {
                    eq("club_id", clubId)
                    eq("student_id", studentId)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- Study Materials ---
    suspend fun getStudyMaterials(): List<StudyMaterial> = withContext(Dispatchers.IO) {
        try {
            db.from("study_materials").select().decodeList<StudyMaterial>()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun uploadStudyFile(file: File): Result<String> = withContext(Dispatchers.IO) {
        try {
            val path = "materials/${System.currentTimeMillis()}_${file.name}"
            adminStorage.from("study materials").upload(path, file.readBytes()) { upsert = true }
            val publicUrl = adminStorage.from("study materials").publicUrl(path)
            Result.success(publicUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createStudyMaterial(material: StudyMaterial): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            adminDb.from("study_materials").insert(material)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun uploadStudyMaterial(title: String, subject: String, batch: String, department: String, file: File, userId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val uploadResult = uploadStudyFile(file)
            if (uploadResult.isSuccess) {
                val material = StudyMaterial(
                    title = title,
                    subject = subject,
                    fileUrl = uploadResult.getOrNull(),
                    uploadedBy = userId
                )
                createStudyMaterial(material)
            } else {
                Result.failure(uploadResult.exceptionOrNull() ?: Exception("Upload failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
