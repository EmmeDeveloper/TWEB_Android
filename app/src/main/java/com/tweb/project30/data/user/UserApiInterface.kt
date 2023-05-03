package com.tweb.project30.data.user

import com.fasterxml.jackson.annotation.JsonProperty
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UserApiInterface {

    @POST("login")
    suspend fun login(@Body request: LoginRequest) : Response<LoginResponse>
    data class LoginRequest(val account: String, val password: String)
    data class LoginResponse(
        @JsonProperty("user") val user: User,
    )

}

