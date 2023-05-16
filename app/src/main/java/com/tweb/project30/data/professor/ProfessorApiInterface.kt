package com.tweb.project30.data.professor

import com.fasterxml.jackson.annotation.JsonProperty
import retrofit2.Response
import retrofit2.http.GET

interface ProfessorApiInterface {
    @GET("professors")
    suspend fun getProfessors() : Response<ProfessorsResponse>

    data class ProfessorsResponse(
        @JsonProperty("professors") val professors: List<Professor>,
    )
}