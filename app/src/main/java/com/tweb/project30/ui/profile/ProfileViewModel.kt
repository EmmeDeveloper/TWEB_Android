package com.tweb.project30.ui.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tweb.project30.data.repetition.RepetitionRepository
import com.tweb.project30.data.user.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date


data class ProfileUIState(
    var loading: Boolean = false,
)

class ProfileViewModel() : ViewModel() {

    private val _state = MutableStateFlow(ProfileUIState())

    val uiState: StateFlow<ProfileUIState>
        get() = _state


    fun logout(
        onLogoutCompleted: () -> Unit
    ) {

        _state.update { it.copy(loading = true) }

        viewModelScope.launch {
            try {
                UserRepository.logout()
                RepetitionRepository.getRepetitions("", Date(), Date())
                onLogoutCompleted()
            } finally {
                _state.update { it.copy(loading = false) }
            }

        }
    }

}

class ProfileViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")

            Log.e("vm", "creato view")

            return ProfileViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}