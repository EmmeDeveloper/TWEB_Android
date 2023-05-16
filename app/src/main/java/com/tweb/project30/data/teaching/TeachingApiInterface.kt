package com.tweb.project30.data.teaching

import com.fasterxml.jackson.annotation.JsonProperty
import com.tweb.project30.data.professor.Professor
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface TeachingApiInterface {
    @GET("courses/professors")
    suspend fun getProfessorsByCourses(
        @Query("ids") courseIds: List<String>
    ) : Response<ProfessorsByCoursesResponse>

    data class ProfessorsByCoursesResponse(
        @JsonProperty("professors") val professors: Map<String, List<Professor>>,
    )
}