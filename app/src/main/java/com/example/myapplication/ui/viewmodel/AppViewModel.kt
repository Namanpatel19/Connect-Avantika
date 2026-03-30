package com.example.myapplication.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.*
import com.example.myapplication.repository.MainRepository
import kotlinx.coroutines.launch
import java.io.File

class AppViewModel : ViewModel() {
    private val repository = MainRepository()

    var userId: String = ""
    var userRole: String = ""

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _students = MutableLiveData<List<Student>>()
    val students: LiveData<List<Student>> get() = _students

    private val _faculty = MutableLiveData<List<Faculty>>()
    val faculty: LiveData<List<Faculty>> get() = _faculty

    private val _clubs = MutableLiveData<List<Club>>()
    val clubs: LiveData<List<Club>> get() = _clubs

    private val _events = MutableLiveData<List<Event>>()
    val events: LiveData<List<Event>> get() = _events

    private val _pendingEvents = MutableLiveData<List<Event>>()
    val pendingEvents: LiveData<List<Event>> get() = _pendingEvents

    private val _announcements = MutableLiveData<List<Announcement>>()
    val announcements: LiveData<List<Announcement>> get() = _announcements

    private val _currentStudent = MutableLiveData<Student?>()
    val currentStudent: LiveData<Student?> get() = _currentStudent

    private val _currentFaculty = MutableLiveData<Faculty?>()
    val currentFaculty: LiveData<Faculty?> get() = _currentFaculty

    private val _myClub = MutableLiveData<Club?>()
    val myClub: LiveData<Club?> get() = _myClub

    private val _clubEvents = MutableLiveData<List<Event>>()
    val clubEvents: LiveData<List<Event>> get() = _clubEvents

    private val _clubRequests = MutableLiveData<List<ClubRequest>>()
    val clubRequests: LiveData<List<ClubRequest>> get() = _clubRequests

    private val _studentCount = MutableLiveData<Int>()
    val studentCount: LiveData<Int> get() = _studentCount

    private val _facultyCount = MutableLiveData<Int>()
    val facultyCount: LiveData<Int> get() = _facultyCount

    private val _clubCount = MutableLiveData<Int>()
    val clubCount: LiveData<Int> get() = _clubCount

    private val _eventCount = MutableLiveData<Int>()
    val eventCount: LiveData<Int> get() = _eventCount

    fun loadSystemStats() {
        viewModelScope.launch {
            val students = repository.getAllStudents()
            _studentCount.value = students.size
            _students.value = students

            val faculty = repository.getAllFaculty()
            _facultyCount.value = faculty.size
            _faculty.value = faculty

            val clubs = repository.getClubs()
            _clubCount.value = clubs.size
            _clubs.value = clubs

            val events = repository.getAllEvents()
            _eventCount.value = events.size
            _events.value = events
        }
    }

    fun loadAnnouncements() {
        loadAllAnnouncements()
    }

    fun loadAllStudents() {
        viewModelScope.launch {
            _isLoading.value = true
            val list = repository.getAllStudents()
            _students.value = list
            _studentCount.value = list.size
            _isLoading.value = false
        }
    }

    fun searchStudents(query: String) {
        viewModelScope.launch {
            _students.value = repository.searchStudents(query)
        }
    }

