package com.tweb.project30.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tweb.project30.data.professor.Professor
import com.tweb.project30.data.repetition.RepetitionStatus
import com.tweb.project30.ui.repetitions.ReserveUIState
import com.tweb.project30.ui.repetitions.SelectedRepetitionUIState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalLayoutApi::class
)
@Composable
fun ReservationComponent(
    state: ReserveUIState,
    onBackClicked: () -> Unit,
    onReserve: (subject: String, profId: String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var selectedSubjectProf = remember {
        mutableStateOf(Pair("", ""))
    }

    ModalBottomSheet(
        onDismissRequest = onBackClicked,
        sheetState = sheetState,
        containerColor = Color.White
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {

            Details(time = state.time, date = state.date)

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 16.dp, start = 8.dp, end = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                state.availableProfessors.forEach() { subject, professors ->
                    if (!professors.isEmpty())
                        stickyHeader {
                            state.courses.first { it.ID == subject }!!.title?.let {
                                Text(
                                    text = it,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            Color.White
                                        ),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }

                    item {
                        FlowRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 8.dp)
                        ) {
                            professors.forEach() { professor ->
                                FilterChip(
                                    modifier = Modifier.padding(horizontal = 4.dp),
                                    selected = selectedSubjectProf.value.first == subject && selectedSubjectProf.value.second == professor.ID,
                                    onClick = {
                                        selectedSubjectProf.value =
                                            Pair(subject, professor.ID)
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        containerColor = MaterialTheme.colorScheme.primary.copy(
                                            alpha = 0.12f
                                        ),
                                        labelColor = Color.Black,
                                        selectedContainerColor = MaterialTheme.colorScheme.primary.copy(
                                            alpha = 0.7f
                                        ),
                                        selectedLabelColor = Color.White
                                    ),
                                    label = { Text("${professor.name} ${professor.surname}") },
                                     enabled = !state.isLoading && !state.reserveSuccess
                                )
                            }
                        }
                    }

                }

            }

            if (state.reserveSuccess) {
                Text(
                    text = "Prenotazione effettuata con successo!",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp, horizontal = 16.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            } else LoadingButton(
                onClick = {
                    onReserve(
                        selectedSubjectProf.value.first,
                        selectedSubjectProf.value.second
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 16.dp),
                enabled = selectedSubjectProf.value.first != "" && selectedSubjectProf.value.second != "",
                loading = state.isLoading,
            ) {
                Text(text = "Prenota")
            }
        }
    }


    LaunchedEffect(Unit) {
        sheetState.show()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepetitionComponent(
    state: SelectedRepetitionUIState,
    onUpdateRepetition: (status: String, note: String?) -> Unit,
    onDeleteRepetition: () -> Unit,
    onBackClicked: () -> Unit
) {

    val repetition = state.repetition
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onBackClicked,
        sheetState = sheetState,
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {

            val showingIssue = remember {
                mutableStateOf(false)
            }

            if (!showingIssue.value) {
                Details(
                    if (repetition.isBeforeNow() && repetition.status!! == RepetitionStatus.PENDING.value) "Hai effettuato la lezione?" else "La tua lezione",
                    time = repetition.time,
                    date = repetition.date,
                    subject = repetition.course?.title,
                    professor = repetition.professor
                )
            }

            if (repetition.status!! == RepetitionStatus.PENDING.value) {
                if (repetition.isBeforeNow()) {
                    if (!showingIssue.value) {
                        PassedRepetition(
                            onUpdateRepetition = onUpdateRepetition,
                            isLoading = state.isLoading,
                            onShowingIssue = { showingIssue.value = it },
                            wasOperationComplete = state.wasOperationCompleted
                        )
                    } else {
                        IssueRepetition(
                            isLoading = state.isLoading,
                            onShowingIssue = { showingIssue.value = it },
                            wasOperationComplete = state.wasOperationCompleted,
                            onUpdateRepetition = onUpdateRepetition
                        )
                    }

                } else {
                    FutureRepetition(
                        isLoading = state.isLoading,
                        onDeleteRepetition = onDeleteRepetition,
                        wasOperationComplete = state.wasOperationCompleted
                    )
                }
            } else {
                if (repetition.note != null && repetition.note != "") {

                    Row(
//                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp)
                    ) {
                        Icon(
                            Icons.Filled.Description,
                            contentDescription = "Description",
                            tint = Color.Black,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = repetition.note!!,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }

    LaunchedEffect(Unit) {
        sheetState.show()
    }
}

@Composable
fun PassedRepetition(
    isLoading: Boolean,
    wasOperationComplete: Boolean,
    onUpdateRepetition: (status: String, note: String?) -> Unit,
    onShowingIssue: (Boolean) -> Unit,
) {

    val coroutineScope = rememberCoroutineScope()
    val (isComplete, setIsComplete) = remember {
        mutableStateOf(false)
    }

    var size by remember { mutableStateOf(IntSize.Zero) }

    var note by remember { mutableStateOf("") }

    if (wasOperationComplete) {
        setIsComplete(true)
    }

    OutlinedTextField(
        value = note,
        onValueChange = { note = it },
        label = {
            Text(
                "Aggiungi una nota",
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        enabled = !wasOperationComplete && !isLoading,
    )

    Spacer(modifier = Modifier.height(16.dp))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .border(1.dp, Color(0xFFFFA500), RoundedCornerShape(4.dp))
            .background(Color(0xFFFFA500).copy(alpha = 0.12f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Filled.Warning,
                contentDescription = "Warning",
                tint = Color(0xFFFFA500), // Adjust the color as desired
                modifier = Modifier.padding(end = 8.dp),
            )
            Text(
                text = "Confermando la lezione come effettuata, non sarà più possibile modificarla.",
                fontSize = 14.sp
            )
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .onSizeChanged {
                size = it
            },
        horizontalArrangement = Arrangement.Center
    ) {
        if (wasOperationComplete) {
            Text(
                text = "Lezione confermata con successo!",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp, horizontal = 16.dp),
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        } else
            SwipeButton(
                text = "Scorri per confermare",
                isComplete = isComplete,
                onSwipe = {
                    coroutineScope.launch {
                        delay(500)
                        onUpdateRepetition(RepetitionStatus.DONE.value, note)
                    }
                },
                modifier = Modifier
                    .padding(vertical = 16.dp, horizontal = 16.dp),
                backgroundColor = MaterialTheme.colorScheme.primary
            )
    }

    if (!wasOperationComplete) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            ClickableText(
                text =
                AnnotatedString(
                    "Qualcosa è andato storto?", spanStyle = SpanStyle(fontSize = 16.sp)
                ) + AnnotatedString(" ") +
                        AnnotatedString(
                            "Segnalalo",
                            spanStyle = SpanStyle(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                textDecoration = TextDecoration.Underline,
                                fontSize = 16.sp
                            )
                        ),
                onClick = { onShowingIssue(true) }
            )
        }
    }

}


@Composable
fun IssueRepetition(
    isLoading: Boolean,
    onShowingIssue: (Boolean) -> Unit,
    wasOperationComplete: Boolean,
    onUpdateRepetition: (status: String, note: String?) -> Unit,
) {

    var issue by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {

        Icon(
            Icons.Filled.ChevronLeft,
            contentDescription = "Warning",
            tint = Color.Black, // Adjust the color as desired
            modifier = Modifier
                .clickable(onClick = { onShowingIssue(false) })
                .size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = "Cosa è andato storto?",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier
                .fillMaxWidth()
        )
    }

    val radioOptions = listOf(
        "Il professore era assente",
        "Ho avuto un contrattempo",
        "La lezione è stata annullata",
        "Altro"
    )
    var selectedOption by remember { mutableStateOf(radioOptions[0]) }

    Column {
        radioOptions.forEach { option ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable {
                        if (!wasOperationComplete && !isLoading) selectedOption = option
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (option == selectedOption),
                    onClick = { selectedOption = option },
                    enabled = !wasOperationComplete && !isLoading
                )
                Text(
                    text = option,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }

    OutlinedTextField(
        value = issue,
        onValueChange = { issue = it },
        label = {
            Text(
                "Descrivi il problema",
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        maxLines = 5,
        enabled = selectedOption == "Altro" && !wasOperationComplete && !isLoading,
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .border(1.dp, Color(0xFFFFA500), RoundedCornerShape(4.dp))
            .background(Color(0xFFFFA500).copy(alpha = 0.12f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Filled.Warning,
                contentDescription = "Warning",
                tint = Color(0xFFFFA500), // Adjust the color as desired
                modifier = Modifier.padding(end = 8.dp),
            )
            Text(
                text = "Segnalando un problema, la lezione verrà considerata come non effettuata, e non sarà più possibile modificarla.",
                fontSize = 14.sp
            )
        }
    }

    if (wasOperationComplete) {
        Text(
            text = "Segnalazione inviata con successo!",
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp, horizontal = 16.dp),
            textAlign = TextAlign.Center,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    } else
        LoadingButton(
            onClick = {
                onUpdateRepetition(
                    RepetitionStatus.DELETED.value,
                    if (selectedOption == "Altro") "$selectedOption:\n$issue" else selectedOption
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp),
            loading = isLoading,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFd95868),
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Conferma segnalazione")
            }

        }

}

@Composable
fun FutureRepetition(
    isLoading: Boolean,
    onDeleteRepetition: () -> Unit,
    wasOperationComplete: Boolean
) {

    Spacer(modifier = Modifier.height(16.dp))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 16.dp)
            .border(1.dp, Color(0xFFd95868), RoundedCornerShape(4.dp))
            .background(Color(0xFFd95868).copy(alpha = 0.12f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Filled.Warning,
                contentDescription = "Warning",
                tint = Color(0xFFd95868), // Adjust the color as desired
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(text = "Cancellando la prenotazione non sarà più possibile ripristinarla, e il professore potrebbe non essere disponibile in futuro.")
        }
    }

    if (wasOperationComplete) {
        Text(
            text = "Prenotazione cancellata con successo!",
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp, horizontal = 16.dp),
            textAlign = TextAlign.Center,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    } else LoadingButton(
        onClick = { onDeleteRepetition() },
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp),
        loading = isLoading,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFd95868),
        )

    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Filled.Delete,
                contentDescription = "Date",
                tint = Color.White, // Adjust the color as desired
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(text = "Cancella prenotazione")
        }

    }
}


@Composable
fun Details(
    title: String? = null,
    time: Int,
    date: LocalDate,
    subject: String? = null,
    professor: Professor? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        val dataFormatter = DateTimeFormatter.ofPattern("EEEE, d MMM, yyyy", Locale.ITALIAN)

        if (title != null) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
//                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 16.dp, bottom = 8.dp)
                    .fillMaxWidth()
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Icon(
                Icons.Filled.CalendarToday,
                contentDescription = "Date",
                tint = Color.Black, // Adjust the color as desired
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = dataFormatter.format(date)
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
                fontSize = 16.sp,
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Icon(
                Icons.Filled.AccessTime,
                contentDescription = "Time",
                tint = Color.Black,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = "${time}:00" + " - " + "${time + 1}:00",
                fontSize = 16.sp,
            )
        }

        if (subject != null)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Icon(
                    Icons.Filled.Subject,
                    contentDescription = "Subject",
                    tint = Color.Black, // Adjust the color as desired
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = subject,
                    fontSize = 16.sp,
                )
            }

        if (professor != null)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Icon(
                    Icons.Filled.People,
                    contentDescription = "Date",
                    tint = Color.Black, // Adjust the color as desired
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = "${professor.name} ${professor.surname}",
                    fontSize = 16.sp,
                )
            }
    }

}