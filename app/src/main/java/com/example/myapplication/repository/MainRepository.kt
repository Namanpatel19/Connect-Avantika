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
            Log.d("MainRepository", "Fetched user for $userId: $user")
            user?.role
        } catch (e: Exception) {
            Log.e("MainRepository", "Error fetching user role for $userId", e)
            null
        }
    }

    suspend fun getUserById(userId: String): User? = withContext(Dispatchers.IO) {
        try {
            db.from("users").select {
                filter { eq("id", userId) }
            }.decodeSingleOrNull<User>()
        } catch (e: Exception) { null }
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

    suspend fun getPendingEvents(): List<Event> = withContext(Dispatchers.IO) {
        db.from("events").select {
            filter { eq("status", "pending") }
        }.decodeList<Event>()
    }

    suspend fun getEventsByClub(clubId: String): List<Event> = withContext(Dispatchers.IO) {
        db.from("events").select {
            filter { eq("club_id", clubId) }
        }.decodeList<Event>()
    }

    suspend fun createEventRequest(event: Event): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            db.from("events").insert(event)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

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

    suspend fun registerForEvent(eventId: String, studentId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val registration = EventRegistration(eventId = eventId, studentId = studentId)
            db.from("event_registrations").insert(registration)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun isRegisteredForEvent(eventId: String, studentId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val result = db.from("event_registrations").select {
                filter {
                    eq("event_id", eventId)
                    eq("student_id", studentId)
                }
            }.decodeList<EventRegistration>()
            result.isNotEmpty()
        } catch (e: Exception) { false }
    }

    // --- Clubs ---
    suspend fun getClubs(): List<Club> = withContext(Dispatchers.IO) {
        db.from("clubs").select().decodeList<Club>()
    }

    suspend fun getClubForHead(userId: String): Club? = withContext(Dispatchers.IO) {
        try {
            db.from("clubs").select {
                filter { eq("club_head_id", userId) }
            }.decodeSingleOrNull<Club>()
        } catch (e: Exception) { null }
    }

    suspend fun joinClub(clubId: String, studentId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val request = ClubRequest(clubId = clubId, studentId = studentId)
            db.from("club_requests").insert(request)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getClubRequests(clubId: String): List<ClubRequest> = withContext(Dispatchers.IO) {
        db.from("club_requests").select {
            filter { eq("club_id", clubId) }
        }.decodeList<ClubRequest>()
    }

    suspend fun updateClubRequestStatus(requestId: String, status: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            db.from("club_requests").update({
                set("status", status)
            }) {
                filter { eq("id", requestId) }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateClub(club: Club): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            db.from("clubs").update(club) {
                filter { eq("id", club.id!!) }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
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
    suspend fun getStudentProfile(userId: String): Student? = withContext(Dispatchers.IO) {
        try {
            db.from("students").select {
                filter { eq("user_id", userId) }
            }.decodeSingleOrNull<Student>()
        } catch (e: Exception) { null }
    }

    suspend fun getAllStudents(): List<Student> = withContext(Dispatchers.IO) {
        db.from("students").select().decodeList<Student>()
    }

    suspend fun updateStudentProfile(student: Student): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            db.from("students").update(student) {
                filter { eq("user_id", student.userId) }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addStudent(user: User, student: Student, autoConfirm: Boolean = true): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // 1. Create User in Supabase Auth via Admin API
            val newUser = adminAuth.createUserWithEmail {
                email = user.email
                password = user.password ?: "Welcome@123"
                // Using emailConfirm if available, otherwise it defaults based on Supabase settings
                // In 3.x, confirm is not directly in the simple builder often, 
                // but let's see if we can just skip it or find the right one.
            }
            
            val userId = newUser.id
            
            // 2. Insert into public.users table
            val publicUser = user.copy(id = userId, password = null) // Don't store plain password
            db.from("users").insert(publicUser)
            
            // 3. Insert into public.students table
            val updatedStudent = student.copy(userId = userId)
            db.from("students").insert(updatedStudent)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("MainRepository", "Error adding student", e)
            Result.failure(e)
        }
    }

    suspend fun deleteStudent(userId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            db.from("students").delete { filter { eq("user_id", userId) } }
            db.from("users").delete { filter { eq("id", userId) } }
            try { adminAuth.deleteUser(userId) } catch (e: Exception) { Log.e("MainRepository", "Auth delete failed", e) }
            
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
                    ilike("department", "%$query%")
                }
            }
        }.decodeList<Student>()
    }

    // --- Faculty ---
    suspend fun getFacultyProfile(userId: String): Faculty? = withContext(Dispatchers.IO) {
        try {
            db.from("faculty").select {
                filter { eq("user_id", userId) }
            }.decodeSingleOrNull<Faculty>()
        } catch (e: Exception) { null }
    }

    suspend fun getAllFaculty(): List<Faculty> = withContext(Dispatchers.IO) {
        db.from("faculty").select().decodeList<Faculty>()
    }

    suspend fun updateFacultyProfile(faculty: Faculty): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            db.from("faculty").update(faculty) {
                filter { eq("user_id", faculty.userId) }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addFaculty(user: User, faculty: Faculty, autoConfirm: Boolean = true): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // 1. Create User in Supabase Auth via Admin API
            val newUser = adminAuth.createUserWithEmail {
                email = user.email
                password = user.password ?: "Welcome@123"
            }
            
            val userId = newUser.id
            
            // 2. Insert into public.users table
            val publicUser = user.copy(id = userId, password = null)
            db.from("users").insert(publicUser)
            
            // 3. Insert into public.faculty table
            val updatedFaculty = faculty.copy(userId = userId)
            db.from("faculty").insert(updatedFaculty)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("MainRepository", "Error adding faculty", e)
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

    suspend fun searchFaculty(query: String): List<Faculty> = withContext(Dispatchers.IO) {
        db.from("faculty").select {
            filter {
                or {
                    ilike("name", "%$query%")
                    ilike("department", "%$query%")
                }
            }
        }.decodeList<Faculty>()
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
            
            val material = StudyMaterial(
                title = title,
                subject = subject,
                fileUrl = publicUrl,
                facultyId = uploaderId
            )
            db.from("study_materials").insert(material)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- Profile Image ---
    suspend fun uploadProfileImage(userId: String, file: File, role: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val fileName = "${userId}_${System.currentTimeMillis()}.${file.extension}"
            val path = "profiles/$fileName"
            storage.from("profiles").upload(path, file.readBytes())
            val publicUrl = storage.from("profiles").publicUrl(path)

            if (role == "student") {
                db.from("students").update({
                    set("photo_url", publicUrl)
                }) {
                    filter { eq("user_id", userId) }
                }
            } else {
                db.from("faculty").update({
                    set("photo_url", publicUrl)
                }) {
                    filter { eq("user_id", userId) }
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
