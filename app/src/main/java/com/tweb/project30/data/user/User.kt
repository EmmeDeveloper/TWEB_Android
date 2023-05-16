package com.tweb.project30.data.user

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class User(
    @JsonProperty("id") val id: String,
    @JsonProperty("email") val email: String,
    @JsonProperty("role") val role: String,
    @JsonProperty("account") val account: String
)  {
    companion object {
        val Default = User("", "", "", "");
    }
}
