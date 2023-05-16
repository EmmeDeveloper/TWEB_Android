package com.tweb.project30.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tweb.project30.data.repetition.Repetition
import com.tweb.project30.data.repetition.RepetitionRepository
import com.tweb.project30.data.user.User
import com.tweb.project30.data.user.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*

data class HomeUIState(
    var isLogged: Boolean = false,
    var loading: Boolean = false,
    var previousRepetition: Repetition? = null,
    var nextRepetition: Repetition? = null,
    var currentUser: User? = null
)

class HomeViewModel(
    private val userRepository: UserRepository,
    private val repetitionRepository: RepetitionRepository
): ViewModel() {

    private val _state = MutableStateFlow(HomeUIState(
        isLogged = userRepository.isLogged.value ?: false,
        currentUser = userRepository.currentUser
    ))

    init {
        UserRepository.isLogged.observeForever { isLogged ->
            _state.update { it.copy(isLogged = isLogged, currentUser = UserRepository.currentUser) }

            if (isLogged) {
                getReservation()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        UserRepository.isLogged.removeObserver { }
    }

    val uiState: MutableStateFlow<HomeUIState>
        get() = _state

    fun getReservation() {
        _state.update { it.copy(loading = true) }

        viewModelScope.launch {
            try {
                val user = userRepository.currentUser

                val start = Calendar.getInstance()
                start.set(2023,1,1)

                val end = Calendar.getInstance()
                end.set(2024,1,1)

                val repetitions = repetitionRepository.getRepetitions(
                    user.id,
                    start.time,
                    end.time
                )

                val previousRepetition = repetitions
                    .filter { it.isBeforeNow() }
                    .sortedByDescending { repetition -> repetition.date?.atTime(repetition.time, 0) }
                    .lastOrNull()

                val nextRepetition = repetitions
                    .filter { it.isAfterNow() }
                    .sortedBy { repetition -> repetition.date?.atTime(repetition.time, 0) }
                    .firstOrNull()

                _state.update { it.copy(
                    previousRepetition = previousRepetition,
                    nextRepetition = nextRepetition
                ) }
            }
            catch (e: Exception) {
                Log.e("HomeVM-getReservation", e.stackTraceToString() )
            }
            finally {
                _state.update { it.copy(loading = false) }
            }
        }


    }

}

class HomeViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")

            Log.e("vm", "creato view")

            return HomeViewModel(
                userRepository = UserRepository,
                repetitionRepository = RepetitionRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}