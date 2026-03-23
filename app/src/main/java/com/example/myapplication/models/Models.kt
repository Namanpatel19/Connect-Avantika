package com.example.myapplication.models

data class Event(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val date: String = "",
    val location: String = "",
    val imageUrl: String = "",
    val registeredUsers: List<String> = emptyList()
)

data class Club(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val logoUrl: String = "",
    val members: List<String> = emptyList(),
    val pendingRequests: List<String> = emptyList()
)

data class Student(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val department: String = "",
    val studentId: String = "",
    val year: String = ""
)