package com.example.myapplication.ui.viewmodel

import android.util.Log
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

    private val _currentFaculty = MutableLiveData<Faculty?>()
    val currentFaculty: LiveData<Faculty?> get() = _currentFaculty

    private val _myClub = MutableLiveData<Club?>()
    val myClub: LiveData<Club?> get() = _myClub

    private val _clubEvents = MutableLiveData<List<Event>>()
    val clubEvents: LiveData<List<Event>> get() = _clubEvents

    private val _eventRegistrations = MutableLiveData<List<EventRegistration>>()
    val eventRegistrations: LiveData<List<EventRegistration>> get() = _eventRegistrations

    private val _myClubRequests = MutableLiveData<List<ClubRequest>>()
    val myClubRequests: LiveData<List<ClubRequest>> get() = _myClubRequests

    private val _clubRequests = MutableLiveData<List<ClubRequest>>()
    val clubRequests: LiveData<List<ClubRequest>> get() = _clubRequests

    private val _notifications = MutableLiveData<List<Notification>>()
    val notifications: LiveData<List<Notification>> get() = _notifications

    private val _studyMaterials = MutableLiveData<List<StudyMaterial>>()
    val studyMaterials: LiveData<List<StudyMaterial>> get() = _studyMaterials

    private val _uploadProgress = MutableLiveData<Boolean>()
    val uploadProgress: LiveData<Boolean> get() = _uploadProgress

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

    fun addClub(club: Club, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = repository.createClub(club)
            if (result.isSuccess) loadAllClubs()
            callback(result.isSuccess)
        }
    }

    fun updateClub(club: Club, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = repository.updateClub(club)
            if (result.isSuccess) loadAllClubs()
            callback(result.isSuccess)
        }
    }

    fun deleteClub(id: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = repository.deleteClub(id)
            if (result.isSuccess) loadAllClubs()
            callback(result.isSuccess)
        }
    }

    fun uploadClubBanner(clubId: String, file: File, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val uploadResult = repository.uploadClubBanner(file)
            if (uploadResult.isSuccess) {
                val bannerUrl = uploadResult.getOrNull()
                val club = _clubs.value?.find { it.id == clubId }
                if (club != null && bannerUrl != null) {
                    val updateResult = repository.updateClub(club.copy(bannerUrl = bannerUrl))
                    if (updateResult.isSuccess) loadAllClubs()
                    _isLoading.value = false
                    callback(updateResult.isSuccess)
                    return@launch
                }
            }
            _isLoading.value = false
            callback(false)
        }
    }

    fun assignClubLead(clubId: String, email: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val user = repository.getUserByEmail(email)
            if (user != null) {
                val result = repository.updateClubLead(clubId, user.id)
                if (result.isSuccess) loadAllClubs()
                callback(result.isSuccess)
            } else {
                callback(false)
            }
        }
    }

    fun loadLiveEvents() {
        viewModelScope.launch {
            _events.value = repository.getApprovedEvents()
        }
    }

    fun loadAllEvents() {
        viewModelScope.launch {
            _events.value = repository.getAllEvents()
        }
    }

    fun loadDeanEvents() {
        viewModelScope.launch {
            _deanPendingEvents.value = repository.getEventsForDean(userId)
        }
    }

    fun submitEvent(event: Event, bannerFile: File?, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                var eventToSubmit = event.copy(createdBy = userId)
                
                if (bannerFile != null) {
                    val uploadResult = repository.uploadEventBanner(bannerFile)
                    if (uploadResult.isSuccess) {
                        eventToSubmit = eventToSubmit.copy(bannerUrl = uploadResult.getOrNull())
                    }
                }
                
                // Pass deanId separately to repository
                val result = repository.createEvent(eventToSubmit, event.deanId)
                if (result.isSuccess) {
                    event.deanId?.let { deanId ->
                        repository.sendNotification(deanId, "New Event Approval", "A new event '${event.title}' is waiting for your approval.")
                    }
                    loadMyClub()
                }
                _isLoading.value = false
                callback(result.isSuccess)
            } catch (e: Exception) {
                Log.e("AppViewModel", "Submit event failed", e)
                _isLoading.value = false
                callback(false)
            }
        }
    }

    fun updateEventStatus(eventId: String, status: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val result = repository.updateEventStatus(eventId, status, userId)
                if (result.isSuccess) {
                    loadDeanEvents()
                }
                callback(result.isSuccess)
            } catch (e: Exception) {
                Log.e("AppViewModel", "Update event status failed", e)
                callback(false)
            }
        }
    }

    fun deleteEvent(id: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = repository.deleteEvent(id)
            if (result.isSuccess) {
                loadAllEvents()
                loadMyClub() // Refresh club events if any
            }
            callback(result.isSuccess)
        }
    }

    fun registerForEvent(eventId: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = repository.registerForEvent(EventRegistration(eventId = eventId, studentId = userId))
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
            club?.id?.let {
                _clubEvents.value = repository.getEventsByClubId(it)
                _clubRequests.value = repository.getClubRequests(it)
            }
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
    fun loadCurrentFaculty() { viewModelScope.launch { _currentFaculty.value = repository.getFacultyProfile(userId) } }
    fun loadAllStudents() { viewModelScope.launch { _students.value = repository.getAllStudents() } }
    fun loadAllFaculty() { viewModelScope.launch { _faculty.value = repository.getAllFaculty() } }
    fun loadAnnouncements() { viewModelScope.launch { _announcements.value = repository.getAnnouncements() } }
    fun loadAllAnnouncements() = loadAnnouncements()

    fun loadClubRequests(clubId: String) {
        viewModelScope.launch {
            _clubRequests.value = repository.getClubRequests(clubId)
        }
    }

    fun acceptClubRequest(request: ClubRequest, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = repository.updateClubRequestStatus(request.id!!, "accepted")
            if (result.isSuccess) {
                repository.addClubMember(request.clubId, request.studentId)
                repository.sendNotification(request.studentId, "Club Request Accepted", "You have been accepted into the club.")
                loadClubRequests(request.clubId)
            }
            callback(result.isSuccess)
        }
    }

    fun rejectClubRequest(request: ClubRequest, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = repository.updateClubRequestStatus(request.id!!, "rejected")
            if (result.isSuccess) {
                repository.sendNotification(request.studentId, "Club Request Rejected", "Your request to join the club has been rejected.")
                loadClubRequests(request.clubId)
            }
            callback(result.isSuccess)
        }
    }

    fun callForInterview(request: ClubRequest, date: String, time: String, venue: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = repository.updateClubRequestStatus(request.id!!, "interview", date, time, venue)
            if (result.isSuccess) {
                repository.sendNotification(request.studentId, "Club Interview Scheduled", "Interview on $date at $time at $venue.")
                loadClubRequests(request.clubId)
            }
            callback(result.isSuccess)
        }
    }

    fun searchStudents(query: String) {
        viewModelScope.launch {
            _students.value = repository.searchStudents(query)
        }
    }

    fun addStudent(user: User, student: Student, autoConfirm: Boolean = true, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.addStudent(user, student)
            if (result.isSuccess) loadAllStudents()
            _isLoading.value = false
            callback(result.isSuccess)
        }
    }

    fun deleteStudent(userId: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.deleteStudent(userId)
            if (result.isSuccess) loadAllStudents()
            _isLoading.value = false
            callback(result.isSuccess)
        }
    }

    fun loadSystemStats() {
        loadAllStudents()
        loadAllFaculty()
        loadAllClubs()
        loadAllEvents()
    }

    fun createAnnouncement(ann: Announcement, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = repository.createAnnouncement(ann)
            if (result.isSuccess) loadAnnouncements()
            callback(result.isSuccess)
        }
    }

    fun deleteAnnouncement(id: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = repository.deleteAnnouncement(id)
            if (result.isSuccess) loadAnnouncements()
            callback(result.isSuccess)
        }
    }

    fun updateFacultyProfile(faculty: Faculty, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = repository.updateFacultyProfile(faculty)
            if (result.isSuccess) {
                _currentFaculty.value = faculty
            }
            callback(result.isSuccess)
        }
    }

    fun addFaculty(user: User, faculty: Faculty, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.addFaculty(user, faculty)
            if (result.isSuccess) loadAllFaculty()
            _isLoading.value = false
            callback(result.isSuccess)
        }
    }

    fun deleteFaculty(userId: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.deleteFaculty(userId)
            if (result.isSuccess) loadAllFaculty()
            _isLoading.value = false
            callback(result.isSuccess)
        }
    }

    fun loadApprovedEvents() {
        viewModelScope.launch {
            _isLoading.value = true
            _events.value = repository.getApprovedEvents()
            _isLoading.value = false
        }
    }

    fun loadStudyMaterials() {
        viewModelScope.launch {
            _studyMaterials.value = repository.getStudyMaterials()
        }
    }

    fun uploadStudyMaterial(title: String, subject: String, batch: String, dept: String, file: File, callback: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                _uploadProgress.value = true
                val uploadResult = repository.uploadStudyFile(file)
                if (uploadResult.isSuccess) {
                    val material = StudyMaterial(
                        title = title,
                        subject = subject,
                        batch = batch,
                        department = dept,
                        fileUrl = uploadResult.getOrNull(),
                        uploadedBy = userId
                    )
                    val createResult = repository.createStudyMaterial(material)
                    if (createResult.isSuccess) {
                        loadStudyMaterials()
                        callback(true, "Material uploaded successfully")
                    } else {
                        callback(false, "Failed to save material details")
                    }
                } else {
                    callback(false, "File upload failed")
                }
                _uploadProgress.value = false
            } catch (e: Exception) {
                Log.e("AppViewModel", "Upload study material failed", e)
                _uploadProgress.value = false
                callback(false, "An error occurred: ${e.message}")
            }
        }
    }
}
