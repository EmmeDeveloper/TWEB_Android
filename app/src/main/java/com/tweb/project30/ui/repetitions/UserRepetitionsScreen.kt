package com.tweb.project30.ui.repetitions

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tweb.project30.R
import com.tweb.project30.data.repetition.Repetition
import com.tweb.project30.data.user.UserRepository
import com.tweb.project30.ui.components.RepetitionCardComponent
import com.tweb.project30.ui.components.RepetitionComponent
import com.tweb.project30.util.supportWideScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale


@Composable
fun UserRepetitionsScreen(
    viewModel: UserRepetitionsViewModel,
    onLoginClicked: () -> Unit,
) {

    val uiState = viewModel.uiState.collectAsState()

    if (UserRepository.isLogged.value != true) {
        NonLoggedRepetitionsScreen(onLoginClicked)

    } else {
        UserRepetitionsScreen(
            uiState,
            deleteRepetition = { repetition ->
                viewModel.deleteRepetition(repetition)
            },
            updateRepetition = { repetition, status, note ->
                viewModel.updateRepetition(repetition, status, note)
            },
        )
    }
}

@Composable
private fun NonLoggedRepetitionsScreen(
    onLoginClicked: () -> Unit,
) {

    Surface(
        modifier = Modifier
            .supportWideScreen()
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val image: Painter = painterResource(R.drawable.isometric_login)
            Image(
                painter = image,
                contentDescription = "App Logo",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            )

            Text(
                text = "Le tue ripetizioni",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(top = 16.dp, bottom = 8.dp)
                    .fillMaxWidth()
            )

            Text(
                text = "Qui troverai le tue ripetizioni, per poterle gestire in modo semplice e veloce. Effettua l'accesso per visualizzarle",
                fontSize = 16.sp,
                modifier = Modifier
                    .padding(top = 8.dp, bottom = 8.dp)
                    .fillMaxWidth()
            )

            Button(
                onClick = { onLoginClicked() },
                modifier = Modifier
                    .padding(top = 8.dp, bottom = 8.dp)
                    .fillMaxWidth()
            ) {
                Text(text = "Accedi")
            }
        }
    }
}

@Composable
private fun UserRepetitionsScreen(
    repetitionsState: State<RepetitionsUIState>,
    deleteRepetition: (repetition: Repetition) -> Unit,
    updateRepetition: (repetition: Repetition, status: String, note: String?) -> Unit,
) {

    var selectedRepetitionState by remember {
        mutableStateOf<SelectedRepetitionUIState?>(null)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {

        RepetitionsList(
            onRepetitionClicked = {
                selectedRepetitionState =
                    SelectedRepetitionUIState(it, repetitionsState.value.loading, false)
            },
            repetitions = repetitionsState.value.myRepetitions
        )
    }

    if (selectedRepetitionState != null) {
        RepetitionComponent(
            selectedRepetitionState!!,
            onBackClicked = {
                selectedRepetitionState = null
                repetitionsState.value.lastRepetitionUpdated = null
            },
            onDeleteRepetition = { deleteRepetition(selectedRepetitionState!!.repetition) },
            onUpdateRepetition = { status, note ->
                updateRepetition(
                    selectedRepetitionState!!.repetition,
                    status,
                    note
                )
            }
        )
    }

    LaunchedEffect(repetitionsState.value.loading) {
        selectedRepetitionState =
            selectedRepetitionState?.copy(isLoading = repetitionsState.value.loading)
    }

    LaunchedEffect(repetitionsState.value.lastRepetitionUpdated) {

        // Gestione pagina conferma e cancellazione
        if (
            repetitionsState.value.lastRepetitionUpdated != null &&
            selectedRepetitionState != null &&
            repetitionsState.value.lastRepetitionUpdated!!.ID == selectedRepetitionState!!.repetition.ID
        ) {
            selectedRepetitionState = selectedRepetitionState!!.copy(wasOperationCompleted = true)
        }

        if (repetitionsState.value.lastRepetitionUpdated == null) {
            selectedRepetitionState = selectedRepetitionState?.copy(wasOperationCompleted = false)
        }
    }
}

@Composable
private fun RepetitionsList(
    onRepetitionClicked: (Repetition) -> Unit,
    repetitions: List<Repetition>
) {

    val currentDay = remember {
        mutableStateOf(
            repetitions.filter { it.date >= LocalDate.now() }.minByOrNull { it.date }
                ?.date ?: repetitions.maxByOrNull { it.date }?.date ?: LocalDate.now()
        )
    }

    val daysOfWeek = remember {
        derivedStateOf {
            getWeekDates(currentDay.value)
        }
    }


    val calendarMode = remember {
        mutableStateOf(CalendarMode.Day)
    }
    val grouped = repetitions.groupBy { it.date }

    val listState = rememberLazyListState() // Remember scroll state to scroll to the current day

    Column {

        Spacer(modifier = Modifier.height(16.dp))

        CalendarToolbar(
            daysOfWeek = daysOfWeek.value,
            currentDay = currentDay.value,
            onDaySelected = {
                currentDay.value = it
            },
            onModeSelected = {
                calendarMode.value = it
            },
            mode = RepetitionMode.MY_REPETITIONS,
            selectableDays = grouped.keys.toList(),
        )

        if (calendarMode.value == CalendarMode.Day)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {

                if (grouped.isEmpty()) {
                    Text(
                        AnnotatedString("Non hai ripetizioni in\n programma\n") +
                                AnnotatedString(
                                    "\n \uD83D\uDE41",
                                    spanStyle = SpanStyle(
                                        fontSize = 32.sp,
                                    )
                                ),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, top = 40.dp, end = 16.dp)
                    )
                }

                UserRepetitionListView(
                    groupedRepetitions = grouped,
                    currentDayChanged = {
                        currentDay.value = it
                    },
                    currentDay = currentDay.value,
                    listState = listState,
                    onRepetitionClicked = onRepetitionClicked,
                )
            }
    }


}


@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun UserRepetitionListView(
    groupedRepetitions: Map<LocalDate, List<Repetition>>,
    currentDayChanged: (LocalDate) -> Unit = {},
    currentDay: LocalDate,
    listState: LazyListState,
    onRepetitionClicked: (Repetition) -> Unit,
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
                    var sizeBeforeCurrentKey = 0
                    var currentIndex = 0
                    for (key in groupedRepetitions.keys) {
                        if (key == currentDay) {
                            currentIndex = sizeBeforeCurrentKey
                            break
                        }
                        sizeBeforeCurrentKey += groupedRepetitions[key]?.size ?: 0
                    }
                    val index = currentIndex + sizeBeforeCurrentKey
                    listState.scrollToItem(index)
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

        groupedRepetitions.forEach { (date, repetitions) ->
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

            items(repetitions) { repetition ->

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {

                    Text(
                        text = "${repetition.time}:00\n${repetition.time.plus(1)}:00",
                        modifier = Modifier.padding(
                            start = 16.dp,
                            top = 8.dp,
                            bottom = 8.dp,
                            end = 8.dp
                        ),
                    )

                    RepetitionCardComponent(
                        repetition = repetition,
                        onRepetitionClicked = { onRepetitionClicked(repetition) }
                    )
                }
            }
        }
    }
}

