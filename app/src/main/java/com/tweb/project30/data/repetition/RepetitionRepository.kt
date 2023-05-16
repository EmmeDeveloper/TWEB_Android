package com.tweb.project30.data.repetition

import com.tweb.project30.util.RetrofitHelper
import java.text.SimpleDateFormat
import java.util.*

object RepetitionRepository {


    private val apiService = RetrofitHelper.getInstance().create(RepetitionApiInterface::class.java)

    suspend fun getRepetitions(userID: String, startDate: Date, endDate: Date) : List<Repetition> {

        val format = SimpleDateFormat("yyyy-MM-dd")
        var result = apiService.getRepetitions(userID, format.format(startDate), format.format(endDate))
        if (result.isSuccessful) {
            return result.body()!!.repetitions
        }
        else {
            throw Exception("Invalid result - status: " + result.code())
        }
    }
}