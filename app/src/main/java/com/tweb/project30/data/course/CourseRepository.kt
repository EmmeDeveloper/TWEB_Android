package com.tweb.project30.data.course

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tweb.project30.util.RetrofitHelper

object CourseRepository {

    private val _courses by lazy { MutableLiveData<List<Course>>() }

    val courses: LiveData<List<Course>> = _courses

    init {
        _courses.value = listOf()
    }

    private val apiService = RetrofitHelper.getInstance().create(CourseApiInterface::class.java)

    suspend fun getCourses() : List<Course> {
        var result = apiService.getCourses()
        if (result.isSuccessful) {
            _courses.postValue(result.body()!!.courses)
            return result.body()!!.courses
        }
        else {
            throw Exception("Invalid result - status: " + result.code())
        }
    }

}