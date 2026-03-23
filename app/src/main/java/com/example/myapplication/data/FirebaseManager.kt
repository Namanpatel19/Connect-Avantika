package com.example.myapplication.data

import com.example.myapplication.models.Club
import com.example.myapplication.models.Event
import com.example.myapplication.models.Notice
import com.example.myapplication.models.Student
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.tasks.await

object FirebaseManager {
    
    private val db: FirebaseFirestore? by lazy {
        try { FirebaseFirestore.getInstance() } catch (e: Exception) { null }
    }
    
    private val auth: FirebaseAuth? by lazy {
        try { FirebaseAuth.getInstance() } catch (e: Exception) { null }
    }

    val currentUserUid: String?
        get() = auth?.currentUser?.uid

    suspend fun getEvents(): List<Event> {
        val database = db ?: return emptyList()
        return try {
            database.collection("events").get().await().toObjects(Event::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun registerForEvent(eventId: String): Boolean {
        val database = db ?: return false
        val uid = currentUserUid ?: return false
        return try {
            database.collection("events").document(eventId)
                .update("registeredUsers", FieldValue.arrayUnion(uid))
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getClubs(): List<Club> {
        val database = db ?: return emptyList()
        return try {
            database.collection("clubs").get().await().toObjects(Club::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun requestToJoinClub(clubId: String): Boolean {
        val database = db ?: return false
        val uid = currentUserUid ?: return false
        return try {
            database.collection("clubs").document(clubId)
                .update("pendingRequests", FieldValue.arrayUnion(uid))
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getNotices(): List<Notice> {
        val database = db ?: return emptyList()
        return try {
            database.collection("notices").get().await().toObjects(Notice::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getStudentProfile(): Student? {
        val database = db ?: return null
        val uid = currentUserUid ?: return null
        return try {
            database.collection("students").document(uid).get().await().toObject(Student::class.java)
        } catch (e: Exception) {
            null
        }
    }
}