    fun addStudent(user: User, student: Student, autoConfirm: Boolean, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = repository.addStudent(user, student, autoConfirm)
            callback(result.isSuccess)
            if (result.isSuccess) loadAllStudents()
        }
    }

    fun deleteStudent(userId: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = repository.deleteStudent(userId)
            callback(result.isSuccess)
            if (result.isSuccess) loadAllStudents()
        }
    }

    fun updateStudentProfile(student: Student, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = repository.updateStudentProfile(student)
            callback(result.isSuccess)
            if (result.isSuccess) loadCurrentStudent()
        }
    }

    fun loadAllFaculty() {
        viewModelScope.launch {
            _isLoading.value = true
            val list = repository.getAllFaculty()
            _faculty.value = list
            _facultyCount.value = list.size
            _isLoading.value = false
        }
    }

    fun addFaculty(user: User, faculty: Faculty, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = repository.addFaculty(user, faculty)
            callback(result.isSuccess)
            if (result.isSuccess) loadAllFaculty()
        }
    }

    fun deleteFaculty(userId: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = repository.deleteFaculty(userId)
            callback(result.isSuccess)
            if (result.isSuccess) loadAllFaculty()
        }
    }

    fun updateFacultyProfile(faculty: Faculty, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = repository.updateFacultyProfile(faculty)
            callback(result.isSuccess)
            if (result.isSuccess) loadCurrentFaculty()
        }
    }

    fun loadAllClubs() {
        viewModelScope.launch {
            _isLoading.value = true
            val list = repository.getClubs()
            _clubs.value = list
            _clubCount.value = list.size
            _isLoading.value = false
        }
    }

    fun loadClubs() {
        loadAllClubs()
    }

    fun addClub(club: Club, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = repository.createClub(club)
            callback(result.isSuccess)
            if (result.isSuccess) loadAllClubs()
        }
    }

    fun updateClub(club: Club, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = repository.updateClub(club)
            callback(result.isSuccess)
            if (result.isSuccess) loadMyClub()
        }
    }

    fun deleteClub(clubId: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = repository.deleteClub(clubId)
            callback(result.isSuccess)
            if (result.isSuccess) loadAllClubs()
        }
    }

    fun joinClub(clubId: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = repository.joinClub(clubId, userId)
            callback(result.isSuccess)
        }
    }

    fun loadClubRequests(clubId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _clubRequests.value = repository.getClubRequests(clubId)
            _isLoading.value = false
        }
    }

    fun updateClubRequest(requestId: String, status: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = repository.updateClubRequest(requestId, status)
            callback(result.isSuccess)
        }
    }

    fun uploadClubBanner(clubId: String, file: File, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.uploadClubBanner(clubId, file)
            callback(result.isSuccess)
            if (result.isSuccess) loadMyClub()
            _isLoading.value = false
        }
    }

    fun loadAllEvents() {
        viewModelScope.launch {
            _isLoading.value = true
            val list = repository.getAllEvents()
            _events.value = list
            _eventCount.value = list.size
            _isLoading.value = false
        }
    }

    fun loadApprovedEvents() {
        viewModelScope.launch {
            _isLoading.value = true
            _events.value = repository.getEvents("approved")
            _isLoading.value = false
        }
    }

    fun loadPendingEvents() {
        viewModelScope.launch {
            _isLoading.value = true
            _pendingEvents.value = repository.getEvents("pending")
            _isLoading.value = false
        }
    }

    fun updateEventStatus(eventId: String, status: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = repository.updateEventStatus(eventId, status)
            callback(result.isSuccess)
            if (result.isSuccess) {
                loadPendingEvents()
                loadAllEvents()
            }
        }
    }

    fun submitEventRequest(event: Event, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = repository.createEvent(event)
            callback(result.isSuccess)
            if (result.isSuccess) {
                loadMyClub()
                loadAllEvents()
            }
        }
    }

    fun createEventRequest(event: Event, callback: (Boolean) -> Unit) {
        submitEventRequest(event, callback)
    }

    fun registerForEvent(eventId: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = repository.registerForEvent(eventId, userId)
            callback(result.isSuccess)
        }
    }

    fun deleteEvent(id: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = repository.deleteEvent(id)
            callback(result.isSuccess)
            if (result.isSuccess) loadAllEvents()
        }
    }

    fun loadAllAnnouncements() {
        viewModelScope.launch {
            _isLoading.value = true
            _announcements.value = repository.getAnnouncements()
            _isLoading.value = false
        }
    }

    fun createAnnouncement(announcement: Announcement, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = repository.createAnnouncement(announcement)
            callback(result.isSuccess)
            if (result.isSuccess) loadAllAnnouncements()
        }
    }

    fun deleteAnnouncement(id: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = repository.deleteAnnouncement(id)
            callback(result.isSuccess)
            if (result.isSuccess) loadAllAnnouncements()
        }
    }

    fun loadCurrentStudent() {
        viewModelScope.launch {
            _currentStudent.value = repository.getStudentProfile(userId)
        }
    }

    fun loadCurrentFaculty() {
        viewModelScope.launch {
            _currentFaculty.value = repository.getFacultyProfile(userId)
        }
    }

    fun loadMyClub() {
        viewModelScope.launch {
            _isLoading.value = true
            val club = repository.getClubByHeadId(userId)
            _myClub.value = club
            club?.id?.let { clubId ->
                _clubEvents.value = repository.getEventsByClubId(clubId)
                _clubRequests.value = repository.getClubRequests(clubId)
            }
            _isLoading.value = false
        }
    }

    fun uploadPhoto(file: File, role: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.uploadProfileImage(userId, file, role)
            callback(result.isSuccess)
            if (result.isSuccess) {
                if (role == "student") loadCurrentStudent() else loadCurrentFaculty()
            }
            _isLoading.value = false
        }
    }
}
