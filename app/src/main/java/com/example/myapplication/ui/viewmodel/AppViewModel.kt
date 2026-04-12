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
    val repository = MainRepository()

    var userId: String = ""
    var userRole: String = ""

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _deans = MutableLiveData<List<User>>(emptyList())
    val deans: LiveData<List<User>> get() = _deans

    private val _allUsers = MutableLiveData<List<User>>(emptyList())
    val allUsers: LiveData<List<User>> get() = _allUsers

    private val _students = MutableLiveData<List<Student>>(emptyList())
    val students: LiveData<List<Student>> get() = _students

    private val _faculty = MutableLiveData<List<Faculty>>(emptyList())
    val faculty: LiveData<List<Faculty>> get() = _faculty

    private val _clubs = MutableLiveData<List<Club>>(emptyList())
    val clubs: LiveData<List<Club>> get() = _clubs

    private val _events = MutableLiveData<List<Event>>(emptyList())
    val events: LiveData<List<Event>> get() = _events

    private val _deanPendingEvents = MutableLiveData<List<Event>>(emptyList())
    val deanPendingEvents: LiveData<List<Event>> get() = _deanPendingEvents

    private val _announcements = MutableLiveData<List<Announcement>>(emptyList())
    val announcements: LiveData<List<Announcement>> get() = _announcements

    private val _currentStudent = MutableLiveData<Student?>(null)
    val currentStudent: LiveData<Student?> get() = _currentStudent

    private val _currentFaculty = MutableLiveData<Faculty?>(null)
    val currentFaculty: LiveData<Faculty?> get() = _currentFaculty

    private val _myClub = MutableLiveData<Club?>(null)
    val myClub: LiveData<Club?> get() = _myClub

    private val _clubEvents = MutableLiveData<List<Event>>(emptyList())
    val clubEvents: LiveData<List<Event>> get() = _clubEvents

    private val _eventRegistrations = MutableLiveData<List<EventRegistration>>(emptyList())
    val eventRegistrations: LiveData<List<EventRegistration>> get() = _eventRegistrations

    private val _myClubRequests = MutableLiveData<List<ClubRequest>>(emptyList())
    val myClubRequests: LiveData<List<ClubRequest>> get() = _myClubRequests

    private val _clubRequests = MutableLiveData<List<ClubRequest>>(emptyList())
    val clubRequests: LiveData<List<ClubRequest>> get() = _clubRequests

    private val _clubMembers = MutableLiveData<List<ClubMember>>(emptyList())
    val clubMembers: LiveData<List<ClubMember>> get() = _clubMembers

    private val _notifications = MutableLiveData<List<Notification>>(emptyList())
    val notifications: LiveData<List<Notification>> get() = _notifications
    
    private val _unreadNotificationsCount = MutableLiveData<Int>(0)
    val unreadNotificationsCount: LiveData<Int> get() = _unreadNotificationsCount

    private val _studyMaterials = MutableLiveData<List<StudyMaterial>>(emptyList())
    val studyMaterials: LiveData<List<StudyMaterial>> get() = _studyMaterials

    private val _uploadProgress = MutableLiveData<Boolean>(false)
    val uploadProgress: LiveData<Boolean> get() = _uploadProgress

    private val _leaderboard = MutableLiveData<List<Pair<Student, UserPoint>>>(emptyList())
    val leaderboard: LiveData<List<Pair<Student, UserPoint>>> get() = _leaderboard

    fun loadDeans() {
        viewModelScope.launch {
            _deans.value = repository.getAllDeans()
        }
    }

    fun loadAllUsers() {
        viewModelScope.launch {
            _allUsers.value = repository.getAllUsers()
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
            if (result.isSuccess) {
                loadAllClubs()
                loadMyClub()
            }
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
            try {
                _isLoading.value = true
                val uploadResult = repository.uploadClubBanner(file)
                if (uploadResult.isSuccess) {
                    val bannerUrl = uploadResult.getOrThrow()
                    val updateResult = repository.updateClubBanner(clubId, bannerUrl)
                    if (updateResult.isSuccess) {
                        loadAllClubs()
                        loadMyClub()
                    }
                    _isLoading.value = false
                    callback(updateResult.isSuccess)
                } else {
                    Log.e("AppViewModel", "Banner upload failed: ${uploadResult.exceptionOrNull()?.message}")
                    _isLoading.value = false
                    callback(false)
                }
            } catch (e: Exception) {
                Log.e("AppViewModel", "uploadClubBanner failed", e)
                _isLoading.value = false
                callback(false)
            }
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
            if (userId.isNotEmpty()) {
                _deanPendingEvents.value = repository.getEventsForDean(userId)
            }
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
                    } else {
                        Log.e("AppViewModel", "Banner upload failed: ${uploadResult.exceptionOrNull()?.message}")
                        _isLoading.value = false
                        callback(false)
                        return@launch
                    }
                }
                
                val result = repository.createEvent(eventToSubmit, event.deanId)
                if (result.isSuccess) {
                    loadMyClub()
                } else {
                    Log.e("AppViewModel", "Repository createEvent failed: ${result.exceptionOrNull()?.message}")
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
                loadMyClub()
            }
            callback(result.isSuccess)
        }
    }

    fun registerForEvent(eventId: String, contact: String? = null, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = repository.registerForEvent(EventRegistration(eventId = eventId, studentId = userId, contact = contact))
            if (result.isSuccess) {
                loadApprovedEvents() 
                loadLeaderboard()
            }
            callback(result.isSuccess)
        }
    }

    fun loadEventEntries(eventId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _eventRegistrations.value = repository.getEventRegistrations(eventId)
            _isLoading.value = false
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
                _clubMembers.value = repository.getClubMembers(it)
            }
            _isLoading.value = false
        }
    }

    fun joinClub(clubId: String, clubName: String, clubHeadId: String?, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = repository.joinClub(clubId, userId)
            if (result.isSuccess) {
                clubHeadId?.let { headId: String -> 
                    repository.sendNotification(headId, "New Join Request", "A student wants to join $clubName.") 
                }
                loadMyClubRequests()
            }
            callback(result.isSuccess)
        }
    }

    fun loadMyClubRequests() { viewModelScope.launch { _myClubRequests.value = repository.getUserClubRequests(userId) } }
    
    fun loadNotifications() { 
        viewModelScope.launch { 
            val list = repository.getNotifications(userId)
            _notifications.value = list
            _unreadNotificationsCount.value = list.count { !it.isRead }
        } 
    }
    
    fun markNotificationsAsRead() {
        viewModelScope.launch {
            repository.markNotificationsRead(userId)
            _unreadNotificationsCount.value = 0
            loadNotifications()
        }
    }

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
                loadMyClub() 
                loadLeaderboard()
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

    fun kickClubMember(clubId: String, studentId: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = repository.deleteClubMember(clubId, studentId)
            if (result.isSuccess) {
                repository.sendNotification(studentId, "Club Membership", "You have been removed from the club.")
                loadMyClub()
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
        loadDeans()
        loadAllUsers()
    }

    fun createAnnouncement(ann: Announcement, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.createAnnouncement(ann.copy(createdBy = userId))
            if (result.isSuccess) loadAnnouncements()
            _isLoading.value = false
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

    fun updateStudentProfile(student: Student, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = repository.updateStudentProfile(student)
            if (result.isSuccess) loadCurrentStudent()
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
            val events = repository.getApprovedEvents()
            val registrations = repository.getStudentEventRegistrations(userId)
            val regIds = registrations.map { it.eventId }.toSet()
            
            events.forEach { it.isRegistered = regIds.contains(it.id) }
            
            _events.value = events
            _isLoading.value = false
        }
    }

    fun loadStudyMaterials() {
        viewModelScope.launch {
            _studyMaterials.value = when (userRole) {
                "faculty" -> repository.getStudyMaterialsForFaculty(userId)
                "student" -> {
                    val student = _currentStudent.value ?: repository.getStudentProfile(userId)
                    if (student != null) {
                        repository.getStudyMaterialsForStudent(student.batch ?: "", student.department ?: "")
                    } else {
                        emptyList()
                    }
                }
                else -> repository.getStudyMaterials()
            }
        }
    }

    fun uploadStudyMaterial(title: String, subject: String, batch: String, dept: String, file: File, callback: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                _uploadProgress.value = true
                val result = repository.uploadStudyMaterial(title, subject, batch, dept, file, userId)
                if (result.isSuccess) {
                    loadStudyMaterials()
                    callback(true, "Material uploaded successfully")
                } else {
                    callback(false, "Upload failed: ${result.exceptionOrNull()?.message}")
                }
                _uploadProgress.value = false
            } catch (e: Exception) {
                Log.e("AppViewModel", "Upload study material failed", e)
                _uploadProgress.value = false
                callback(false, "An error occurred: ${e.message}")
            }
        }
    }

    fun deleteStudyMaterial(id: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = repository.deleteStudyMaterial(id)
            if (result.isSuccess) loadStudyMaterials()
            callback(result.isSuccess)
        }
    }

    fun loadLeaderboard() {
        viewModelScope.launch {
            _isLoading.value = true
            _leaderboard.value = repository.getLeaderboard()
            _isLoading.value = false
        }
    }

    // --- Profile Stats ---
    private val _studentClubsCount = MutableLiveData<Int>(0)
    val studentClubsCount: LiveData<Int> get() = _studentClubsCount

    private val _studentEventsCount = MutableLiveData<Int>(0)
    val studentEventsCount: LiveData<Int> get() = _studentEventsCount

    private val _userEmail = MutableLiveData<String?>()
    val userEmail: LiveData<String?> get() = _userEmail

    fun loadProfileStats() {
        viewModelScope.launch {
            _studentClubsCount.value = repository.getStudentClubMemberships(userId).size
            _studentEventsCount.value = repository.getStudentEventRegistrations(userId).size
            _userEmail.value = repository.getUserEmail(userId)
        }
    }

    fun uploadProfileImage(file: File, callback: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.uploadProfileImage(userId, file, userRole)
            _isLoading.value = false
            if (result.isSuccess) {
                if (userRole == "student") loadCurrentStudent() else loadCurrentFaculty()
                callback(true, result.getOrNull())
            } else {
                callback(false, null)
            }
        }
    }

    fun addDean(user: User, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.addDean(user)
            if (result.isSuccess) loadDeans()
            _isLoading.value = false
            callback(result.isSuccess)
        }
    }

    fun deleteDean(userId: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.deleteDean(userId)
            if (result.isSuccess) loadDeans()
            _isLoading.value = false
            callback(result.isSuccess)
        }
    }
}
