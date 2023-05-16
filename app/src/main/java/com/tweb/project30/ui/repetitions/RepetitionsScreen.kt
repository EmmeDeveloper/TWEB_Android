package com.tweb.project30.ui.repetitions

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tweb.project30.data.course.Course
import com.tweb.project30.data.professor.Professor
import com.tweb.project30.data.repetition.Repetition
import com.tweb.project30.data.user.User
import java.time.LocalDate


//data class RepetitionsUIState(
//    var courses: List<Course> = emptyList(),
//    var professors: Map<String, List<Professor>> = emptyMap(),
//    var selectedCourses: List<Course> = emptyList(),
//    var selectedProfessors: Map<String, List<Professor>> = emptyMap(),
//    var loading: Boolean = false,
//    var currentUserId: String = ""
//)

@Composable
fun RepetitionsScreen(
    viewModel: RepetitionsViewModel,
    onFilterButtonClicked: () -> Unit
) {
    val uiState = viewModel.uiState.collectAsState()
    RepetitionsScreen(
        uiState,
        onFilterButtonClicked,
        updateFilters = { selectedProfessors ->
            viewModel.updateFilters(selectedProfessors)
        }
    )

}

@Composable
private fun RepetitionsScreen(
    repetitionsState: State<RepetitionsUIState>,
    onFilterButtonClicked: () -> Unit,
    updateFilters: (selectedProfessors: Map<String, List<Professor>>) -> Unit,
) {

    val courses = repetitionsState.value.courses
    val professors = repetitionsState.value.professors
    val selectedProfessors = repetitionsState.value.selectedProfessors

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        RepetitionsFilterToolbar(
            courses = courses,
            professors = professors,
            selectedProfessors = selectedProfessors,
            onFilterButtonClicked = onFilterButtonClicked,
            onClearFilterClicked = { course ->
                updateFilters(
                    selectedProfessors.filter { it.key != course.ID }
                )
            }
        )

        RepetitionsList(
        )
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun RepetitionsList(
) {

    val repetitions = mutableListOf<Repetition>()
    val dates = generateUniqueDates(10) // Generate 30 unique dates
    var index = 0
    repeat(50) {
        val date = dates[index % dates.size]

        val repetition = Repetition(
            ID = "Repetition_$it",
            IDUser = "User_$it",
            IDCourse = "Course_$it",
            IDProfessor = "Professor_$it",
            professor = Professor(it.toString(), "Professor_${it % 10}", "Name_${it % 10}"),
            user = User(it.toString(), "User_${it % 5}", "User", "User_${it % 5}"),
            date = date,
            time = it % 24,
            status = "Scheduled",
            note = "Note_$it",
            course = Course("Course_${it % 10}", "Course_${it % 10}")
        )
        repetitions.add(repetition)
        index++
    }

    val grouped = repetitions.groupBy { it.date }

    repetitions.sortBy { it.date }


    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val currentDay: State<LocalDate?> = remember {
        derivedStateOf {
            listState.layoutInfo.visibleItemsInfo
                .firstOrNull { it.key is LocalDate }
                ?.let { it.key as LocalDate }
        }
    }


    if (currentDay.value != null) {
        Box(
            modifier = Modifier
                .background(Color.Gray)
                .fillMaxWidth()
        ) {
            Text(
                text = currentDay.value.toString(),
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth()
            )
        }
    }

    Spacer(modifier = Modifier.height(8.dp))

    Box {
        LazyColumn(
            state = listState
        ) {
            grouped.forEach { (initial, list) ->
                stickyHeader(key = initial) {
                    Text(
                        text = initial.toString(),
                        modifier = Modifier
                            .background(Color.Gray)
                            .padding(5.dp)
                            .fillMaxWidth()
                    )
                }

                items(list.size) { index ->
                    Text(
                        text = list[index].ID,
                        modifier = Modifier
                            .background(Color.White)
                            .padding(5.dp)
                            .fillMaxWidth()
                    )
                }
            }
        }
    }

}

fun generateUniqueDates(count: Int): List<LocalDate> {
    val currentDate = LocalDate.now()
    return (0 until count).map { currentDate.plusDays(it.toLong()) }
}


