package com.tweb.project30.data.course

import com.fasterxml.jackson.annotation.JsonProperty

data class Course (
    @JsonProperty("id") val ID: String,
    @JsonProperty("title") val title: String?
)