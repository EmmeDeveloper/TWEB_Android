package com.tweb.project30.data.repetition

import com.fasterxml.jackson.annotation.JsonProperty
import com.tweb.project30.data.course.Course
import com.tweb.project30.data.professor.Professor
import com.tweb.project30.data.user.User
import java.time.LocalDate
import java.time.LocalDateTime

enum class RepetitionStatus(val value: String) {
    PENDING("pending"),
    DONE("done"),
    DELETED("deleted")
}

data class Repetition (
    @JsonProperty("id") val ID: String,
    @JsonProperty("professor") val professor: Professor?  = null,
    @JsonProperty("user") val user: User?  = null,
    @JsonProperty("course") val course: Course? = null,
    @JsonProperty("date") val date: LocalDate,
    @JsonProperty("time") val time: Int,
    @JsonProperty("status") val status: String?, // Could be an enum. Is null when the repetition is not assigned to current user
    @JsonProperty("note") val note: String? = null,

    ) {

    val IDUser: String? get() = user?.id
    val IDCourse: String? get() = course?.ID
    val IDProfessor: String? get() = professor?.ID

    fun isBeforeNow(): Boolean {
        val currentDateTime = LocalDateTime.now()
        return date != null && (date.isBefore(currentDateTime.toLocalDate())
                || (date == currentDateTime.toLocalDate() && time < currentDateTime.hour))
    }

    fun isAfterNow(): Boolean {
        val currentDateTime = LocalDateTime.now()
        return date != null && (date.isAfter(currentDateTime.toLocalDate())
                || (date == currentDateTime.toLocalDate() && time > currentDateTime.hour))
    }
}