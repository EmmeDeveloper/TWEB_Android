package com.tweb.project30.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tweb.project30.data.course.Course
import com.tweb.project30.data.professor.Professor
import com.tweb.project30.data.repetition.Repetition
import com.tweb.project30.data.repetition.RepetitionStatus
import com.tweb.project30.ui.theme.MontserratFontFamily
import java.time.LocalDate

class RepetitionComponent {
}

@Composable
fun RepetitionComponentHome(
    repetition: Repetition,
    onRepetitionClicked: (id: String?) -> Unit,
    isLoading: Boolean = false
) {

    Spacer(modifier = Modifier.padding(top = 8.dp))

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .height(120.dp)
            .clickable { onRepetitionClicked(repetition.ID) },
    ) {

        if (isLoading) {

        } else {

            Box() {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                )
                {

                    // Left side
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = repetition.course?.title ?: "Corso eliminato",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            fontFamily = MontserratFontFamily
                        )
                        Spacer(modifier = Modifier.padding(2.dp))
                        Text(
                            text = "${repetition.date}",
                            fontSize = 16.sp,
                            fontFamily = MontserratFontFamily
                        )
                        Spacer(modifier = Modifier.padding(2.dp))
                        Text(
                            text = "${repetition.time}:00 - ${repetition.time + 1}:00",
                            fontSize = 16.sp,
                            fontFamily = MontserratFontFamily
                        )
                        Spacer(modifier = Modifier.padding(2.dp))
                        Text(
                            text = repetition.professor?.run { "$name $surname" }
                                ?: "Professore eliminato",
                            fontSize = 16.sp,
                            fontFamily = MontserratFontFamily
                        )
                    }

                    // Right side
                    Column {

                        Spacer(Modifier.weight(1f))

                        if (repetition.note != null) {
                            Icon(
                                Icons.Filled.Description,
                                contentDescription = "Note",
                                modifier = Modifier
                                    .size(24.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RepetitionComponent(
    repetition: Repetition,
    onRepetitionClicked: (id: String?) -> Unit,
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .height(104.dp)
            .clickable { onRepetitionClicked(repetition.ID) },
    ) {

        Box() {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                // Upper side
                Row(
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        text = repetition.course?.title ?: "Corso eliminato",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        fontFamily = MontserratFontFamily
                    )
                    Spacer(Modifier.weight(1f))
                    Box(
                        modifier = Modifier
                            .background(
                                if (repetition.date.isBefore(LocalDate.now()))
                                    Color(0xFF808080)
                                else
                                    when (repetition.status) {
                                        RepetitionStatus.PENDING.toString() -> Color(0xFFFFCC99)
                                        RepetitionStatus.DONE.toString() -> Color(0xFF99FF99)
                                        RepetitionStatus.DELETED.toString() -> Color(0xFFFF9999)
                                        else -> Color(0xFF9999FF)
                                    },
                                shape = RoundedCornerShape(8.dp)
                            )
                    ) {
                        Text(
                            modifier = Modifier.padding(4.dp),
                            text =
                            if (repetition.isBeforeNow())
                                "Passata"
                            else
                                when (repetition.status) {
                                    RepetitionStatus.PENDING.toString() -> "Programmata"
                                    RepetitionStatus.DONE.toString() -> "Completata"
                                    RepetitionStatus.PENDING.toString() -> "Eliminata"
                                    else -> "In attesa"
                                },
                            fontSize = 14.sp,
                            fontFamily = MontserratFontFamily,
                        )
                    }

                }

                // Right side
                Row {
                    Text(
                        text = repetition.professor?.run { "$name $surname" }
                            ?: "Professore eliminato",
                        fontSize = 18.sp,
                        fontFamily = MontserratFontFamily
                    )

                    Spacer(Modifier.weight(1f))

                    if (repetition.note != null) {
                        Icon(
                            Icons.Filled.Description,
                            contentDescription = "Note",
                            modifier = Modifier
                                .size(24.dp)
                        )
                    }
                }
            }
        }

    }
}

@Composable
fun AvailableRepetitionComponent(
    courses: List<Course>,
    professors: Map<String, List<Professor>>,
    availableProfessors: Map<String, List<Professor>>,
    onRepetitionClicked: (id: String?) -> Unit,
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .height(104.dp)
            .clickable { onRepetitionClicked(null) },
    ) {
        Box() {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                // Left side
                Row(
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        text = availableProfessors.keys
                            .map { courseId -> courses.find { it.ID == courseId }?.title ?: "" }
                            .joinToString(", "),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        fontFamily = MontserratFontFamily
                    )

                    Spacer(Modifier.weight(1f))

                    val freeRatio =
                        availableProfessors.values.size.toDouble() / professors.values.size.toDouble()

                    Box(
                        modifier = Modifier
                            .background(
                                when {
                                    freeRatio >= 0.4 -> Color(0xFF99FF99)
                                    freeRatio == 0.0 -> Color(0xFFFF9999)
                                    else -> Color(0xFFFFFF99)
                                },
                                shape = RoundedCornerShape(8.dp)
                            )
                    ) {
                        Text(
                            modifier = Modifier.padding(4.dp),
                            text =
                            when {
                                freeRatio >= 0.4 -> "Disponibile"
                                freeRatio == 0.0 -> "Esaurito"
                                else -> "Quasi esaurito"
                            },
                            fontSize = 14.sp,
                            fontFamily = MontserratFontFamily,
                        )
                    }

                }

                Row() {
                    Text(
//                        text = availableProfessors.values
//                            .map { it.joinToString { "${it.name} ${it.surname}" } }
//                            .joinToString(", "),
                        text ="BOOOO",
                        fontSize = 18.sp,
                        fontFamily = MontserratFontFamily
                    )

                    Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}