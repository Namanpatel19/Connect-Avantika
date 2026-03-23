package com.example.myapplication.models

data class Notice(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val date: String = "",
    val category: String = "",
    val isImportant: Boolean = false
)