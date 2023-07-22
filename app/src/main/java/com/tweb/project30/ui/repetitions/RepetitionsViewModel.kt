package com.tweb.project30.ui.repetitions

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tweb.project30.data.course.Course
import com.tweb.project30.data.course.CourseRepository
import com.tweb.project30.data.professor.Professor
import com.tweb.project30.data.repetition.Repetition
import com.tweb.project30.data.repetition.RepetitionRepository
import com.tweb.project30.data.teaching.TeachingRepository
import com.tweb.project30.data.user.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.*


data class RepetitionsUIState(
    var courses: List<Course> = emptyList(),
    var professors: Map<String, List<Professor>> = emptyMap(),
    var selectedProfessors: Map<String, List<Professor>> = emptyMap(),
    var loading: Boolean = false,
    var currentUserId: String = "",
    var myRepetitions: List<Repetition> = emptyList(),
    var allUsersRepetitions: List<Repetition> = emptyList(),
    var lastRepetitionUpdated: Repetition? = null,
)

class RepetitionsViewModel(
    private val userRepository: UserRepository,
    private val repetitionRepository: RepetitionRepository,
    private val teachingRepository: TeachingRepository,
    private val courseRepository: CourseRepository
) : ViewModel(
) {
    private val _state = MutableStateFlow(RepetitionsUIState())

    val uiState: StateFlow<RepetitionsUIState>
        get() = _state

    init {

        getCoursesAndProfessors()

        viewModelScope.launch {
            // Observe changes to the professors property only once
            _state.first { state ->
                state.professors.isNotEmpty()
            }
            // Call getAllRepetitions with desired date range
            getAllRepetitions(firstDateOfYear(), lastDateOfYear())
        }

        UserRepository.isLogged.observeForever { isLogged ->

            if (isLogged) {
                _state.update { it.copy(currentUserId = UserRepository.currentUser!!.id) }
                getMyRepetitions(firstDateOfYear(), lastDateOfYear())
                getAllRepetitions(firstDateOfYear(), lastDateOfYear())
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        UserRepository.isLogged.removeObserver { }
    }

    private fun getCoursesAndProfessors() {

        _state.update { it.copy(loading = true) }

        viewModelScope.launch {
            try {
                val courses = courseRepository.getCourses()
                val professors = teachingRepository.getProfessorsByCourse(courses.map { it.ID })

                _state.update {
                    it.copy(
                        courses = courses,
                        professors = professors,
                        selectedProfessors = professors
                    )
                }
            } catch (e: Exception) {
                Log.e("RepetitionsVM", e.stackTraceToString())
            } finally {
                _state.update { it.copy(loading = false) }
            }
        }
    }

    fun updateFilters(
        selectedProfessors: Map<String, List<Professor>>
    ) {
        _state.update {
            it.copy(
                selectedProfessors = selectedProfessors
            )
        }

        getAllRepetitions(firstDateOfYear(), lastDateOfYear())
    }

    fun addRepetition(
        idcourse: String,
        idprofessor: String,
        date: LocalDate,
        time: Int,
        note: String? = null
    ) {

        _state.update { it.copy(loading = true) }

        viewModelScope.launch {
            try {
                var rep = repetitionRepository.addRepetition(
                    Repetition(
                        course = Course(ID = idcourse, title = ""),
                        professor = Professor(ID = idprofessor, name = "", surname = ""),
                        date = date,
                        time = time,
                        note = note,
                        ID = "",
                        status = "pending",
                    )
                )

                var courses = _state.value.selectedProfessors.keys.toList()
                val allRepetitions = repetitionRepository.getRepetitions(
                    courses,
                    firstDateOfYear(),
                    lastDateOfYear()
                )

                val myRepetitions = repetitionRepository.getRepetitions(
                    _state.value.currentUserId,
                    firstDateOfYear(),
                    lastDateOfYear()
                )

                _state.update {
                    it.copy(
                        allUsersRepetitions = allRepetitions.sortedBy { it.date },
                        myRepetitions = myRepetitions.sortedBy { it.date },
                        lastRepetitionUpdated = rep
                    )
                }

            } catch (e: Exception) {
                Log.e("RepetitionsVM", e.stackTraceToString())
            } finally {
                _state.update { it.copy(loading = false) }
            }
        }
    }

    fun updateRepetition(repetition: Repetition, status: String, note: String? = null) {

        _state.update { it.copy(loading = true) }

        viewModelScope.launch {
            try {
                // If update has an error, an execption is thrown, so after the update we can assume that the update was successful
                repetitionRepository.updateRepetition(repetition.ID, status, note)

                var courses = _state.value.selectedProfessors.keys.toList()
                val allRepetitions = repetitionRepository.getRepetitions(
                    courses,
                    firstDateOfYear(),
                    lastDateOfYear()
                )

                val myRepetitions = repetitionRepository.getRepetitions(
                    _state.value.currentUserId,
                    firstDateOfYear(),
                    lastDateOfYear()
                )

                _state.update {
                    it.copy(
                        lastRepetitionUpdated = repetition,
                        allUsersRepetitions = allRepetitions.sortedBy { it.date },
                        myRepetitions = myRepetitions.sortedBy { it.date },
                    )
                }

            } catch (e: Exception) {
                Log.e("RepetitionsVM", e.stackTraceToString())
            } finally {
                _state.update { it.copy(loading = false) }
            }
        }
    }

    fun deleteRepetition(repetition: Repetition) {

        _state.update { it.copy(loading = true) }

        viewModelScope.launch {
            try {
                // If update has an error, an execption is thrown, so after the update we can assume that the update was successful
                repetitionRepository.deleteRepetition(repetition.ID)

                var courses = _state.value.selectedProfessors.keys.toList()
                val allRepetitions = repetitionRepository.getRepetitions(
                    courses,
                    firstDateOfYear(),
                    lastDateOfYear()
                )

                val myRepetitions = repetitionRepository.getRepetitions(
                    _state.value.currentUserId,
                    firstDateOfYear(),
                    lastDateOfYear()
                )

                _state.update {
                    it.copy(
                        lastRepetitionUpdated = repetition,
                        allUsersRepetitions = allRepetitions.sortedBy { it.date },
                        myRepetitions = myRepetitions.sortedBy { it.date },
                    )
                }

            } catch (e: Exception) {
                Log.e("RepetitionsVM", e.stackTraceToString())
            } finally {
                _state.update { it.copy(loading = false) }
            }
        }

    }

    private fun getMyRepetitions(startDate: Date, endDate: Date) {

        _state.update { it.copy(loading = true) }

        viewModelScope.launch {
            try {

                val myRepetitions = repetitionRepository.getRepetitions(
                    _state.value.currentUserId,
                    startDate,
                    endDate
                )

                _state.update {
                    it.copy(
                        myRepetitions = myRepetitions.sortedBy { it.date },
                    )
                }
                Log.e("RepetitionsV MyRep", myRepetitions.toString())
            } catch (e: Exception) {
                Log.e("RepetitionsVM", e.stackTraceToString())
            } finally {
                _state.update { it.copy(loading = false) }
            }
        }

    }

    private fun getAllRepetitions(startDate: Date, endDate: Date) {

        _state.update { it.copy(loading = true) }

        viewModelScope.launch {
            try {

                var courses = _state.value.selectedProfessors.keys.toList()
                val allRepetitions = repetitionRepository.getRepetitions(
                    courses,
                    startDate,
                    endDate
                )
                Log.e("RepetitionsVM AllRep", allRepetitions.toString())
                _state.update {
                    it.copy(
                        allUsersRepetitions = allRepetitions.sortedBy { it.date },
                    )
                }
            } catch (e: Exception) {
                Log.e("RepetitionsVM", e.stackTraceToString())
            } finally {
                _state.update { it.copy(loading = false) }
            }
        }

    }

    private fun firstDateOfYear(): Date {
        val date = Calendar.getInstance()
        date.set(LocalDate.now().year,1,1)
        return date.time
    }

    private fun lastDateOfYear(): Date {
        val date = Calendar.getInstance()
        date.set(LocalDate.now().year,12,31)
        return date.time
    }


}

class RepetitionsViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RepetitionsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")

            Log.e("vm", "creato view")

            return RepetitionsViewModel(
                userRepository = UserRepository,
                repetitionRepository = RepetitionRepository,
                teachingRepository = TeachingRepository,
                courseRepository = CourseRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}