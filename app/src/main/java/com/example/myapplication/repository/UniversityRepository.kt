package com.example.myapplication.repository

import com.example.myapplication.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.myapplication.data.Event
import com.example.myapplication.data.Club
import com.example.myapplication.data.Student

class UniversityRepository {

    suspend fun getEvents() = withContext(Dispatchers.IO) {
        SupabaseClient.client.postgrest["events"]
            .select {
                filter {
                    eq("status", "approved")
                }
            }.decodeList<Event>()
    }

    suspend fun getClubs() = withContext(Dispatchers.IO) {
        SupabaseClient.client.postgrest["clubs"]
            .select().decodeList<Club>()
    }

    suspend fun getStudentProfile(userId: String) = withContext(Dispatchers.IO) {
        SupabaseClient.client.postgrest["students"]
            .select {
                filter {
                    eq("user_id", userId)
                }
            }.decodeSingle<Student>()
    }
    
    suspend fun submitEventRequest(event: Event) = withContext(Dispatchers.IO) {
        SupabaseClient.client.postgrest["events"].insert(event)
    }
}
