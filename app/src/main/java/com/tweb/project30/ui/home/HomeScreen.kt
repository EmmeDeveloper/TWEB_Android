package com.tweb.project30.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tweb.project30.data.course.Course
import com.tweb.project30.data.professor.Professor
import com.tweb.project30.data.repetition.Repetition
import com.tweb.project30.data.repetition.RepetitionStatus
import com.tweb.project30.ui.components.RepetitionComponentHome
import com.tweb.project30.ui.theme.MontserratFontFamily
import java.time.LocalDate
import java.util.*

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onUserActionClicked: () -> Unit,
    onRepetitionClicked: (repetitionID: String?) -> Unit
) {

    val uiState = viewModel.uiState.collectAsState()

    HomeScreen(uiState, onUserActionClicked, onRepetitionClicked)
}

@Composable
private fun HomeScreen(
    homeState: State<HomeUIState>,
    onUserActionClicked: () -> Unit,
    onRepetitionClicked: (repetitionID: String?) -> Unit
) {

    if (homeState.value.isLogged || true) {
        HomeLoggedScreen(
            homeState = homeState.value,
            onUserActionClicked = onUserActionClicked,
            onRepetitionClicked = onRepetitionClicked
        )
    } else {
        HomeNotLoggedScreen(
            homeState = homeState.value,
            onUserActionClicked = onUserActionClicked
        )
    }
}

@Composable
private fun HomeLoggedScreen(
    homeState: HomeUIState,
    onUserActionClicked: () -> Unit,
    onRepetitionClicked: (repetitionID: String?) -> Unit
) {

    val prev: Repetition = Repetition(
        ID = "1",
        IDUser = "1",
        IDCourse = "1",
        course = Course(
            ID = "1",
            title = "Analisi 1",
        ),
        date = LocalDate.now(),
        time = 10,
        professor = Professor(
            ID = "1",
            name = "Mario",
            surname = "Rossi",
        ),
        note = "Note bellissime",
        status = RepetitionStatus.DONE.value
    )

    val next: Repetition = Repetition(
        ID = "1",
        IDUser = "1",
        IDCourse = "1",
        course = Course(
            ID = "1",
            title = "Programmazione 3",
        ),
        date = LocalDate.now().plusDays(1),
        time = 14,
        professor = Professor(
            ID = "1",
            name = "Molica",
            surname = "Marco",
        ),
        status = RepetitionStatus.PENDING.value
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

        // Logo

        val boldStyle = SpanStyle(
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = MontserratFontFamily
        )

        ClickableText(
            text =
            AnnotatedString(
                "Ciao, ",
                spanStyle = boldStyle
            ) +
                    AnnotatedString(
                        "${
                            homeState.currentUser!!.account.replaceFirstChar {
                                if (it.isLowerCase()) it.titlecase(
                                    Locale.getDefault()
                                ) else it.toString()
                            }
                        }",
                        spanStyle = boldStyle.copy(
                            color = MaterialTheme.colorScheme.primary,
                        )
                    ) +
                    AnnotatedString(
                        " \uD83D\uDC4B",
                        spanStyle = boldStyle
                    ),
            onClick = { onUserActionClicked() }
        )
        Text(
            text = "Bentornato in Project30.",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .padding(top = 4.dp, bottom = 16.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier
                    .weight(1f),
                text =
                AnnotatedString(
                    "Le tue "
                ) +
                        AnnotatedString(
                            "lezioni",
                            spanStyle = SpanStyle(
                                fontWeight = FontWeight.Bold
                            )
                        ),
                fontSize = 22.sp,
                style = MaterialTheme.typography.bodyLarge,

                )
            AddButton(
                onClick = { onRepetitionClicked(null) }
            )


        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Lezione precedente",
            fontSize = 22.sp,
            fontFamily = MontserratFontFamily
        )

        // Previous
        if (homeState.previousRepetition != null || true) {
            RepetitionComponentHome(
                repetition = homeState.previousRepetition ?: prev,
                onRepetitionClicked = { onRepetitionClicked(it) })
        } else {
            // No previous repetition
            // Card
            // onRepetitionClicked
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Prossima lezione",
            fontSize = 22.sp,
            fontFamily = MontserratFontFamily
        )

        // Next
        if (homeState.nextRepetition != null || true) {
            RepetitionComponentHome(
                repetition = homeState.previousRepetition ?: next,
                onRepetitionClicked = { onRepetitionClicked(it) })
            // onRepetitionClicked
        } else {
            // No next repetition
            // Card
            // onRepetitionClicked
        }


    }

}

@Composable
private fun HomeNotLoggedScreen(
    homeState: HomeUIState,
    onUserActionClicked: () -> Unit
) {
    // Hi! Please login to continue.
    // Schermata
    // Login button

}

@Composable
fun AddButton(
    modifier: Modifier = Modifier,
    onClick: (isEnabled: Boolean) -> Unit = {},
    enable: Boolean = true,
) {
    Button(
        onClick = { onClick(enable) },
        modifier = modifier
            .width(124.dp)
            .shadow(0.dp),
        shape = RoundedCornerShape(28.dp),
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
            contentColor = MaterialTheme.colorScheme.primary
        ),
//        colors = ButtonDefaults.buttonColors(
//            containerColor = backgroundColor,
//            contentColor = fontColor
//        ),
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Aggiungi",
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Add",
                    modifier = Modifier
                        .size(16.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(16.dp)
                        ),
                    tint = Color.White
                )

            }
        }
    }
}

