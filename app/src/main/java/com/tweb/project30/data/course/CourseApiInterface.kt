package com.tweb.project30.data.course

import com.fasterxml.jackson.annotation.JsonProperty
import retrofit2.Response
import retrofit2.http.GET


interface CourseApiInterface {

    @GET("courses")
    suspend fun getCourses() : Response<CoursesResponse>

    data class CoursesResponse(
        @JsonProperty("courses") val courses: List<Course>,
    )

}