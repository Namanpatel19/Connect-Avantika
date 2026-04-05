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
        } catch (e: Exception) { null }
    }

    // --- Clubs & Join Logic ---
    suspend fun getClubs(): List<Club> = withContext(Dispatchers.IO) {
        try {
            db.from("clubs").select().decodeList<Club>()
        } catch (e: Exception) { emptyList() }
    }

    suspend fun getClubById(clubId: String): Club? = withContext(Dispatchers.IO) {
        try {
            db.from("clubs").select {
                filter { eq("id", clubId) }
            }.decodeSingleOrNull<Club>()
        } catch (e: Exception) { null }
    }

    suspend fun joinClub(clubId: String, studentId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val existing = adminDb.from("club_requests").select {
                filter {
                    eq("club_id", clubId)
                    eq("student_id", studentId)
                }
            }.decodeSingleOrNull<ClubRequest>()

            if (existing != null) {
                if (existing.status == "rejected") {
                    // If previously rejected, allow re-applying by resetting the status to pending
                    adminDb.from("club_requests").update({
                        set("status", "pending")
                        set<String?>("interview_date", null)
                        set<String?>("interview_time", null)
                        set<String?>("interview_venue", null)
                    }) {
                        filter { eq("id", existing.id!!) }
                    }
                    return@withContext Result.success(Unit)
                }
                return@withContext Result.failure(Exception("Already applied"))
            }

            adminDb.from("club_requests").insert(ClubRequest(clubId = clubId, studentId = studentId))
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun getUserClubRequests(studentId: String): List<ClubRequest> = withContext(Dispatchers.IO) {
        try {
            adminDb.from("club_requests").select {
                filter { eq("student_id", studentId) }
            }.decodeList<ClubRequest>()
        } catch (e: Exception) { emptyList() }
    }

    suspend fun getClubRequests(clubId: String): List<ClubRequest> = withContext(Dispatchers.IO) {
        try {
            adminDb.from("club_requests").select {
                filter { eq("club_id", clubId) }
                order("requested_at", Order.DESCENDING)
            }.decodeList<ClubRequest>()
        } catch (e: Exception) { emptyList() }
    }

    suspend fun updateClubRequestStatus(
        requestId: String,
        status: String,
        date: String? = null,
        time: String? = null,
        venue: String? = null
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            adminDb.from("club_requests").update({
                set("status", status)
                date?.let { set("interview_date", it) }
                time?.let { set("interview_time", it) }
                venue?.let { set("interview_venue", it) }
            }) { filter { eq("id", requestId) } }
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun deleteClubRequest(requestId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            adminDb.from("club_requests").delete { filter { eq("id", requestId) } }
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun addClubMember(clubId: String, studentId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            adminDb.from("club_members").insert(ClubMember(clubId = clubId, studentId = studentId))
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun getClubMembers(clubId: String): List<Student> = withContext(Dispatchers.IO) {
        try {
            val members = adminDb.from("club_members").select {
                filter { eq("club_id", clubId) }
            }.decodeList<ClubMember>()
            
            val memberIds = members.map { it.studentId }
            if (memberIds.isEmpty()) return@withContext emptyList()
            
            adminDb.from("students").select {
                filter { isIn("user_id", memberIds) }
            }.decodeList<Student>()
        } catch (e: Exception) { emptyList() }
    }

    suspend fun getClubByHeadId(userId: String): Club? = withContext(Dispatchers.IO) {
        try {
            adminDb.from("clubs").select {
                filter { eq("club_head_id", userId) }
            }.decodeSingleOrNull<Club>()
        } catch (e: Exception) { null }
    }

    suspend fun assignClubLead(clubId: String, leadEmail: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val user = getUserByEmail(leadEmail) ?: return@withContext Result.failure(Exception("User not found"))
            adminDb.from("users").update({ set("role", "club_head") }) { filter { eq("id", user.id) } }
            adminDb.from("clubs").update({ set("club_head_id", user.id) }) { filter { eq("id", clubId) } }
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    // --- Notifications ---
    suspend fun sendNotification(userId: String, title: String, message: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            adminDb.from("notifications").insert(Notification(userId = userId, title = title, message = message))
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun getNotifications(userId: String): List<Notification> = withContext(Dispatchers.IO) {
        try {
            db.from("notifications").select {
                filter { eq("user_id", userId) }
                order("created_at", Order.DESCENDING)
            }.decodeList<Notification>()
        } catch (e: Exception) { emptyList() }
    }

    // --- Standard Methods ---
    suspend fun getAllUsers(): List<User> = withContext(Dispatchers.IO) { try { adminDb.from("users").select().decodeList<User>() } catch (e: Exception) { emptyList() } }
    suspend fun getAllEvents(): List<Event> = withContext(Dispatchers.IO) { try { db.from("events").select().decodeList<Event>() } catch (e: Exception) { emptyList() } }
    suspend fun getEvents(status: String? = "approved"): List<Event> = withContext(Dispatchers.IO) { try { db.from("events").select { status?.let { filter { eq("status", it) } } }.decodeList<Event>() } catch (e: Exception) { emptyList() } }
    suspend fun createEvent(event: Event): Result<Unit> = withContext(Dispatchers.IO) { try { val database = if (event.status == "approved") adminDb else db; database.from("events").insert(event); Result.success(Unit) } catch (e: Exception) { Result.failure(e) } }
    suspend fun updateEventStatus(eventId: String, status: String): Result<Unit> = withContext(Dispatchers.IO) { try { adminDb.from("events").update({ set("status", status) }) { filter { eq("id", eventId) } }; Result.success(Unit) } catch (e: Exception) { Result.failure(e) } }
    suspend fun deleteEvent(eventId: String): Result<Unit> = withContext(Dispatchers.IO) { try { adminDb.from("events").delete { filter { eq("id", eventId) } }; Result.success(Unit) } catch (e: Exception) { Result.failure(e) } }
    suspend fun uploadEventBanner(file: File): Result<String> = withContext(Dispatchers.IO) { try { val path = "banners/event_${System.currentTimeMillis()}.${file.extension}"; adminStorage.from("event banner").upload(path, file.readBytes()) { upsert = true }; val publicUrl = adminStorage.from("event banner").publicUrl(path); Result.success(publicUrl) } catch (e: Exception) { Result.failure(e) } }
    suspend fun createClub(club: Club): Result<Unit> = withContext(Dispatchers.IO) { try { adminDb.from("clubs").insert(club); Result.success(Unit) } catch (e: Exception) { Result.failure(e) } }
    suspend fun updateClub(club: Club): Result<Unit> = withContext(Dispatchers.IO) { try { adminDb.from("clubs").update(club) { filter { eq("id", club.id!!) } }; Result.success(Unit) } catch (e: Exception) { Result.failure(e) } }
    suspend fun deleteClub(clubId: String): Result<Unit> = withContext(Dispatchers.IO) { try { adminDb.from("clubs").delete { filter { eq("id", clubId) } }; Result.success(Unit) } catch (e: Exception) { Result.failure(e) } }
    suspend fun getEventsByClubId(clubId: String): List<Event> = withContext(Dispatchers.IO) { try { db.from("events").select { filter { eq("club_id", clubId) } }.decodeList<Event>() } catch (e: Exception) { emptyList() } }
    suspend fun uploadClubBanner(clubId: String, file: File): Result<String> = withContext(Dispatchers.IO) { try { val path = "banners/${clubId}_${System.currentTimeMillis()}.${file.extension}"; adminStorage.from("club banner").upload(path, file.readBytes()) { upsert = true }; val publicUrl = adminStorage.from("club banner").publicUrl(path); adminDb.from("clubs").update({ set("banner_url", publicUrl) }) { filter { eq("id", clubId) } }; Result.success(publicUrl) } catch (e: Exception) { Result.failure(e) } }
    suspend fun getAnnouncements(): List<Announcement> = withContext(Dispatchers.IO) { try { db.from("announcements").select().decodeList<Announcement>() } catch (e: Exception) { emptyList() } }
    suspend fun createAnnouncement(announcement: Announcement): Result<Unit> = withContext(Dispatchers.IO) { try { adminDb.from("announcements").insert(announcement); Result.success(Unit) } catch (e: Exception) { Result.failure(e) } }
    suspend fun deleteAnnouncement(id: String): Result<Unit> = withContext(Dispatchers.IO) { try { adminDb.from("announcements").delete { filter { eq("id", id) } }; Result.success(Unit) } catch (e: Exception) { Result.failure(e) } }
    suspend fun getStudyMaterials(): List<StudyMaterial> = withContext(Dispatchers.IO) { try { adminDb.from("study_materials").select().decodeList<StudyMaterial>() } catch (e: Exception) { emptyList() } }
    suspend fun getStudyMaterialsByFilter(batch: String? = null, department: String? = null): List<StudyMaterial> = withContext(Dispatchers.IO) { try { adminDb.from("study_materials").select { filter { batch?.let { eq("batch", it) }; department?.let { eq("department", it) } } }.decodeList<StudyMaterial>() } catch (e: Exception) { emptyList() } }
    suspend fun uploadStudyMaterial(title: String, subject: String, batch: String, department: String, file: File, facultyId: String): Result<Unit> = withContext(Dispatchers.IO) { try { val fileName = "${System.currentTimeMillis()}_${file.name}"; val path = "materials/$fileName"; adminStorage.from("study_materials").upload(path, file.readBytes()) { upsert = true }; val publicUrl = adminStorage.from("study_materials").publicUrl(path); val material = StudyMaterial(title = title, subject = subject, batch = batch, department = department, fileUrl = publicUrl, fileName = file.name, facultyId = facultyId); adminDb.from("study_materials").insert(material); Result.success(Unit) } catch (e: Exception) { Result.failure(e) } }
    suspend fun getAllStudents(): List<Student> = withContext(Dispatchers.IO) { try { adminDb.from("students").select().decodeList<Student>() } catch (e: Exception) { emptyList() } }
    suspend fun addStudent(user: User, student: Student, autoConfirm: Boolean = true): Result<Unit> = withContext(Dispatchers.IO) { try { val newUser = adminAuth.createUserWithEmail { this.email = user.email; this.password = user.password ?: "Welcome@123"; this.autoConfirm = autoConfirm }; val userId = newUser.id; adminDb.from("users").insert(user.copy(id = userId, password = null)); adminDb.from("students").insert(student.copy(userId = userId)); Result.success(Unit) } catch (e: Exception) { Result.failure(e) } }
    suspend fun deleteStudent(userId: String): Result<Unit> = withContext(Dispatchers.IO) { try { adminDb.from("students").delete { filter { eq("user_id", userId) } }; adminDb.from("users").delete { filter { eq("id", userId) } }; try { adminAuth.deleteUser(userId) } catch (e: Exception) {}; Result.success(Unit) } catch (e: Exception) { Result.failure(e) } }
    suspend fun searchStudents(query: String): List<Student> = withContext(Dispatchers.IO) { try { adminDb.from("students").select { filter { or { ilike("name", "%$query%"); ilike("enrollment", "%$query%") } } }.decodeList<Student>() } catch (e: Exception) { emptyList() } }
    suspend fun getAllFaculty(): List<Faculty> = withContext(Dispatchers.IO) { try { adminDb.from("faculty").select().decodeList<Faculty>() } catch (e: Exception) { emptyList() } }
    suspend fun addFaculty(user: User, faculty: Faculty): Result<Unit> = withContext(Dispatchers.IO) { try { val newUser = adminAuth.createUserWithEmail { this.email = user.email; this.password = user.password ?: "Welcome@123"; this.autoConfirm = true }; val userId = newUser.id; adminDb.from("users").insert(user.copy(id = userId, password = null)); adminDb.from("faculty").insert(faculty.copy(userId = userId)); Result.success(Unit) } catch (e: Exception) { Result.failure(e) } }
    suspend fun deleteFaculty(userId: String): Result<Unit> = withContext(Dispatchers.IO) { try { adminDb.from("faculty").delete { filter { eq("id", userId) } }; adminDb.from("users").delete { filter { eq("id", userId) } }; try { adminAuth.deleteUser(userId) } catch (e: Exception) {}; Result.success(Unit) } catch (e: Exception) { Result.failure(e) } }
    suspend fun updateStudentProfile(student: Student): Result<Unit> = withContext(Dispatchers.IO) { try { adminDb.from("students").update(student) { filter { eq("user_id", student.userId) } }; Result.success(Unit) } catch (e: Exception) { Result.failure(e) } }
    suspend fun updateFacultyProfile(faculty: Faculty): Result<Unit> = withContext(Dispatchers.IO) { try { adminDb.from("faculty").update(faculty) { filter { eq("user_id", faculty.userId) } }; Result.success(Unit) } catch (e: Exception) { Result.failure(e) } }
    suspend fun getStudentProfile(userId: String): Student? = withContext(Dispatchers.IO) { try { adminDb.from("students").select { filter { eq("user_id", userId) } }.decodeSingleOrNull<Student>() } catch (e: Exception) { null } }
    suspend fun getFacultyProfile(userId: String): Faculty? = withContext(Dispatchers.IO) { try { adminDb.from("faculty").select { filter { eq("user_id", userId) } }.decodeSingleOrNull<Faculty>() } catch (e: Exception) { null } }
    suspend fun uploadProfileImage(userId: String, file: File, role: String): Result<String> = withContext(Dispatchers.IO) { try { val path = "profiles/${userId}_${System.currentTimeMillis()}.${file.extension}"; adminStorage.from("student photo").upload(path, file.readBytes()) { upsert = true }; val publicUrl = adminStorage.from("student photo").publicUrl(path); if (role == "student") adminDb.from("students").update({ set("photo_url", publicUrl) }) { filter { eq("user_id", userId) } } else adminDb.from("faculty").update({ set("photo_url", publicUrl) }) { filter { eq("user_id", userId) } }; Result.success(publicUrl) } catch (e: Exception) { Result.failure(e) } }
    suspend fun registerForEvent(eventId: String, studentId: String): Result<Unit> = withContext(Dispatchers.IO) { try { adminDb.from("event_registrations").insert(EventRegistration(eventId = eventId, studentId = studentId)); Result.success(Unit) } catch (e: Exception) { Result.failure(e) } }
}
