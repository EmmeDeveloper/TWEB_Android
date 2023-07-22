package com.tweb.project30.data.repetition

import com.fasterxml.jackson.annotation.JsonProperty
import retrofit2.Response
import retrofit2.http.*

interface RepetitionApiInterface {

    @GET("users/repetitions")
    suspend fun getRepetitions(
        @Query("userID") userID: String,
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String,
    ): Response<RepetitionResponse>

    @GET("repetitions")
    suspend fun getRepetitions(
        @Query("courseIDs") courseIds: List<String>,
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String,
    ): Response<RepetitionResponse>

    @POST("repetitions")
    suspend fun addRepetition(
        @Body repetition: AddRepetitionRequest
    ): Response<Repetition>

    @PUT("repetitions")
    suspend fun updateRepetition(
        @Query("status") newStatus: String,
        @Body info: UpdateRepetitionRequest
    ): Response<Unit>

    @DELETE("repetitions")
    suspend fun deleteRepetition(
        @Query("id") id: String
    ): Response<Unit>

    data class RepetitionResponse(
        @JsonProperty("repetitions") val repetitions: List<Repetition>,
    )

    data class AddRepetitionRequest(
        val idcourse: String,
        val idprofessor: String,
        val date: String,
        val time: Int,
        val note: String? = null,
    )

    data class UpdateRepetitionRequest(
        val id: String,
        val note: String?
    )

}