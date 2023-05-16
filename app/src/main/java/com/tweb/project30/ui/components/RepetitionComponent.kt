package com.tweb.project30.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tweb.project30.data.repetition.Repetition
import com.tweb.project30.ui.theme.MontserratFontFamily

class RepetitionComponent {
}

@Composable
fun RepetitionComponentHome(
    repetition: Repetition,
    onRepetitionClicked: (id : String?) -> Unit,
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
                        .fillMaxWidth()
                    ,
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