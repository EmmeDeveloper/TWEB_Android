package com.tweb.project30.ui.repetitions

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tweb.project30.data.course.Course
import com.tweb.project30.data.course.CourseRepository
import com.tweb.project30.data.professor.Professor
import com.tweb.project30.data.repetition.RepetitionRepository
import com.tweb.project30.data.teaching.TeachingRepository
import com.tweb.project30.data.user.UserRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class RepetitionsUIState(
    var courses: List<Course> = emptyList(),
    var professors: Map<String, List<Professor>> = emptyMap(),
    var selectedProfessors: Map<String, List<Professor>> = emptyMap(),
    var loading: Boolean = false,
    var currentUserId: String = ""
)

class RepetitionsViewModel(
    private val userRepository: UserRepository,
    private val repetitionRepository: RepetitionRepository,
    private val teachingRepository: TeachingRepository,
    private val courseRepository: CourseRepository
) : ViewModel(
) {

    private var refreshingJob: Job? = null

    private val _state = MutableStateFlow(RepetitionsUIState())

    val uiState: StateFlow<RepetitionsUIState>
        get() = _state

    init {

        val courses: List<Course> = listOf(
            Course(title = "TWeb", ID = "TWeb"),
            Course(title = "Analisi 1", ID = "Analisi1"),
            Course(title = "Algoritmi", ID = "Algoritmi"),
        )

        val firstNameList = listOf("Alice", "Bob", "Charlie", "Diana", "Ethan", "Fiona")
        val lastNameList = listOf("Smith", "Johnson", "Williams", "Brown", "Jones", "Davis")

        val professors: Map<String, List<Professor>> = mapOf(
            "TWeb" to listOf(
                Professor(name = firstNameList.random(), surname = lastNameList.random(), ID = "1"),
                Professor(name = firstNameList.random(), surname = lastNameList.random(), ID = "2"),
                Professor(name = firstNameList.random(), surname = lastNameList.random(), ID = "3"),
                Professor(name = firstNameList.random(), surname = lastNameList.random(), ID = "4"),
            ),
            "Analisi1" to listOf(
                Professor(name = firstNameList.random(), surname = lastNameList.random(), ID = "1"),
                Professor(name = firstNameList.random(), surname = lastNameList.random(), ID = "2"),
                Professor(name = firstNameList.random(), surname = lastNameList.random(), ID = "3"),
                Professor(name = firstNameList.random(), surname = lastNameList.random(), ID = "4"),
            ),
            "Algoritmi" to listOf(
                Professor(name = firstNameList.random(), surname = lastNameList.random(), ID = "1"),
                Professor(name = firstNameList.random(), surname = lastNameList.random(), ID = "2"),
                Professor(name = firstNameList.random(), surname = lastNameList.random(), ID = "3"),
                Professor(name = firstNameList.random(), surname = lastNameList.random(), ID = "4"),
            ),
        )

        _state.update { it.copy(
            courses = courses,
            professors = professors,
            selectedProfessors = professors,
            currentUserId = userRepository.currentUser?.id ?: ""
        ) }
    }

    fun getCoursesAndProfessors() {

        _state.update { it.copy(loading = true) }

        viewModelScope.launch {
            try {
                val courses = courseRepository.getCourses()
                val professors = teachingRepository.getProfessorsByCourse(courses.map { it.ID })

                _state.update { it.copy(
                    courses = courses,
                    professors = professors,
                ) }
            }
            catch (e: Exception) {
                Log.e("RepetitionsVM", e.stackTraceToString() )
            }
            finally {
                _state.update { it.copy(loading = false) }
            }
        }
    }

    fun updateFilters(
        selectedProfessors: Map<String, List<Professor>>
    ) {
        _state.update { it.copy(
            selectedProfessors = selectedProfessors
        ) }
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