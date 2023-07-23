package com.tweb.project30.ui.repetitions

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tweb.project30.data.course.Course
import com.tweb.project30.data.professor.Professor


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepetitionsFilterToolbar(
    courses: List<Course> = emptyList(),
    professors: Map<String, List<Professor>> = emptyMap(),
    selectedProfessors: Map<String, List<Professor>> = emptyMap(),
    onFilterButtonClicked: () -> Unit,
    onClearFilterClicked: (course: Course) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // Filter
        Row(
            modifier = Modifier
                .background(Color.White)
                .horizontalScroll(rememberScrollState())
                .weight(1f)
        ) {

            for (course in selectedProfessors.keys.map { courses.find { c -> c.ID == it }!! }) {
                CourseChip(
                    course = course,
                    selectedProfCount = selectedProfessors[course.ID]?.size ?: 0,
                    maxProfCount = professors[course.ID]?.size ?: 0,
                    onClick = {
                        onClearFilterClicked(course)
                    },
                    enabled = selectedProfessors.keys.size > 1
                )
            }
        }

        // Filter button
        Icon(
            Icons.Filled.Tune,
            contentDescription = "Filter",
            modifier = Modifier
                .size(44.dp)
                .padding(8.dp)
                .clickable {
                    onFilterButtonClicked()
                }
//            tint = Color.White
        )
    }
}


@Composable
@ExperimentalMaterial3Api
fun CourseChip(
    course: Course,
    selectedProfCount: Int = 0,
    maxProfCount: Int = 0,
    onClick: () -> Unit,
    enabled: Boolean
) {
    InputChip(
        modifier = Modifier.padding(start = 16.dp),
        selected = true,
        onClick = {
            if (enabled) onClick()
        },
        enabled = enabled,
        label = {
            Column(modifier = Modifier.padding(8.dp)) {
                Text(text = course.title!!, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        shape = RoundedCornerShape(24.dp),
        trailingIcon = {
            Icon(
                Icons.Filled.Clear,
                contentDescription = "Clear",
                modifier = Modifier
                    .size(24.dp)
                    .background(
                        color = if (enabled) MaterialTheme.colorScheme.primary else Color.LightGray,
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(4.dp),
                tint = Color.White
            )
        },
        leadingIcon = {
            Icon(
                Icons.Filled.Group,
                contentDescription = "Professors",
            )
            Text(
                text = "$selectedProfCount/$maxProfCount",
                fontSize = 16.sp,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    )
}