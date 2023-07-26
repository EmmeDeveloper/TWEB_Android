package com.tweb.project30.data.repetition

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tweb.project30.util.RetrofitHelper
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.Date

object RepetitionRepository {

    private val apiService = RetrofitHelper.getInstance().create(RepetitionApiInterface::class.java)

    private val _globalRepetitions by lazy { MutableLiveData<List<Repetition>>() }
    private val _userRepetitions by lazy { MutableLiveData<List<Repetition>>() }
    val userRepetitions: LiveData<List<Repetition>>
        get() = _userRepetitions

    val globalRepetitions: LiveData<List<Repetition>>
        get() = _globalRepetitions


    suspend fun getRepetitions(userID: String, startDate: Date, endDate: Date) {

        if (userID.isNullOrEmpty()) {
            _userRepetitions.value = listOf()
            return
        }

        val format = SimpleDateFormat("yyyy-MM-dd")
        var result =
            apiService.getRepetitions(userID, format.format(startDate), format.format(endDate))
        if (result.isSuccessful) {
            _userRepetitions.setValue(result.body()!!.repetitions)
        } else {
            throw Exception("Invalid result - status: " + result.code())
        }
    }

    suspend fun getRepetitions(
        courseIDs: List<String>,
        startDate: Date,
        endDate: Date
    ) {

        if (courseIDs.isNullOrEmpty()) {
            _globalRepetitions.value = listOf()
            return
        }

        val format = SimpleDateFormat("yyyy-MM-dd")
        var result =
            apiService.getRepetitions(courseIDs, format.format(startDate), format.format(endDate))
        if (result.isSuccessful) {
            _globalRepetitions.setValue(result.body()!!.repetitions)
        } else {
            throw Exception("Invalid result - status: " + result.code())
        }
    }

    suspend fun addRepetition(repetition: Repetition): Repetition {
        var result = apiService.addRepetition(
            RepetitionApiInterface.AddRepetitionRequest(
                repetition.IDCourse!!,
                repetition.IDProfessor!!,
                repetition.date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                repetition.time,
                repetition.note
            )
        )
        if (result.isSuccessful) {
            return result.body()!!
        } else {
            throw Exception("Invalid result - status: " + result.code())
        }
    }

    suspend fun deleteRepetition(repetitionID: String) {
        var result = apiService.deleteRepetition(repetitionID)
        if (!result.isSuccessful) {
            throw Exception("Invalid result - status: " + result.code())
        }
    }

    suspend fun updateRepetition(repetitionID: String, newStatus: String, note: String?) {
        var result = apiService.updateRepetition(
            newStatus, RepetitionApiInterface.UpdateRepetitionRequest(
                repetitionID,
                note
            )
        )
        if (!result.isSuccessful) {
            throw Exception("Invalid result - status: " + result.code())
        }
    }
}