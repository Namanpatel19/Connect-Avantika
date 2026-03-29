package com.example.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.*
import com.example.myapplication.repository.MainRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class MainViewModel(private val repository: MainRepository = MainRepository()) : ViewModel() {

    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events = _events.asStateFlow()

    private val _announcements = MutableStateFlow<List<Announcement>>(emptyList())
    val announcements = _announcements.asStateFlow()

    private val _clubs = MutableStateFlow<List<Club>>(emptyList())
    val clubs = _clubs.asStateFlow()

    private val _studentProfile = MutableStateFlow<Student?>(null)
    val studentProfile = _studentProfile.asStateFlow()

    private val _facultyProfile = MutableStateFlow<Faculty?>(null)
    val facultyProfile = _facultyProfile.asStateFlow()

    private val _studyMaterials = MutableStateFlow<List<StudyMaterial>>(emptyList())
    val studyMaterials = _studyMaterials.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun fetchEvents(status: String? = "approved") {
        viewModelScope.launch {
            _isLoading.value = true
            _events.value = repository.getEvents(status)
            _isLoading.value = false
        }
    }

    fun fetchAnnouncements() {
        viewModelScope.launch {
            _isLoading.value = true
            _announcements.value = repository.getAnnouncements()
            _isLoading.value = false
        }
    }

    fun fetchClubs() {
        viewModelScope.launch {
            _isLoading.value = true
            _clubs.value = repository.getClubs()
            _isLoading.value = false
        }
    }

    fun fetchStudentProfile(userId: String) {
        viewModelScope.launch {
            _studentProfile.value = repository.getStudentProfile(userId)
        }
    }

    fun fetchFacultyProfile(userId: String) {
        viewModelScope.launch {
            _facultyProfile.value = repository.getFacultyProfile(userId)
        }
    }

    fun updateStudentProfile(student: Student) {
        viewModelScope.launch {
            repository.updateStudentProfile(student)
            _studentProfile.value = student
        }
    }

    fun uploadProfileImage(userId: String, file: File, role: String) {
        viewModelScope.launch {
            val result = repository.uploadProfileImage(userId, file, role)
            if (result.isSuccess) {
                if (role == "student") fetchStudentProfile(userId)
                else fetchFacultyProfile(userId)
            }
        }
    }

    fun fetchStudyMaterials() {
        viewModelScope.launch {
            _isLoading.value = true
            _studyMaterials.value = repository.getStudyMaterials()
            _isLoading.value = false
        }
    }

    fun uploadStudyMaterial(userId: String, file: File, title: String, subject: String) {
        viewModelScope.launch {
git add .            repository.uploadStudyMaterial(title, subject, file, userId)
            fetchStudyMaterials()
        }
    }

    fun createEventRequest(event: Event) {
        viewModelScope.launch {
            repository.createEventRequest(event)
            fetchEvents("pending")
        }
    }

    fun updateEventStatus(eventId: String, status: String) {
        viewModelScope.launch {
            repository.updateEventStatus(eventId, status)
            fetchEvents(null) // Refresh all
        }
    }

    fun registerEvent(eventId: String, studentId: String) {
        viewModelScope.launch {
            repository.registerForEvent(eventId, studentId)
        }
    }

    fun joinClub(clubId: String, studentId: String) {
        viewModelScope.launch {
            repository.joinClub(clubId, studentId)
        }
    }
}
