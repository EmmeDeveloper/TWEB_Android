package com.tweb.project30.ui.repetitions

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tweb.project30.data.repetition.Repetition
import com.tweb.project30.data.repetition.RepetitionRepository
import com.tweb.project30.data.user.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Calendar
import java.util.Date

class UserRepetitionsViewModel(
    private val repetitionRepository: RepetitionRepository,
) : ViewModel(
) {
    private val _state = MutableStateFlow(RepetitionsUIState())

    val uiState: StateFlow<RepetitionsUIState>
        get() = _state

    init {
        UserRepository.isLogged.observeForever { isLogged ->

            if (isLogged) {
                _state.update { it.copy(currentUserId = UserRepository.currentUser!!.id) }
                getMyRepetitions(firstDateOfYear(), lastDateOfYear())
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        UserRepository.isLogged.removeObserver { }
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


    private fun firstDateOfYear(): Date {
        val date = Calendar.getInstance()
        date.set(LocalDate.now().year, 1, 1)
        return date.time
    }

    private fun lastDateOfYear(): Date {
        val date = Calendar.getInstance()
        date.set(LocalDate.now().year, 12, 31)
        return date.time
    }


}

class UserRepetitionsViewModelFactory() :
    ViewModelProvider.Factory {


    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserRepetitionsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")

            Log.e("vm", "creato view")

            return UserRepetitionsViewModel(
                repetitionRepository = RepetitionRepository,
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
