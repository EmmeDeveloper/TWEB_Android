package com.tweb.project30.data.teaching

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tweb.project30.data.professor.Professor
import com.tweb.project30.util.RetrofitHelper

object TeachingRepository {

    private val _coursesProfessors by lazy { MutableLiveData<Map<String, List<Professor>>>() }

    val coursesProfessors: LiveData<Map<String, List<Professor>>> = _coursesProfessors

    init {
        _coursesProfessors.value = mapOf()
    }

    private val apiService = RetrofitHelper.getInstance().create(TeachingApiInterface::class.java)

    suspend fun getProfessorsByCourse(courseIds: List<String>) : Map<String, List<Professor>> {
        var result = apiService.getProfessorsByCourses(courseIds)
        if (result.isSuccessful) {
            _coursesProfessors.postValue(result.body()!!.professors)
            return result.body()!!.professors
        }
        else {
            throw Exception("Invalid result - status: " + result.code())
        }
    }

    fun getProfessorsByCourse(courseId: String) : List<Professor> {
        return _coursesProfessors.value!![courseId]!!
    }

    fun getCoursesByProfessor(professorId: String) : List<String> {
        val courses = mutableListOf<String>()
        _coursesProfessors.value!!.forEach { (courseId, professors) ->
            if (professors.any { it.ID == professorId }) {
                courses.add(courseId)
            }
        }
        return courses
    }

}