package com.tweb.project30.ui.repetitions

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tweb.project30.data.course.Course
import com.tweb.project30.data.professor.Professor
import com.tweb.project30.data.repetition.Repetition
import com.tweb.project30.data.user.User
import com.tweb.project30.ui.components.AvailableRepetitionComponent
import com.tweb.project30.ui.components.RepetitionComponent
import com.tweb.project30.ui.theme.MontserratFontFamily
import kotlinx.coroutines.CoroutineScope
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import java.util.*


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
            courses = courses,
            professors = selectedProfessors,
        )
    }
}


@Composable
private fun RepetitionsList(
    courses: List<Course>,
    professors: Map<String, List<Professor>>,
) {

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val currentDay: State<LocalDate?> = remember {
        derivedStateOf {
            listState.layoutInfo.visibleItemsInfo
                .firstOrNull { it.key is LocalDate }
                ?.let { it.key as LocalDate }
        }
    }

    val isNearToEnd: State<Boolean> = remember {
        derivedStateOf {
            val itemCount = listState.layoutInfo.totalItemsCount
            val lastVisibleIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
            val threshold = 3

            itemCount - lastVisibleIndex <= threshold
        }
    }

    val daysOfWeek = remember {
        derivedStateOf {
            getWeekDates(currentDay.value ?: LocalDate.now())
        }
    }


    val repetitions = mutableListOf<Repetition>()
    val dates = generateUniqueDates(daysOfWeek.value.first(), daysOfWeek.value.last()) // Generate 30 unique dates
    var index = 0
    repeat(50) {
        val date = dates[index % dates.size]

        val user = User("${it % 2}", "User_${it % 5}", "User", "User_${it % 5}")
        val course = courses[it % courses.size]
        val professor = professors[course.ID]!![it % professors[course.ID]!!.size]

        val repetition = Repetition(
            ID = "Repetition_$it",
            IDUser = user.id,
            IDCourse = course.ID,
            IDProfessor = professor.ID,
            professor = professor,
            user = user,
            date = date,
            time = (it % 4) + 14,
            status = "Scheduled",
            note = "Note_$it",
            course = Course("Course_${it % 10}", "Course_${it % 10}")
        )
        repetitions.add(repetition)
        index++
    }

    val grouped = repetitions.groupBy { it.date }
    repetitions.sortBy { it.date }


    Column {

        CalendarWeek(
            daysOfWeek = daysOfWeek.value,
            currentDay = currentDay,
            onDaySelected = {}
        )

        Spacer(modifier = Modifier.height(32.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            RepetitionsListView(
                startDate = daysOfWeek.value.first(),
                endDate = daysOfWeek.value.last(),
                courses = courses,
                professors = professors,
                groupedRepetitions = grouped,
                listState = listState,
                coroutineScope = coroutineScope,
                isNearToEnd = isNearToEnd,
                user = repetitions.first().user!!,
            )
        }



        if (isNearToEnd.value) {
            Text(text = "Loading more...", modifier = Modifier.padding(16.dp))
        }
    }
}

@Composable
fun CalendarWeek(
    daysOfWeek: List<LocalDate>,
    currentDay: State<LocalDate?>,
    onDaySelected: (LocalDate) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            daysOfWeek.forEach {

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = it.dayOfWeek.getDisplayName(
                            TextStyle.NARROW,
                            Locale("it", "IT")
                        ).uppercase(),
                        modifier = Modifier
                            .padding(4.dp),
                        fontFamily = MontserratFontFamily,
                    )

                    val isToday = it == LocalDate.now()
                    val isSelected = it == currentDay.value

                    val backgroundColor = when {
                        isToday && !isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                        isSelected -> MaterialTheme.colorScheme.primary
                        else -> Color.Transparent
                    }

                    Box(
                        modifier = Modifier
                            .background(backgroundColor, CircleShape)
                            .fillMaxWidth()
                            .aspectRatio(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = it.dayOfMonth.toString(),
                            modifier = Modifier
                                .wrapContentSize(Alignment.Center),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else Color.Black
                        )
                    }

//                        Text(
//                            text = it.dayOfMonth.toString(),
//                            modifier = Modifier
//                                .background(
//                                    backgroundColor,
//                                    CircleShape
//                                )
//                                .fillMaxWidth()
//                                .aspectRatio(1f),
//                            textAlign = TextAlign.Center,
//                            fontWeight = FontWeight.Bold,
//                            fontFamily = MontserratFontFamily,
//                            color = if (isSelected) Color.White else Color.Black
//                        )
                }

            }

            // Filter button
            Icon(
                Icons.Filled.CalendarMonth,
                contentDescription = "Filter",
                modifier = Modifier
                    .size(44.dp)
                    .padding(8.dp)
                    .clickable {
//                            onFilterButtonClicked()
                    }
            )
        }

    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RepetitionsListView(
    startDate: LocalDate,
    endDate: LocalDate,
    courses: List<Course>,
    professors: Map<String, List<Professor>>,
    groupedRepetitions: Map<LocalDate, List<Repetition>>,
    listState: LazyListState,
    coroutineScope: CoroutineScope,
    isNearToEnd: State<Boolean>,
    user: User
) {

    val days = remember {
        mutableStateMapOf<LocalDate, List<Int>>().apply {
            var date = startDate
            while (date.isBefore(endDate)) {
                val times = mutableListOf<Int>()
                times.add(14)
                times.add(15)
                times.add(16)
                times.add(17)
                put(date, times)
                date = date.plusDays(1)
            }
        }
    }

    LazyColumn(
        state = listState
    ) {

        days.forEach { (date, times) ->
            stickyHeader(key = date) {
                Text(
                    text = date.toString(),
                    modifier = Modifier
                        .background(Color.White)
                        .padding(8.dp)
                        .fillMaxWidth()
                        .height(32.dp)
                )
            }


            items(times) { time ->

                // Get repetition filtered by date and time
                val repetitionOfCurrentTime = groupedRepetitions[date]?.filter { it.time == time } ?: emptyList()

                // Get repetition of current time and user
                val myRepetition = repetitionOfCurrentTime?.firstOrNull { it.IDUser?.equals(user?.id) ?: false }

                if (myRepetition != null) {

                    RepetitionComponent(
                        repetition = myRepetition,
                        onRepetitionClicked = {}
                    )

                } else {

                    // Get available courses and professors
                    val availableProfessor = professors.toMutableMap()
                    repetitionOfCurrentTime.forEach { rep ->
                        availableProfessor[rep.IDCourse]?.let { professorList ->
                            val filteredProfessors = professorList.filter { it.ID != rep.IDProfessor }
                            availableProfessor[rep.IDCourse!!] = filteredProfessors
                        }
                    }

                    AvailableRepetitionComponent(
                        courses = courses,
                        professors = professors,
                        availableProfessors = availableProfessor,
                        onRepetitionClicked = {}
                    )

                }

            }
        }
    }

}

fun generateUniqueDates(startDate: LocalDate, endDate: LocalDate): List<LocalDate> {
    val daysBetween = ChronoUnit.DAYS.between(startDate, endDate)
    return (0..daysBetween).map { startDate.plusDays(it) }
}

//fun calendarDates(): Map<Int, List<LocalDate>> {
//    val calendarDates = mutableListOf<LocalDate>()
//    var currentDate = LocalDate.of(2023, 1, 1)
//
//    while (!currentDate.isAfter(LocalDate.of(2023, 12, 31))) {
//        if (currentDate.dayOfWeek.value < 6) {
//            calendarDates.add(currentDate)
//        }
//        currentDate = currentDate.plusDays(1)
//    }
//
//    return calendarDates.groupBy { it.get(WeekFields.ISO.weekOfWeekBasedYear()) }
//}

fun getWeekDates(currentDay: LocalDate): List<LocalDate> {
    val startOfWeek = currentDay.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    val endOfWeek = currentDay.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))

    val weekDates = mutableListOf<LocalDate>()
    var currentDate = startOfWeek

    while (!currentDate.isAfter(endOfWeek)) {
        weekDates.add(currentDate)
        currentDate = currentDate.plusDays(1)
    }

    return weekDates
}