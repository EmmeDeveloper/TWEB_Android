package com.tweb.project30.data.repetition

import com.fasterxml.jackson.annotation.JsonProperty
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RepetitionApiInterface {

    @GET("users/repetitions")
    suspend fun getRepetitions(
        @Query("userID") userID: String,
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String,
    ) : Response<RepetitionResponse>

    data class RepetitionResponse(
        @JsonProperty("repetitions") val repetitions: List<Repetition>,
    )

}