package com.example.myapplication.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.*
import com.example.myapplication.repository.MainRepository
import kotlinx.coroutines.launch
import java.io.File

class AppViewModel : ViewModel() {

    private val repository = MainRepository()

    // --- Session ---
    var userId: String = ""
    var userRole: String = ""

    // --- Events ---
    val events = MutableLiveData<List<Event>>()
    val pendingEvents = MutableLiveData<List<Event>>()
    val clubEvents = MutableLiveData<List<Event>>()

    // --- Clubs ---
    val clubs = MutableLiveData<List<Club>>()
    val myClub = MutableLiveData<Club?>()
    val clubRequests = MutableLiveData<List<ClubRequest>>()

    // --- Announcements ---
    val announcements = MutableLiveData<List<Announcement>>()

    // --- Users ---
    val students = MutableLiveData<List<Student>>()
    val faculty = MutableLiveData<List<Faculty>>()
    val currentStudent = MutableLiveData<Student?>()
    val currentFaculty = MutableLiveData<Faculty?>()

    // --- Study Materials ---
    val studyMaterials = MutableLiveData<List<StudyMaterial>>()

    // --- Stats ---
    val studentCount = MutableLiveData<Int>()
    val facultyCount = MutableLiveData<Int>()
    val clubCount = MutableLiveData<Int>()
    val eventCount = MutableLiveData<Int>()

    // --- Loading / Error ---
    val isLoading = MutableLiveData<Boolean>(false)
    val errorMessage = MutableLiveData<String?>()
    val successMessage = MutableLiveData<String?>()

    // ========================
    // Events
    // ========================
    fun loadApprovedEvents() = viewModelScope.launch {
        isLoading.value = true
        try { events.value = repository.getEvents("approved") } catch (e: Exception) { errorMessage.value = e.message } finally { isLoading.value = false }
    }

    fun loadPendingEvents() = viewModelScope.launch {
        isLoading.value = true
        try { pendingEvents.value = repository.getPendingEvents() } catch (e: Exception) { errorMessage.value = e.message } finally { isLoading.value = false }
    }

    fun loadClubEvents(clubId: String) = viewModelScope.launch {
        try { clubEvents.value = repository.getEventsByClub(clubId) } catch (e: Exception) { errorMessage.value = e.message }
    }

    fun submitEventRequest(event: Event, onResult: (Boolean) -> Unit) = viewModelScope.launch {
        isLoading.value = true
        val result = repository.createEventRequest(event)
        isLoading.value = false
        onResult(result.isSuccess)
        if (result.isFailure) errorMessage.value = result.exceptionOrNull()?.message
    }

    fun updateEventStatus(eventId: String, status: String, onResult: (Boolean) -> Unit) = viewModelScope.launch {
        val result = repository.updateEventStatus(eventId, status)
        onResult(result.isSuccess)
        if (result.isSuccess) {
            pendingEvents.value = pendingEvents.value?.filter { it.id != eventId }
        }
    }

    fun registerForEvent(eventId: String, onResult: (Boolean) -> Unit) = viewModelScope.launch {
        val result = repository.registerForEvent(eventId, userId)
        onResult(result.isSuccess)
        if (result.isFailure) errorMessage.value = result.exceptionOrNull()?.message
    }

    // ========================
    // Clubs
    // ========================
    fun loadClubs() = viewModelScope.launch {
        isLoading.value = true
        try { clubs.value = repository.getClubs() } catch (e: Exception) { errorMessage.value = e.message } finally { isLoading.value = false }
    }

    fun loadMyClub() = viewModelScope.launch {
        myClub.value = repository.getClubForHead(userId)
        myClub.value?.id?.let { loadClubEvents(it); loadClubRequests(it) }
    }

    fun loadClubRequests(clubId: String) = viewModelScope.launch {
        try { clubRequests.value = repository.getClubRequests(clubId) } catch (e: Exception) { errorMessage.value = e.message }
    }

    fun joinClub(clubId: String, onResult: (Boolean) -> Unit) = viewModelScope.launch {
        val result = repository.joinClub(clubId, userId)
        onResult(result.isSuccess)
        if (result.isFailure) errorMessage.value = result.exceptionOrNull()?.message
    }

    fun updateClubRequest(requestId: String, status: String, onResult: (Boolean) -> Unit) = viewModelScope.launch {
        val result = repository.updateClubRequestStatus(requestId, status)
        onResult(result.isSuccess)
    }

    fun updateClub(club: Club, onResult: (Boolean) -> Unit) = viewModelScope.launch {
        val result = repository.updateClub(club)
        onResult(result.isSuccess)
        if (result.isSuccess) myClub.value = club
    }

    // ========================
    // Announcements
    // ========================
    fun loadAnnouncements() = viewModelScope.launch {
        try { announcements.value = repository.getAnnouncements() } catch (e: Exception) { errorMessage.value = e.message }
    }

