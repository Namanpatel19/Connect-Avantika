package com.example.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.*
import com.example.myapplication.repository.MainRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(private val repository: MainRepository = MainRepository()) : ViewModel() {

    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events = _events.asStateFlow()

    private val _announcements = MutableStateFlow<List<Announcement>>(emptyList())
    val announcements = _announcements.asStateFlow()

    private val _academics = MutableStateFlow<List<Academic>>(emptyList())
    val academics = _academics.asStateFlow()

    private val _clubs = MutableStateFlow<List<Club>>(emptyList())
    val clubs = _clubs.asStateFlow()

    fun fetchEvents() {
        viewModelScope.launch {
            _events.value = repository.getEvents()
        }
    }

    fun fetchAnnouncements(role: String) {
        viewModelScope.launch {
            _announcements.value = repository.getAnnouncements(role)
        }
    }

    fun fetchAcademics(studentId: Int) {
        viewModelScope.launch {
            _academics.value = repository.getAcademics(studentId)
        }
    }

    fun fetchClubs() {
        viewModelScope.launch {
            _clubs.value = repository.getClubs()
        }
    }

    fun registerEvent(eventId: Int, studentId: Int) {
        viewModelScope.launch {
            repository.registerForEvent(eventId, studentId)
        }
    }

    fun joinClub(clubId: Int, studentId: Int) {
        viewModelScope.launch {
            repository.joinClub(clubId, studentId)
        }
    }
}
