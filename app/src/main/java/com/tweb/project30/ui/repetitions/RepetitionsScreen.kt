package com.tweb.project30.ui.repetitions

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tweb.project30.data.course.Course
import com.tweb.project30.data.professor.Professor
import com.tweb.project30.data.repetition.Repetition
import com.tweb.project30.data.repetition.RepetitionStatus
import com.tweb.project30.data.user.User
import com.tweb.project30.ui.components.AvailableRepetitionCardComponent
import com.tweb.project30.ui.components.RepetitionCardComponent
import com.tweb.project30.ui.components.RepetitionComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
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

    var reserveState by remember {
        mutableStateOf<ReserveUIState?>(null)
    }


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
            onReserveClicked = { date, time, availableProfessors ->
                reserveState = ReserveUIState(date, time, availableProfessors, professors)
            },
            onRepetitionClicked = {}
        )
    }

    if (reserveState != null) {
        RepetitionComponent(
            reserveState!!,
            onBackClicked = {
                reserveState = null
            },
        )
    }

}

    data class ReserveUIState(
        val date: LocalDate,
        val time: Int,
        val availableProfessors: Map<String, List<Professor>>,
        val professors: Map<String, List<Professor>>
    )


@Composable
private fun RepetitionsList(
    courses: List<Course>,
    professors: Map<String, List<Professor>>,
    onReserveClicked: (date: LocalDate, time: Int, availableProfessors: Map<String, List<Professor>>) -> Unit,
    onRepetitionClicked: (Repetition) -> Unit,
) {

    val currentDay = remember {
        mutableStateOf(LocalDate.now())
    }

    val daysOfWeek = remember {
        derivedStateOf {
            getWeekDates(currentDay.value)
        }
    }

    val calendarMode = remember {
        mutableStateOf(CalendarMode.Day)
    }

    val repetitions = mutableListOf<Repetition>()
    val dates = generateUniqueDates(
        LocalDate.now().minusDays(1),
        LocalDate.now().plusDays(7)
    ) // Generate 30 unique dates
    var index = 0
    repeat(80) {
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
            status = RepetitionStatus.values()[it % 3].toString(),
            note = "Note_$it",
            course = Course("Course_${it % 10}", "Course_${it % 10}")
        )
        repetitions.add(repetition)
        index++
    }

    val grouped = repetitions.groupBy { it.date }
    repetitions.sortBy { it.date }

    val listState = rememberLazyListState() // Remember scroll state to scroll to the current day

    Column {

        CalendarToolbar(
            daysOfWeek = daysOfWeek.value,
            currentDay = currentDay.value,
            onDaySelected = {
                currentDay.value = it
            },
            onModeSelected = {
                calendarMode.value = it
            },
        )

        if (calendarMode.value == CalendarMode.Day)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                RepetitionsListView(
                    startDate = LocalDate.now(), // Start from today
                    endDate = maxOf(
                        daysOfWeek.value.last().plusDays(7),
                        currentDay.value
                    ), // Load placeholder repetitions for 7 days after the last day of the week
                    courses = courses,
                    professors = professors,
                    groupedRepetitions = grouped,
                    user = repetitions.first().user!!,
                    currentDayChanged = {
                        currentDay.value = it
                    },
                    currentDay = currentDay.value,
                    listState = listState,
                    onReserveClicked = onReserveClicked,
                    onRepetitionClicked = onRepetitionClicked,
                )
            }


