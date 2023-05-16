package com.tweb.project30.data.professor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tweb.project30.util.RetrofitHelper

object ProfessorRepository {

    private val _professors by lazy { MutableLiveData<List<Professor>>() }

    val professors: LiveData<List<Professor>> = _professors

    init {
        _professors.value = listOf()
    }

    private val apiService = RetrofitHelper.getInstance().create(ProfessorApiInterface::class.java)

    suspend fun getProfessors() : List<Professor> {
        var result = apiService.getProfessors()
        if (result.isSuccessful) {
            _professors.postValue(result.body()!!.professors)
            return result.body()!!.professors
        }
        else {
            throw Exception("Invalid result - status: " + result.code())
        }
    }

}