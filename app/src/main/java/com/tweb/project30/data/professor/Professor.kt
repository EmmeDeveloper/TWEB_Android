package com.tweb.project30.data.professor

import com.fasterxml.jackson.annotation.JsonProperty

data class Professor (
    @JsonProperty("id") val ID: String,
    @JsonProperty("surname") val surname: String?,
    @JsonProperty("name") val name: String?
)