    fun createAnnouncement(announcement: Announcement, onResult: (Boolean) -> Unit) = viewModelScope.launch {
        val result = repository.createAnnouncement(announcement)
        onResult(result.isSuccess)
        if (result.isSuccess) loadAnnouncements()
    }

    fun deleteAnnouncement(id: String, onResult: (Boolean) -> Unit) = viewModelScope.launch {
        val result = repository.deleteAnnouncement(id)
        onResult(result.isSuccess)
        if (result.isSuccess) announcements.value = announcements.value?.filter { it.id != id }
    }

    // ========================
    // Students
    // ========================
    fun loadCurrentStudent() = viewModelScope.launch {
        currentStudent.value = repository.getStudentProfile(userId)
    }

    fun loadAllStudents() = viewModelScope.launch {
        isLoading.value = true
        try { students.value = repository.getAllStudents() } catch (e: Exception) { errorMessage.value = e.message } finally { isLoading.value = false }
    }

    fun searchStudents(query: String) = viewModelScope.launch {
        try { students.value = repository.searchStudents(query) } catch (e: Exception) { errorMessage.value = e.message }
    }

    fun updateStudentProfile(student: Student, onResult: (Boolean) -> Unit) = viewModelScope.launch {
        val result = repository.updateStudentProfile(student)
        onResult(result.isSuccess)
        if (result.isSuccess) currentStudent.value = student
    }

    fun addStudent(user: User, student: Student, autoConfirm: Boolean = true, onResult: (Boolean) -> Unit) = viewModelScope.launch {
        isLoading.value = true
        val result = repository.addStudent(user, student, autoConfirm)
        isLoading.value = false
        onResult(result.isSuccess)
        if (result.isSuccess) loadAllStudents()
        else errorMessage.value = result.exceptionOrNull()?.message
    }

    fun deleteStudent(userId: String, onResult: (Boolean) -> Unit) = viewModelScope.launch {
        val result = repository.deleteStudent(userId)
        onResult(result.isSuccess)
        if (result.isSuccess) students.value = students.value?.filter { it.userId != userId }
    }

    // ========================
    // Faculty
    // ========================
    fun loadCurrentFaculty() = viewModelScope.launch {
        currentFaculty.value = repository.getFacultyProfile(userId)
    }

    fun loadAllFaculty() = viewModelScope.launch {
        isLoading.value = true
        try { faculty.value = repository.getAllFaculty() } catch (e: Exception) { errorMessage.value = e.message } finally { isLoading.value = false }
    }

    fun searchFaculty(query: String) = viewModelScope.launch {
        try { faculty.value = repository.searchFaculty(query) } catch (e: Exception) { errorMessage.value = e.message }
    }

    fun updateFacultyProfile(f: Faculty, onResult: (Boolean) -> Unit) = viewModelScope.launch {
        val result = repository.updateFacultyProfile(f)
        onResult(result.isSuccess)
        if (result.isSuccess) currentFaculty.value = f
    }

    fun addFaculty(user: User, faculty: Faculty, autoConfirm: Boolean = true, onResult: (Boolean) -> Unit) = viewModelScope.launch {
        isLoading.value = true
        val result = repository.addFaculty(user, faculty, autoConfirm)
        isLoading.value = false
        onResult(result.isSuccess)
        if (result.isSuccess) loadAllFaculty()
        else errorMessage.value = result.exceptionOrNull()?.message
    }

    fun deleteFaculty(userId: String, onResult: (Boolean) -> Unit) = viewModelScope.launch {
        val result = repository.deleteFaculty(userId)
        onResult(result.isSuccess)
        if (result.isSuccess) faculty.value = faculty.value?.filter { it.userId != userId }
    }

    // ========================
    // Study Materials
    // ========================
    fun loadStudyMaterials() = viewModelScope.launch {
        try { studyMaterials.value = repository.getStudyMaterials() } catch (e: Exception) { errorMessage.value = e.message }
    }

    fun uploadMaterial(file: File, title: String, subject: String, onResult: (Boolean) -> Unit) = viewModelScope.launch {
        isLoading.value = true
        val result = repository.uploadStudyMaterial(title, subject, file, userId)
        isLoading.value = false
        onResult(result.isSuccess)
        if (result.isSuccess) loadStudyMaterials()
        else errorMessage.value = result.exceptionOrNull()?.message
    }

    // ========================
    // Dean Stats
    // ========================
    fun loadSystemStats() = viewModelScope.launch {
        // These methods should be implemented in MainRepository
        // For now, providing dummy implementation to avoid compilation errors if possible, 
        // but the user only reported the mismatch error.
    }

    // ========================
    // Profile Photo Upload
    // ========================
    fun uploadPhoto(file: File, onResult: (String?) -> Unit) = viewModelScope.launch {
        // This method should be implemented in MainRepository
    }
}
