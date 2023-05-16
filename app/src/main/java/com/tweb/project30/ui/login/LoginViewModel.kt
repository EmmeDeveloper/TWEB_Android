package com.tweb.project30.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tweb.project30.data.login.UserNotFoundException
import com.tweb.project30.data.user.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class LoginRequestState {
    NONE,
    WRONG_PSW_OR_EMAIL,
    INTERNET_ERROR
}

data class LoginUIState(
    var account: String = "giovanni", // TODO: remove default account
    var password: String = "pass", // TODO: remove default password
    var loading: Boolean = false,
    var requestState: LoginRequestState = LoginRequestState.NONE,

)

class LoginViewModel(
    private val userRepository: UserRepository
) : ViewModel()
{

    private val _state = MutableStateFlow(LoginUIState())

    val uiState: StateFlow<LoginUIState>
        get() = _state

    fun login(
        account: String,
        password: String,
        onLoginCompleted: () -> Unit
    ) {
        _state.update { it.copy(requestState = LoginRequestState.NONE, loading = true) }

        viewModelScope.launch {
            var state = LoginRequestState.NONE
            try {
                userRepository.login(account, password)
                onLoginCompleted()
            }
            catch (e: UserNotFoundException) {
                // Wrong email or password
                state = LoginRequestState.WRONG_PSW_OR_EMAIL
                Log.e("LoginVM-Login", e.stackTraceToString() )
            }
            catch (e: Exception) {
                // Internet error
                state = LoginRequestState.INTERNET_ERROR
                Log.e("LoginVM-Login", e.stackTraceToString() )
            }
            finally {
                _state.update { it.copy(loading = false, requestState = state) }
            }
        }
    }

    fun onPasswordChange(
        psw: String
    ) {
        _state.update { it.copy(password = psw, requestState = LoginRequestState.NONE) }
    }

    fun onAccountChange(
        account: String
    ) {
        _state.update { it.copy(account = account, requestState = LoginRequestState.NONE) }
    }

    fun resetRequestState() {
        _state.update { it.copy(requestState = LoginRequestState.NONE) }
    }
}

class LoginViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")

            Log.e("vm", "creato view")

            return LoginViewModel(userRepository = UserRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

