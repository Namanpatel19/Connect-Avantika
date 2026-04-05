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
    
    private val _myClubRequests = MutableLiveData<List<ClubRequest>>()
    val myClubRequests: LiveData<List<ClubRequest>> get() = _myClubRequests

    private val _notifications = MutableLiveData<List<Notification>>()
    val notifications: LiveData<List<Notification>> get() = _notifications

    private val _studyMaterials = MutableLiveData<List<StudyMaterial>>()
    val studyMaterials: LiveData<List<StudyMaterial>> get() = _studyMaterials

    private val _uploadProgress = MutableLiveData<Boolean>(false)
    val uploadProgress: LiveData<Boolean> get() = _uploadProgress

    fun loadSystemStats() {
        viewModelScope.launch {
            _students.value = repository.getAllStudents()
            _faculty.value = repository.getAllFaculty()
            _clubs.value = repository.getClubs()
            _events.value = repository.getAllEvents()
        }
    }

    fun loadAllClubs() {
        viewModelScope.launch {
            _isLoading.value = true
            _clubs.value = repository.getClubs()
            _isLoading.value = false
        }
    }

    fun joinClub(clubId: String, clubName: String, clubHeadId: String?, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = repository.joinClub(clubId, userId)
            if (result.isSuccess) {
                // Notify Club Lead
                clubHeadId?.let { headId ->
                    repository.sendNotification(
                        userId = headId,
                        title = "New Club Request!",
                        message = "A student has requested to join $clubName."
                    )
                }
                loadMyClubRequests()
            }
            callback(result.isSuccess)
        }
    }
    
    fun loadMyClubRequests() {
        viewModelScope.launch {
            _myClubRequests.value = repository.getUserClubRequests(userId)
        }
    }

    fun loadClubRequests(clubId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _clubRequests.value = repository.getClubRequests(clubId)
            _isLoading.value = false
        }
    }

    // --- Club Lead Actions ---
    
    fun acceptClubRequest(request: ClubRequest, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val addResult = repository.addClubMember(request.clubId, request.studentId)
            if (addResult.isSuccess) {
                repository.updateClubRequestStatus(request.id!!, "accepted")
                repository.sendNotification(
                    userId = request.studentId,
                    title = "Club Membership Accepted! 🎉",
                    message = "Congratulations! You are now a member of the club."
                )
                loadMyClub() 
                callback(true)
            } else {
                callback(false)
            }
            _isLoading.value = false
        }
    }

    fun callForInterview(request: ClubRequest, date: String, time: String, venue: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.updateClubRequestStatus(request.id!!, "interview", date, time, venue)
            if (result.isSuccess) {
                repository.sendNotification(
                    userId = request.studentId,
                    title = "Interview Scheduled 🗓️",
                    message = "You have been called for an interview on $date at $time. Venue: $venue"
                )
                loadMyClub()
                callback(true)
            } else {
                callback(false)
            }
            _isLoading.value = false
        }
    }

    fun deleteClubRequest(requestId: String, studentId: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = repository.deleteClubRequest(requestId)
            if (result.isSuccess) {
                repository.sendNotification(
                    userId = studentId,
                    title = "Club Request Update",
                    message = "Your request to join the club was declined."
                )
                loadMyClub()
            }
            callback(result.isSuccess)
        }
    }

    fun rejectClubRequest(request: ClubRequest, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.updateClubRequestStatus(request.id!!, "rejected")
            if (result.isSuccess) {
                repository.sendNotification(
                    userId = request.studentId,
                    title = "Club Request Declined",
                    message = "Your request to join the club has been declined."
                )
                loadMyClub()
            }
            callback(result.isSuccess)
            _isLoading.value = false
        }
    }

    // --- Super Admin Actions ---

    fun assignClubLead(clubId: String, email: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = repository.assignClubLead(clubId, email)
            if (result.isSuccess) {
                loadAllClubs()
            }
            callback(result.isSuccess)
        }
    }

    fun deleteClub(clubId: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = repository.deleteClub(clubId)
            if (result.isSuccess) {
                loadAllClubs()
            }
            callback(result.isSuccess)
        }
    }

    fun addClub(club: Club, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = repository.createClub(club)
            callback(result.isSuccess)
            if (result.isSuccess) loadAllClubs()
        }
    }

    // --- Notifications ---

    fun loadNotifications() {
        viewModelScope.launch {
            _notifications.value = repository.getNotifications(userId)
        }
    }

    // --- Profile & Club Loading ---

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

    fun loadCurrentStudent() {
        viewModelScope.launch {
            _currentStudent.value = repository.getStudentProfile(userId)
            loadMyClubRequests()
        }
    }

    fun loadCurrentFaculty() {
        viewModelScope.launch {
            _currentFaculty.value = repository.getFacultyProfile(userId)
        }
    }

    fun updateFacultyProfile(faculty: Faculty, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = repository.updateFacultyProfile(faculty)
            callback(result.isSuccess)
            if (result.isSuccess) _currentFaculty.value = faculty
        }
    }

    // --- Events ---

    fun loadAllEvents() { 
        viewModelScope.launch { 
            _events.value = repository.getAllEvents() 
        } 
    }

    fun loadPendingEvents() {
        viewModelScope.launch {
            _pendingEvents.value = repository.getEvents("pending")
        }
    }

    fun loadApprovedEvents() {
        viewModelScope.launch {
            _events.value = repository.getEvents("approved")
        }
    }

    fun updateEventStatus(eventId: String, status: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = repository.updateEventStatus(eventId, status)
            callback(result.isSuccess)
            if (result.isSuccess) {
                loadAllEvents()
                loadPendingEvents()
            }
        }
    }

    fun deleteEvent(eventId: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = repository.deleteEvent(eventId)
            callback(result.isSuccess)
            if (result.isSuccess) loadAllEvents()
        }
    }

    fun registerForEvent(eventId: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = repository.registerForEvent(eventId, userId)
            callback(result.isSuccess)
        }
    }

    fun submitEventRequest(event: Event, bannerFile: File?, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            var eventToSubmit = event
            
            if (bannerFile != null) {
                val uploadResult = repository.uploadEventBanner(bannerFile)
                if (uploadResult.isSuccess) {
                    eventToSubmit = event.copy(bannerUrl = uploadResult.getOrNull())
                } else {
                    _isLoading.value = false
                    callback(false)
                    return@launch
                }
            }
            
            val result = repository.createEvent(eventToSubmit)
            callback(result.isSuccess)
            if (result.isSuccess) {
                loadAllEvents()
                loadMyClub()
            }
            _isLoading.value = false
        }
    }

    // --- Students & Faculty Management ---

    fun loadAllStudents() { 
        viewModelScope.launch { 
            _students.value = repository.getAllStudents() 
        } 
    }
    
    fun loadAllFaculty() { 
        viewModelScope.launch { 
            _faculty.value = repository.getAllFaculty() 
        } 
    }

    fun addStudent(user: User, student: Student, autoConfirm: Boolean = true, callback: (Boolean) -> Unit) {
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

    fun searchStudents(query: String) {
        viewModelScope.launch {
            _students.value = repository.searchStudents(query)
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

    // --- Announcements & Materials ---

    fun loadAnnouncements() { 
        viewModelScope.launch { 
            _announcements.value = repository.getAnnouncements() 
        } 
    }

    fun loadAllAnnouncements() {
        loadAnnouncements()
    }

    fun createAnnouncement(announcement: Announcement, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = repository.createAnnouncement(announcement)
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

    fun loadStudyMaterials() { 
        viewModelScope.launch { 
            _studyMaterials.value = repository.getStudyMaterials() 
        } 
    }
    
    fun uploadStudyMaterial(title: String, subject: String, batch: String, department: String, file: File, callback: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            _uploadProgress.value = true
            val result = repository.uploadStudyMaterial(title, subject, batch, department, file, userId)
            _uploadProgress.value = false
            callback(result.isSuccess, if (result.isSuccess) "Uploaded successfully" else "Upload failed: ${result.exceptionOrNull()?.message}")
            if (result.isSuccess) loadStudyMaterials()
        }
    }

    fun updateClub(club: Club, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = repository.updateClub(club)
            callback(result.isSuccess)
            if (result.isSuccess) loadMyClub()
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
}
