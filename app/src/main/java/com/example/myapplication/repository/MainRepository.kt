package com.example.myapplication.repository

import android.util.Log
import com.example.myapplication.BuildConfig
import com.example.myapplication.SupabaseClient
import com.example.myapplication.data.*
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonArray
import java.io.File
import java.util.UUID
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class MainRepository {
    private val db = SupabaseClient.client.postgrest
    private val adminDb = SupabaseClient.adminClient.postgrest
    private val adminStorage = SupabaseClient.adminClient.storage
    private val httpClient = OkHttpClient()

    private fun String.normalize() = this.lowercase().replace("-", "").replace(" ", "").trim()

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

    suspend fun getUserEmail(userId: String): String? = withContext(Dispatchers.IO) {
        try {
            val user = adminDb.from("users").select { filter { eq("id", userId) } }.decodeSingleOrNull<User>()
            user?.email
        } catch (e: Exception) { null }
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
            result
        } catch (e: Exception) {
            Log.e("MainRepository", "Deans fetch failed: ${e.message}")
            emptyList()
        }
    }

    suspend fun getAllUsers(): List<User> = withContext(Dispatchers.IO) {
        try {
            val authUsers = SupabaseClient.adminClient.auth.admin.retrieveUsers()
            val publicUsers = adminDb.from("users").select().decodeList<User>()
            val roleMap = publicUsers.associateBy({ it.id }, { it.role })

            authUsers.map { authUser ->
                User(
                    id = authUser.id,
                    email = authUser.email ?: "no-email",
                    role = roleMap[authUser.id] ?: "user",
                    createdAt = authUser.createdAt.toString(),
                    lastSignInAt = authUser.lastSignInAt?.toString()
                )
            }
        } catch (e: Exception) {
            Log.e("MainRepository", "All users fetch failed: ${e.message}")
            emptyList()
        }
    }

    suspend fun addDean(user: User): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val authUser = SupabaseClient.adminClient.auth.admin.createUserWithEmail {
                email = user.email
                password = user.password ?: "123456"
                autoConfirm = true
            }
            val newUser = user.copy(id = authUser.id, role = "dean")
            adminDb.from("users").upsert(newUser)
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("MainRepository", "Add dean failed", e)
            Result.failure(e)
        }
    }

    suspend fun deleteDean(userId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            adminDb.from("event_approvals").delete { filter { eq("dean_id", userId) } }
            try {
                adminDb.from("events").update(buildJsonObject { put("dean_id", null as String?) }) {
                    filter { eq("dean_id", userId) }
                }
            } catch (e: Exception) { }
            adminDb.from("users").delete { filter { eq("id", userId) } }
            SupabaseClient.adminClient.auth.admin.deleteUser(userId)
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("MainRepository", "Delete dean failed: ${e.message}", e)
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
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun updateClub(club: Club): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // Surgical update to avoid accidental nulling of fields or primary key issues
            adminDb.from("clubs").update(buildJsonObject {
                put("name", club.name)
                put("description", club.description)
                put("club_head_id", club.clubHeadId)
                put("banner_url", club.bannerUrl)
                put("category", club.category)
            }) { filter { eq("id", club.id!!) } }
            Result.success(Unit)
        } catch (e: Exception) { 
            Log.e("MainRepository", "Update club failed", e)
            Result.failure(e) 
        }
    }

    suspend fun updateClubBanner(clubId: String, bannerUrl: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            adminDb.from("clubs").update(buildJsonObject {
                put("banner_url", bannerUrl)
            }) { filter { eq("id", clubId) } }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("MainRepository", "Update club banner failed", e)
            Result.failure(e)
        }
    }

    suspend fun deleteClub(id: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            adminDb.from("club_members").delete { filter { eq("club_id", id) } }
            adminDb.from("club_requests").delete { filter { eq("club_id", id) } }
            adminDb.from("clubs").delete { filter { eq("id", id) } }
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun updateClubLead(clubId: String, userId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            adminDb.from("clubs").update(buildJsonObject { put("club_head_id", userId) }) { 
                filter { eq("id", clubId) } 
            }
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
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
                    adminDb.from("club_requests").update(buildJsonObject {
                        put("status", "pending")
                        put("interview_date", null as String?)
                        put("interview_time", null as String?)
                        put("venue", null as String?)
                    }) { filter { eq("id", existing.id!!) } }
                    return@withContext Result.success(Unit)
                }
                return@withContext Result.failure(Exception("Already applied"))
            }

            adminDb.from("club_requests").insert(ClubRequest(clubId = clubId, studentId = studentId))
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun updateClubRequestStatus(requestId: String, status: String, date: String? = null, time: String? = null, venue: String? = null): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            adminDb.from("club_requests").update(buildJsonObject {
                put("status", status)
                if (date != null) put("interview_date", date)
                if (time != null) put("interview_time", time)
                if (venue != null) put("venue", venue)
            }) { filter { eq("id", requestId) } }
            
            val req = adminDb.from("club_requests").select { filter { eq("id", requestId) } }.decodeSingleOrNull<ClubRequest>()
            req?.let {
                val club = adminDb.from("clubs").select { filter { eq("id", it.clubId) } }.decodeSingleOrNull<Club>()
                val clubName = club?.name ?: "Club"

                if (status == "accepted") {
                    addClubMember(it.clubId, it.studentId)
                    addPoints(it.studentId, 100, "club_join", it.id)
                    sendNotification(it.studentId, "Club Request Accepted", "You have been accepted into $clubName! +100 points.")
                } else if (status == "rejected") {
                    sendNotification(it.studentId, "Club Request Rejected", "Your request to join $clubName has been rejected.")
                } else if (status == "interview") {
                    sendNotification(it.studentId, "$clubName Interview Scheduled", "Interview on $date at $time at $venue.")
                }
            }
            
            Result.success(Unit)
        } catch (e: Exception) { 
            Log.e("MainRepository", "Update club request status failed: ${e.message}")
            Result.failure(e) 
        }
    }

    suspend fun getClubByHeadId(userId: String): Club? = withContext(Dispatchers.IO) {
        try { db.from("clubs").select { filter { eq("club_head_id", userId) } }.decodeSingleOrNull<Club>() } catch (e: Exception) { null }
    }

    suspend fun getClubRequests(clubId: String): List<ClubRequest> = withContext(Dispatchers.IO) {
        try { db.from("club_requests").select { filter { eq("club_id", clubId) } }.decodeList<ClubRequest>() } catch (e: Exception) { emptyList() }
    }

    suspend fun getClubMembers(clubId: String): List<ClubMember> = withContext(Dispatchers.IO) {
        try { db.from("club_members").select { filter { eq("club_id", clubId) } }.decodeList<ClubMember>() } catch (e: Exception) { emptyList() }
    }

    suspend fun getUserClubRequests(userId: String): List<ClubRequest> = withContext(Dispatchers.IO) {
        try { db.from("club_requests").select { filter { eq("student_id", userId) } }.decodeList<ClubRequest>() } catch (e: Exception) { emptyList() }
    }

    suspend fun getStudentClubMemberships(userId: String): List<ClubMember> = withContext(Dispatchers.IO) {
        try { db.from("club_members").select { filter { eq("student_id", userId) } }.decodeList<ClubMember>() } catch (e: Exception) { emptyList() }
    }

    suspend fun addClubMember(clubId: String, studentId: String) {
        try { adminDb.from("club_members").insert(buildJsonObject { put("club_id", clubId); put("student_id", studentId) }) } catch (e: Exception) {}
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
        } catch (e: Exception) { Result.failure(e) }
    }

    // --- Events ---
    suspend fun getEvents(status: String? = null): List<Event> = withContext(Dispatchers.IO) {
        try {
            db.from("events").select {
                filter {
                    if (status != null) eq("status", status)
                }
            }.decodeList<Event>()
        } catch (e: Exception) { emptyList() }
    }

    suspend fun getAllEvents(): List<Event> = getEvents()
    suspend fun getApprovedEvents(): List<Event> = getEvents("approved")
    
    suspend fun createEvent(event: Event, deanId: String?): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val newEventId = UUID.randomUUID().toString()
            
            val eventJson = buildJsonObject {
                put("id", newEventId)
                put("title", event.title)
                put("description", event.description)
                put("club_id", event.clubId)
                put("created_by", event.createdBy)
                put("status", "pending")
                if (!event.eventDate.isNullOrBlank()) put("event_date", event.eventDate)
                put("banner_url", event.bannerUrl)
                put("venue", event.venue)
                if (!event.eventTime.isNullOrBlank()) put("event_time", event.eventTime)
                if (!deanId.isNullOrBlank()) put("dean_id", deanId)
                put("entry_fee", event.entryFee)
                put("is_paid", event.isPaid)
            }
            
            adminDb.from("events").insert(eventJson)
            
            if (!deanId.isNullOrBlank()) {
                val approvalJson = buildJsonObject {
                    put("id", UUID.randomUUID().toString())
                    put("event_id", newEventId)
                    put("dean_id", deanId)
                    put("status", "pending")
                }
                adminDb.from("event_approvals").insert(approvalJson)
                
                sendNotification(deanId, "New Event Approval", "A new event '${event.title}' is waiting for your approval.")
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("MainRepository", "Create event failed: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun updateEventStatus(eventId: String, status: String, deanId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val lowerStatus = status.lowercase()
            adminDb.from("events").update(buildJsonObject { put("status", lowerStatus) }) { filter { eq("id", eventId) } }
            adminDb.from("event_approvals").update(buildJsonObject { put("status", lowerStatus) }) {
                filter { eq("event_id", eventId); eq("dean_id", deanId) }
            }

            val event = adminDb.from("events").select { filter { eq("id", eventId) } }.decodeSingleOrNull<Event>()
            event?.createdBy?.let { leadId ->
                val msg = if (lowerStatus == "approved") "Your event '${event.title}' has been approved!" else "Your event '${event.title}' was rejected."
                sendNotification(leadId, "Event Status Updated", msg)
            }

            if (lowerStatus == "approved") {
                val students = adminDb.from("students").select().decodeList<Student>()
                students.forEach { student ->
                    sendNotification(student.userId, "New Approved Event", "Check out '${event?.title}'! It's now open for registration.")
                }
            }

            Result.success(Unit)
        } catch (e: Exception) { 
            Log.e("MainRepository", "Update event status failed", e)
            Result.failure(e) 
        }
    }

    suspend fun getEventsForDean(deanId: String): List<Event> = withContext(Dispatchers.IO) {
        try {
            val approvals = adminDb.from("event_approvals").select {
                filter { 
                    eq("dean_id", deanId)
                    eq("status", "pending") 
                }
            }.decodeList<EventApproval>()
            
            val approvalIds = approvals.map { it.eventId }
            if (approvalIds.isEmpty()) return@withContext emptyList()
            
            return@withContext adminDb.from("events").select {
                filter { 
                    isIn("id", approvalIds)
                }
            }.decodeList<Event>()
        } catch (e: Exception) { 
            Log.e("MainRepository", "getEventsForDean failed", e)
            emptyList() 
        }
    }

    suspend fun getEventsByClubId(clubId: String): List<Event> = withContext(Dispatchers.IO) {
        try { db.from("events").select { filter { eq("club_id", clubId) } }.decodeList<Event>() } catch (e: Exception) { emptyList() }
    }

    suspend fun deleteEvent(id: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            adminDb.from("event_approvals").delete { filter { eq("event_id", id) } }
            adminDb.from("event_registrations").delete { filter { eq("event_id", id) } }
            adminDb.from("events").delete { filter { eq("id", id) } }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("MainRepository", "Delete event failed", e)
            Result.failure(e)
        }
    }

    suspend fun registerForEvent(registration: EventRegistration): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            adminDb.from("event_registrations").insert(registration)
            addPoints(registration.studentId, 100, "event_registration", registration.eventId)
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun getEventRegistrations(eventId: String): List<EventRegistration> = withContext(Dispatchers.IO) {
        try { 
            // FIXED: Column name is event_id, not id
            db.from("event_registrations").select { 
                filter { eq("event_id", eventId) } 
            }.decodeList<EventRegistration>() 
        } catch (e: Exception) { 
            Log.e("MainRepository", "getEventRegistrations failed: ${e.message}")
            emptyList() 
        }
    }

    suspend fun getStudentEventRegistrations(userId: String): List<EventRegistration> = withContext(Dispatchers.IO) {
        try { db.from("event_registrations").select { filter { eq("student_id", userId) } }.decodeList<EventRegistration>() } catch (e: Exception) { emptyList() }
    }

    // --- Profile & Points ---
    suspend fun getStudentProfile(userId: String): Student? = withContext(Dispatchers.IO) { try { adminDb.from("students").select { filter { eq("user_id", userId) } }.decodeSingleOrNull<Student>() } catch (e: Exception) { null } }
    suspend fun getFacultyProfile(userId: String): Faculty? = withContext(Dispatchers.IO) { try { adminDb.from("faculty").select { filter { eq("user_id", userId) } }.decodeSingleOrNull<Faculty>() } catch (e: Exception) { null } }
    
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

    // --- Leaderboard & Points ---
    suspend fun getAllStudents(): List<Student> = withContext(Dispatchers.IO) {
        try { db.from("students").select().decodeList<Student>() } catch (e: Exception) { emptyList() }
    }

    suspend fun getAllFaculty(): List<Faculty> = withContext(Dispatchers.IO) {
        try { db.from("faculty").select().decodeList<Faculty>() } catch (e: Exception) { emptyList() }
    }

    suspend fun searchStudents(query: String): List<Student> = withContext(Dispatchers.IO) {
        try {
            adminDb.from("students").select {
                filter { ilike("name", "%$query%") }
            }.decodeList<Student>()
        } catch (e: Exception) { emptyList() }
    }

    suspend fun addStudent(user: User, student: Student): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val authUser = SupabaseClient.adminClient.auth.admin.createUserWithEmail {
                email = user.email
                password = user.password ?: "123456"
                autoConfirm = true
            }
            val newUser = user.copy(id = authUser.id, role = "student")
            adminDb.from("users").upsert(newUser)
            adminDb.from("students").upsert(student.copy(userId = authUser.id))
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("MainRepository", "Add student failed", e)
            Result.failure(e) 
        }
    }

    suspend fun deleteStudent(userId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            adminDb.from("club_members").delete { filter { eq("student_id", userId) } }
            adminDb.from("club_requests").delete { filter { eq("student_id", userId) } }
            adminDb.from("event_registrations").delete { filter { eq("student_id", userId) } }
            adminDb.from("point_transactions").delete { filter { eq("user_id", userId) } }
            adminDb.from("user_points").delete { filter { eq("user_id", userId) } }
            adminDb.from("notifications").delete { filter { eq("user_id", userId) } }
            adminDb.from("students").delete { filter { eq("user_id", userId) } }
            adminDb.from("users").delete { filter { eq("id", userId) } }
            SupabaseClient.adminClient.auth.admin.deleteUser(userId)
            Result.success(Unit)
        } catch (e: Exception) { 
            Log.e("MainRepository", "Delete student failed", e)
            Result.failure(e) 
        }
    }

    suspend fun addFaculty(user: User, faculty: Faculty): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val authUser = SupabaseClient.adminClient.auth.admin.createUserWithEmail {
                email = user.email
                password = user.password ?: "123456"
                autoConfirm = true
            }
            val newUser = user.copy(id = authUser.id, role = "faculty")
            adminDb.from("users").upsert(newUser)
            adminDb.from("faculty").upsert(faculty.copy(userId = authUser.id))
            Result.success(Unit)
        } catch (e: Exception) { 
            Log.e("MainRepository", "Add faculty failed", e)
            Result.failure(e) 
        }
    }

    suspend fun deleteFaculty(userId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            adminDb.from("study_materials").delete { filter { eq("faculty_id", userId) } }
            try {
                adminDb.from("events").update(buildJsonObject { put("created_by", null as String?) }) { filter { eq("created_by", userId) } }
                adminDb.from("announcements").update(buildJsonObject { put("created_by", null as String?) }) { filter { eq("created_by", userId) } }
            } catch (e: Exception) { }
            adminDb.from("notifications").delete { filter { eq("user_id", userId) } }
            adminDb.from("clubs").update(buildJsonObject { put("club_head_id", null as String?) }) {
                filter { eq("club_head_id", userId) }
            }
            adminDb.from("faculty").delete { filter { eq("user_id", userId) } }
            adminDb.from("users").delete { filter { eq("id", userId) } }
            SupabaseClient.adminClient.auth.admin.deleteUser(userId)
            Result.success(Unit)
        } catch (e: Exception) { 
            Log.e("MainRepository", "Delete faculty failed: ${e.message}", e)
            Result.failure(e) 
        }
    }

    suspend fun getLeaderboard(): List<Pair<Student, UserPoint>> = withContext(Dispatchers.IO) {
        try {
            val points = adminDb.from("user_points").select { order("total_points", Order.DESCENDING) }.decodeList<UserPoint>()
            val students = adminDb.from("students").select().decodeList<Student>()
            val studentMap = students.associateBy { it.userId }
            points.mapNotNull { point -> studentMap[point.userId]?.let { student -> Pair(student, point) } }
        } catch (e: Exception) { emptyList() }
    }

    suspend fun addPoints(userId: String, points: Int, type: String, refId: String?): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            adminDb.from("point_transactions").insert(buildJsonObject {
                put("user_id", userId); put("points", points); put("action_type", type); if (refId != null) put("reference_id", refId)
            })
            val current = adminDb.from("user_points").select { filter { eq("user_id", userId) } }.decodeSingleOrNull<UserPoint>()
            if (current == null) {
                adminDb.from("user_points").insert(UserPoint(userId = userId, totalPoints = points))
            } else {
                adminDb.from("user_points").update(buildJsonObject { put("total_points", current.totalPoints + points) }) { filter { eq("user_id", userId) } }
            }
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    // --- Announcements ---
    suspend fun getAnnouncements(): List<Announcement> = withContext(Dispatchers.IO) {
        try { db.from("announcements").select { order("created_at", Order.DESCENDING) }.decodeList<Announcement>() } catch (e: Exception) { emptyList() }
    }

    suspend fun createAnnouncement(ann: Announcement): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val annJson = buildJsonObject {
                put("title", ann.title)
                put("content", ann.content)
                put("created_by", ann.createdBy)
            }
            adminDb.from("announcements").insert(annJson)
            
            val allUsers = adminDb.from("users").select().decodeList<User>()
            allUsers.forEach { user ->
                try {
                    adminDb.from("notifications").insert(Notification(
                        userId = user.id,
                        title = ann.title,
                        message = ann.content ?: "New Announcement"
                    ))
                } catch (e: Exception) { }
            }
            sendGlobalPushNotification(ann.title, ann.content ?: "New announcement posted")
            Result.success(Unit)
        } catch (e: Exception) { 
            Log.e("MainRepository", "Create announcement failed", e)
            Result.failure(e) 
        }
    }

    suspend fun deleteAnnouncement(id: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            adminDb.from("announcements").delete { filter { eq("id", id) } }
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    // --- Study Materials ---
    suspend fun getStudyMaterials(): List<StudyMaterial> = withContext(Dispatchers.IO) {
        try { adminDb.from("study_materials").select().decodeList<StudyMaterial>() } catch (e: Exception) { emptyList() }
    }

    suspend fun getStudyMaterialsForFaculty(userId: String): List<StudyMaterial> = withContext(Dispatchers.IO) {
        try { adminDb.from("study_materials").select { filter { eq("faculty_id", userId) } }.decodeList<StudyMaterial>() } catch (e: Exception) { emptyList() }
    }

    suspend fun getStudyMaterialsForStudent(batch: String, dept: String): List<StudyMaterial> = withContext(Dispatchers.IO) {
        try {
            val all = adminDb.from("study_materials").select().decodeList<StudyMaterial>()
            all.filter { m ->
                val mBatch = (m.batch ?: "").normalize()
                val mDept = (m.department ?: "").normalize()
                val sBatch = batch.normalize()
                val sDept = dept.normalize()
                
                (mBatch == sBatch && mDept == sDept) ||
                (mBatch.isEmpty() && mDept.isEmpty()) ||
                (mBatch.isEmpty() && mDept == sDept) ||
                (mDept.isEmpty() && mBatch == sBatch)
            }
        } catch (e: Exception) { 
            Log.e("MainRepository", "getStudyMaterialsForStudent failed", e)
            emptyList() 
        }
    }

    suspend fun createStudyMaterial(material: StudyMaterial): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val materialJson = buildJsonObject {
                put("title", material.title)
                put("subject", material.subject)
                put("batch", material.batch)
                put("department", material.department)
                put("file_url", material.fileUrl)
                put("faculty_id", material.uploadedBy)
            }
            adminDb.from("study_materials").insert(materialJson)
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun uploadStudyMaterial(title: String, subject: String, batch: String, department: String, file: File, facultyId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val fileUrlResult = uploadStudyFile(file)
            if (fileUrlResult.isFailure) return@withContext Result.failure(fileUrlResult.exceptionOrNull() ?: Exception("Upload failed"))
            
            val url = fileUrlResult.getOrThrow()
            val material = StudyMaterial(
                title = title,
                subject = subject,
                batch = batch,
                department = department,
                fileUrl = url,
                uploadedBy = facultyId
            )
            createStudyMaterial(material)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun deleteStudyMaterial(id: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            adminDb.from("study_materials").delete { filter { eq("id", id) } }
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    // --- Storage ---
    suspend fun uploadClubBanner(file: File): Result<String> = withContext(Dispatchers.IO) {
        try {
            val fileName = "club_${UUID.randomUUID()}.${file.extension}"
            val bucket = adminStorage.from("club banner")
            bucket.upload(fileName, file.readBytes()) { upsert = true }
            Result.success(bucket.publicUrl(fileName))
        } catch (e: Exception) { 
            Log.e("MainRepository", "uploadClubBanner failed", e)
            Result.failure(e) 
        }
    }

    suspend fun uploadEventBanner(file: File): Result<String> = withContext(Dispatchers.IO) {
        try {
            val fileName = "event_${UUID.randomUUID()}.${file.extension}"
            val bucket = adminStorage.from("event banner")
            bucket.upload(fileName, file.readBytes()) { upsert = true }
            Result.success(bucket.publicUrl(fileName))
        } catch (e: Exception) { 
            Log.e("MainRepository", "uploadEventBanner failed", e)
            Result.failure(e) 
        }
    }

    suspend fun uploadStudyFile(file: File): Result<String> = withContext(Dispatchers.IO) {
        try {
            val fileName = "study_${UUID.randomUUID()}.${file.extension}"
            val bucket = adminStorage.from("study material")
            bucket.upload(fileName, file.readBytes()) { upsert = true }
            Result.success(bucket.publicUrl(fileName))
        } catch (e: Exception) { 
            Log.e("MainRepository", "uploadStudyFile failed", e)
            Result.failure(e) 
        }
    }

    suspend fun uploadProfileImage(userId: String, file: File, role: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val fileName = "${role}_$userId.${file.extension}"
            val bucketName = if (role == "student") "student photo" else "faculty photo"
            val bucket = adminStorage.from(bucketName)
            bucket.upload(fileName, file.readBytes()) {
                upsert = true
            }
            val url = bucket.publicUrl(fileName)
            
            if (role == "student") {
                adminDb.from("students").update(buildJsonObject { put("photo_url", url) }) { filter { eq("user_id", userId) } }
            } else if (role == "faculty") {
                adminDb.from("faculty").update(buildJsonObject { put("photo_url", url) }) { filter { eq("user_id", userId) } }
            }
            
            Result.success(url)
        } catch (e: Exception) { 
            Log.e("MainRepository", "uploadProfileImage failed", e)
            Result.failure(e) 
        }
    }

    // --- Notifications ---
    suspend fun getNotifications(userId: String): List<Notification> = withContext(Dispatchers.IO) {
        try { db.from("notifications").select { filter { eq("user_id", userId) }; order("created_at", Order.DESCENDING) }.decodeList<Notification>() } catch (e: Exception) { emptyList() }
    }

    suspend fun markNotificationsRead(userId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            adminDb.from("notifications").update(buildJsonObject { put("is_read", true) }) { filter { eq("user_id", userId) } }
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun sendNotification(userId: String, title: String, message: String) {
        try { 
            adminDb.from("notifications").insert(Notification(userId = userId, title = title, message = message))
            sendPushNotification(userId, title, message)
        } catch (e: Exception) {
            Log.e("MainRepository", "sendNotification failed for $userId: ${e.message}")
        }
    }

    private fun sendPushNotification(userId: String, title: String, message: String) {
        val json = buildJsonObject {
            put("app_id", "fa04dbc2-3bcb-4fe7-adc1-9205d5669056")
            put("include_external_user_ids", buildJsonArray { add(userId) })
            put("headings", buildJsonObject { put("en", title) })
            put("contents", buildJsonObject { put("en", message) })
        }.toString()

        val restKey = BuildConfig.ONESIGNAL_REST_API_KEY
        if (restKey.isEmpty()) return

        val request = Request.Builder()
            .url("https://onesignal.com/api/v1/notifications")
            .post(json.toRequestBody("application/json".toMediaType()))
            .addHeader("Authorization", "Basic $restKey") 
            .build()

        httpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: java.io.IOException) { }
            override fun onResponse(call: Call, response: Response) { response.close() }
        })
    }

    private fun sendGlobalPushNotification(title: String, message: String) {
        val json = buildJsonObject {
            put("app_id", "fa04dbc2-3bcb-4fe7-adc1-9205d5669056")
            put("included_segments", buildJsonArray { add("All") })
            put("headings", buildJsonObject { put("en", title) })
            put("contents", buildJsonObject { put("en", message) })
        }.toString()

        val restKey = BuildConfig.ONESIGNAL_REST_API_KEY
        if (restKey.isEmpty()) return

        val request = Request.Builder()
            .url("https://onesignal.com/api/v1/notifications")
            .post(json.toRequestBody("application/json".toMediaType()))
            .addHeader("Authorization", "Basic $restKey") 
            .build()

        httpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: java.io.IOException) { }
            override fun onResponse(call: Call, response: Response) { response.close() }
        })
    }
}
