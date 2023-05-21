package com.tweb.project30.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tweb.project30.data.course.Course
import com.tweb.project30.data.professor.Professor
import com.tweb.project30.data.repetition.Repetition
import com.tweb.project30.data.repetition.RepetitionStatus
import com.tweb.project30.ui.theme.MontserratFontFamily
import java.time.LocalDateTime

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
            .height(80.dp)
            .clickable { onRepetitionClicked(repetition.ID) }
            .border(width = 1.dp, color = Color(0xFF999999), shape = RoundedCornerShape(8.dp))
            .background(Color.White),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
            contentColor = Color.Black
        )
    ) {

        Box() {
            Column(
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 16.dp)
                    .fillMaxWidth()
            ) {
                // Upper side
                Row(
//                    modifier = Modifier.padding(top = 16.dp),
                ) {
                    Text(
                        text = repetition.course?.title ?: "Corso eliminato",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        fontFamily = MontserratFontFamily
                    )
                    Spacer(Modifier.weight(1f))
                    Box(
                        modifier = Modifier
                            .background(
                                if (repetition.isBeforeNow())
                                    Color(0x77808080)
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
                                    RepetitionStatus.DELETED.toString() -> "Eliminata"
                                    else -> "In attesa"
                                },
                            fontSize = 13.sp,
                            fontFamily = MontserratFontFamily,
                        )
                    }

                }

                Spacer(modifier = Modifier.weight(1f))

                // Right side
                Row {
                    Text(
                        text = repetition.professor?.run { "$name $surname" }
                            ?: "Professore eliminato",
                        fontSize = 14.sp,
                        fontFamily = MontserratFontFamily
                    )

                    Spacer(Modifier.weight(1f))

                    if (repetition.note != null) {
                        Icon(
                            Icons.Filled.Description,
                            contentDescription = "Note",
                            modifier = Modifier
                                .size(18.dp)
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
    date: LocalDateTime
) {
    val isEnabled = remember { mutableStateOf(availableProfessors.isNotEmpty() && !date.isBefore(LocalDateTime.now())) }

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .height(80.dp)
//            .clickable(enabled = isEnabled.value) { onRepetitionClicked(null) }
            .border(width = 1.dp, color = Color(0xFF999999), shape = RoundedCornerShape(8.dp))
            .background(Color.White),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
            contentColor = Color.Black
        )
    ) {
        Box() {
            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.padding(end = 8.dp),
                ) {
                    // Right side
//                    val freeRatio =
//                        availableProfessors.values.size.toDouble() / professors.values.size.toDouble()

                    Box(
                        modifier = Modifier
                            .background(
//                                when {
//                                    freeRatio >= 0.4 -> Color(0xFF99FF99)
//                                    freeRatio == 0.0 -> Color(0xFFFF9999)
//                                    else -> Color(0xFFFFFF99)
//                                },
                                if (isEnabled.value)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
//                                            Color (0xFF808080),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .fillMaxHeight()
                            .width(4.dp)
                    )
                    Spacer(Modifier.weight(1f))
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                ) {
                    Text(
                        text = availableProfessors.keys
                            .map { courseId -> courses.find { it.ID == courseId }?.title ?: "" }
                            .joinToString(", "),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        fontFamily = MontserratFontFamily,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = if (isEnabled.value) Color.Black else Color.Gray.copy(alpha = 0.5f)
                    )
                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = availableProfessors.values
                            .map { it.joinToString { "${it.name} ${it.surname}" } }
                            .joinToString(", "),
                        fontSize = 14.sp,
                        fontFamily = MontserratFontFamily,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        style = androidx.compose.ui.text.TextStyle(
                            lineHeight = 16.sp,
                        ),
                        color = if (isEnabled.value) Color.Black else Color.Gray.copy(alpha = 0.5f)
                    )
                }

                Column(
                ) {
                    Button(
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(8.dp),
                        modifier = Modifier
                            .defaultMinSize(minWidth = 1.dp, minHeight = 32.dp),
                        onClick = { onRepetitionClicked(null) },
                        enabled = isEnabled.value
                    )
                    {
                        Text(
                            text = "Prenota",
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}