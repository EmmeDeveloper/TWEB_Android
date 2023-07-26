package com.tweb.project30.data.user

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class User(
    @JsonProperty("id") val id: String,
    @JsonProperty("email") val email: String,
    @JsonProperty("role") val role: String,
    @JsonProperty("account") val account: String,

    @JsonProperty("name") val name: String? = "Marco",
    @JsonProperty("surname") val surname: String? = "Molica",
    @JsonProperty("address") val birthDate: String? = "21/11/1996",
    @JsonProperty("birthDate") val address: String? = "Via alcide de gasperi",
    @JsonProperty("phone") val phone: String? = "3398987827",
    @JsonProperty("memberSince") val memberSince: String? = "13/01/2023",
)  {
    companion object {
        val Default = User("", "", "", "");
    }
}