//        if (isNearToEnd.value) {
//            Text(text = "Loading more...", modifier = Modifier.padding(16.dp))
//        }
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
    user: User,
    currentDayChanged: (LocalDate) -> Unit = {},
    currentDay: LocalDate,
    listState: LazyListState,
    onReserveClicked: (
        date: LocalDate,
        time: Int,
        availableProfessors: Map<String, List<Professor>>,
    ) -> Unit,
    onRepetitionClicked: (Repetition) -> Unit = {},
) {

    val isScrollSentProgrammatically = remember {
        mutableStateOf(false)
    }

    val firstVisibleDay: State<LocalDate?> = remember {
        derivedStateOf {
            listState.layoutInfo.visibleItemsInfo
                .firstOrNull { it.key is LocalDate }
                ?.let { it.key as LocalDate } //?: currentDay
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

    val days = remember {
        sortedMapOf<LocalDate, List<Int>>()
    }

    val maxCalculatedDate = remember {
        mutableStateOf(maxOf(LocalDate.now(), endDate))
    }

    LaunchedEffect(endDate) {
        if (endDate.isAfter(maxCalculatedDate.value)) {
            maxCalculatedDate.value = endDate
        }
    }

    LaunchedEffect(maxCalculatedDate.value) {
        var date = days.keys.lastOrNull() ?: startDate

        while (date.isBefore(maxCalculatedDate.value.plusDays(1))) {
            val times = mutableListOf<Int>()
            times.add(14)
            times.add(15)
            times.add(16)
            times.add(17)
            days[date] = times
            date = date.plusDays(1)
        }
    }

    LaunchedEffect(firstVisibleDay.value) {
        if (firstVisibleDay.value != null && !isScrollSentProgrammatically.value) {
            currentDayChanged(firstVisibleDay.value!!)
        }
    }

    LaunchedEffect(currentDay) {
        if (currentDay != firstVisibleDay.value) {
            isScrollSentProgrammatically.value = true
            val scrollJob = launch {
                withContext(Dispatchers.Main) {
                    listState.scrollToItem(days.keys.indexOf(currentDay) * 5)
                }
            }
            scrollJob.join()
            isScrollSentProgrammatically.value = false
        }
    }

    val dateHeaderFormatter = DateTimeFormatter.ofPattern("d MMM", Locale.ITALIAN)

    LazyColumn(
        state = listState
    ) {

        days.forEach { (date, times) ->
            stickyHeader(key = date) {
                Text(
                    text =
                    AnnotatedString(
                        date.format(dateHeaderFormatter),
                        spanStyle = SpanStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    ) +
                            AnnotatedString(" ") +
                            AnnotatedString(
                                date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ITALIAN),
                                spanStyle = SpanStyle(
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                            ),
                    modifier = Modifier
                        .background(Color.White)
                        .padding(start = 16.dp, top = 8.dp, end = 16.dp)
                        .fillMaxWidth()
                        .height(32.dp)
                )
            }


            items(times) { time ->

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {

                    Text(
                        text = "$time:00\n${time.plus(1)}:00",
                        modifier = Modifier.padding(
                            start = 16.dp,
                            top = 8.dp,
                            bottom = 8.dp,
                            end = 8.dp
                        ),
                    )

                    // Get repetition filtered by date and time
                    val repetitionOfCurrentTime =
                        groupedRepetitions[date]?.filter { it.time == time } ?: emptyList()

                    // Get repetition of current time and user
                    val myRepetition =
                        repetitionOfCurrentTime?.firstOrNull {
                            it.IDUser?.equals(user?.id) ?: false
                        }

                    if (myRepetition != null) {

                        RepetitionCardComponent(
                            repetition = myRepetition,
                            onRepetitionClicked = {}
                        )

                    } else {

                        // Get available courses and professors
                        val availableProfessor = professors.toMutableMap()
                        repetitionOfCurrentTime.forEach { rep ->
                            availableProfessor[rep.IDCourse]?.let { professorList ->
                                val filteredProfessors =
                                    professorList.filter { it.ID != rep.IDProfessor }
                                availableProfessor[rep.IDCourse!!] = filteredProfessors
                            }
                        }

                        AvailableRepetitionCardComponent(
                            courses = courses,
                            professors = professors,
                            availableProfessors = availableProfessor,
                            onRepetitionClicked = {
                                onReserveClicked(date, time, availableProfessor)
                            },
                            date = date.atTime(LocalTime.of(time, 0, 0)),
                        )

                    }
                }
            }
        }
    }

}

fun generateUniqueDates(startDate: LocalDate, endDate: LocalDate): List<LocalDate> {
    val daysBetween = ChronoUnit.DAYS.between(startDate, endDate)
    return (0..daysBetween).map { startDate.plusDays(it) }
}

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