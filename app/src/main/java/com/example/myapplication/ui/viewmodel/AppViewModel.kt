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

    private val _deans = MutableLiveData<List<User>>()
    val deans: LiveData<List<User>> get() = _deans

    private val _students = MutableLiveData<List<Student>>()
    val students: LiveData<List<Student>> get() = _students

    private val _faculty = MutableLiveData<List<Faculty>>()
    val faculty: LiveData<List<Faculty>> get() = _faculty

    private val _clubs = MutableLiveData<List<Club>>()
    val clubs: LiveData<List<Club>> get() = _clubs

    private val _events = MutableLiveData<List<Event>>()
    val events: LiveData<List<Event>> get() = _events

    private val _deanPendingEvents = MutableLiveData<List<Event>>()
    val deanPendingEvents: LiveData<List<Event>> get() = _deanPendingEvents

    private val _announcements = MutableLiveData<List<Announcement>>()
    val announcements: LiveData<List<Announcement>> get() = _announcements

    private val _currentStudent = MutableLiveData<Student?>()
    val currentStudent: LiveData<Student?> get() = _currentStudent

    private val _myClub = MutableLiveData<Club?>()
    val myClub: LiveData<Club?> get() = _myClub

    private val _clubEvents = MutableLiveData<List<Event>>()
    val clubEvents: LiveData<List<Event>> get() = _clubEvents

    private val _eventRegistrations = MutableLiveData<List<EventRegistration>>()
    val eventRegistrations: LiveData<List<EventRegistration>> get() = _eventRegistrations

    private val _myClubRequests = MutableLiveData<List<ClubRequest>>()
    val myClubRequests: LiveData<List<ClubRequest>> get() = _myClubRequests

    private val _notifications = MutableLiveData<List<Notification>>()
    val notifications: LiveData<List<Notification>> get() = _notifications

    fun loadDeans() {
        viewModelScope.launch {
            _deans.value = repository.getAllDeans()
        }
    }

    fun loadAllClubs() {
        viewModelScope.launch {
            _isLoading.value = true
            _clubs.value = repository.getClubs()
            _isLoading.value = false
        }
    }

    fun loadLiveEvents() {
        viewModelScope.launch {
            _events.value = repository.getApprovedEvents()
        }
    }

    fun loadDeanEvents() {
        viewModelScope.launch {
            _deanPendingEvents.value = repository.getEventsForDean(userId)
        }
    }

    fun submitEvent(event: Event, bannerFile: File?, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            var eventToSubmit = event.copy(createdBy = userId)
            
            if (bannerFile != null) {
                val uploadResult = repository.uploadEventBanner(bannerFile)
                if (uploadResult.isSuccess) {
                    eventToSubmit = eventToSubmit.copy(bannerUrl = uploadResult.getOrNull())
                }
            }
            
            val result = repository.createEvent(eventToSubmit)
            if (result.isSuccess) {
                event.deanId?.let { deanId ->
                    repository.sendNotification(deanId, "New Event Approval", "A new event '${event.title}' is waiting for your approval.")
                }
                loadMyClub()
            }
            _isLoading.value = false
            callback(result.isSuccess)
        }
    }

    fun updateEventStatus(eventId: String, status: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = repository.updateEventStatus(eventId, status)
            if (result.isSuccess) {
                loadDeanEvents()
            }
            callback(result.isSuccess)
        }
    }

    fun registerForEvent(reg: EventRegistration, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = repository.registerForEvent(reg.copy(studentId = userId))
            callback(result.isSuccess)
        }
    }

    fun loadEventEntries(eventId: String) {
        viewModelScope.launch {
            _eventRegistrations.value = repository.getEventRegistrations(eventId)
        }
    }

    fun loadMyClub() {
        viewModelScope.launch {
            _isLoading.value = true
            val club = repository.getClubByHeadId(userId)
            _myClub.value = club
            club?.id?.let { _clubEvents.value = repository.getEventsByClubId(it) }
            _isLoading.value = false
        }
    }

    fun joinClub(clubId: String, clubName: String, clubHeadId: String?, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = repository.joinClub(clubId, userId)
            if (result.isSuccess) {
                clubHeadId?.let { repository.sendNotification(it, "New Join Request", "A student wants to join $clubName.") }
                loadMyClubRequests()
            }
            callback(result.isSuccess)
        }
    }

    fun loadMyClubRequests() { viewModelScope.launch { _myClubRequests.value = repository.getUserClubRequests(userId) } }
    fun loadNotifications() { viewModelScope.launch { _notifications.value = repository.getNotifications(userId) } }
    fun loadCurrentStudent() { viewModelScope.launch { _currentStudent.value = repository.getStudentProfile(userId); loadMyClubRequests() } }
    fun loadAllStudents() { viewModelScope.launch { _students.value = repository.getAllStudents() } }
    fun loadAnnouncements() { viewModelScope.launch { _announcements.value = repository.getAnnouncements() } }
}
