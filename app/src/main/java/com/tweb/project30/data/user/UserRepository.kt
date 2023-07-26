package com.tweb.project30.data.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tweb.project30.data.login.UserNotFoundException
import com.tweb.project30.util.RetrofitHelper

object UserRepository {

    private val apiService = RetrofitHelper.getInstance().create(UserApiInterface::class.java)
    private val _isLogged by lazy { MutableLiveData<Boolean>() }

    var currentUser = User.Default
        private set

    val isLogged: LiveData<Boolean> = _isLogged

    init {
        currentUser = User.Default
    }

    suspend fun login(account: String, password: String) {
        var result = apiService.login(UserApiInterface.LoginRequest(account, password))

        if (result.isSuccessful) {
            currentUser = result.body()!!.user
           _isLogged.setValue(true)
        }
        else {
            when (result.code()) {
                404 -> throw UserNotFoundException("User not found")
                else -> throw Exception("Invalid result - status: " + result.code())
            }
        }
    }

    suspend fun logout() {

        var result = apiService.logout()

        if (!result.isSuccessful) {
            throw Exception("Invalid result - status: " + result.code())
        }

        currentUser = User.Default
        _isLogged.value = false
    }

}