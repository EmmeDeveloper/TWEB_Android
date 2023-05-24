package com.tweb.project30.ui.components

//import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tweb.project30.ui.repetitions.ReserveUIState

@OptIn(
    ExperimentalMaterialApi::class, ExperimentalFoundationApi::class,
    ExperimentalLayoutApi::class
)
@Composable
fun RepetitionComponent(
    state: ReserveUIState,
    onBackClicked: () -> Unit,
    onReserve: () -> Unit = {}
) {
    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()

    var selectedSubjectProf = remember {
        mutableStateOf(Pair("", ""))
    }

    if (sheetState.currentValue != ModalBottomSheetValue.Hidden) {
        DisposableEffect(Unit) {
            onDispose {
                onBackClicked()
            }
        }
    }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
//                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Color.White,
                                RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                            )
                            .padding(top = 32.dp, bottom = 16.dp, start = 8.dp, end = 8.dp)
                        ,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        state.professors.forEach() { subject, professor ->
                            stickyHeader {
                                Text(
                                    text = subject,
                                    modifier = Modifier
                                        .fillMaxWidth(),
//                                        .padding(8.dp),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Center,
                                )
                            }
                            item {
                                FlowRow(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp, vertical = 8.dp)
                                ) {
                                    professor.forEach() { professor ->
                                        FilterChip(
                                            modifier = Modifier.padding(horizontal = 4.dp),
                                            selected = selectedSubjectProf.value.first == subject && selectedSubjectProf.value.second == professor.ID,
                                            onClick = {
                                                selectedSubjectProf.value =
                                                    Pair(subject, professor.ID)
                                            },
                                            enabled = state.availableProfessors.containsKey(subject) && state.availableProfessors[subject]!!.contains(
                                                professor
                                            ),
                                        ) {
                                            Text(
                                                text = "${professor.name} ${professor.surname}",
                                                modifier = Modifier.padding(8.dp)
                                            )
                                        }
                                    }
                                }
                            }

                        }

                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                        .height(36.dp)
                        .background(
                            Color.Red,
                            RoundedCornerShape(8.dp)
                        ),
                ) {

                }
            }
        },
        sheetBackgroundColor = Color.Blue,
        sheetElevation = 0.dp,
    ) {

    }

    LaunchedEffect(Unit) {
        sheetState.show()
    }
}