package com.tweb.project30.ui.repetitions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tweb.project30.data.course.Course
import com.tweb.project30.data.professor.Professor
import com.tweb.project30.ui.theme.MontserratFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterScreen(
    viewModel: RepetitionsViewModel,
    onBackPressed: () -> Unit,
) {

    var uiState = viewModel.uiState.collectAsState()
    val edited = remember { mutableStateOf<Boolean>(false) }

    val professors = uiState.value.professors
    val courseMap = remember {
        mutableStateMapOf<String, Course>().apply {
            putAll(uiState.value.courses.associateBy { it.ID })
        }
    }

    val selectedProfessors: SnapshotStateMap<String, SnapshotStateList<Professor>> = remember {
        mutableStateMapOf<String, SnapshotStateList<Professor>>().apply {
            putAll(uiState.value.selectedProfessors.mapValues { mutableStateListOf(*it.value.toTypedArray()) })
        }
    }

    fun selectCourse(course: String) {
        selectedProfessors[course] = mutableStateListOf(*professors[course]!!.toTypedArray())
        edited.value = true
    }

    fun deselectCourse(course: String) {
        selectedProfessors.remove(course)
        edited.value = true
    }

    fun selectProfessor(course: String, professor: Professor) {
        selectedProfessors.getOrPut(course) { mutableStateListOf() }.add(professor)
        edited.value = true
    }

    fun deselectProfessor(course: String, professor: Professor) {
        selectedProfessors[course]?.remove(professor)
        if (selectedProfessors[course]?.isEmpty() == true) {
            deselectCourse(course)
        }
        edited.value = true
    }


    Scaffold(
        topBar = {
            Surface(
                shadowElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                TopAppBar(
                    title = { Text(text = "Filtri") },
                    navigationIcon = {
                        IconButton(onClick = onBackPressed) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                viewModel.updateFilters(
                                    selectedProfessors
                                )
                                edited.value = false
                            },
                            enabled = edited.value
                        ) {
                            Icon(
                                Icons.Filled.Save, contentDescription = "Save",
                                tint =
                                if (edited.value) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                            )
                        }
                    },
                )
            }
        }
    ) { paddingValues ->
        paddingValues.apply {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingValues)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    content = {
                        item {
                            Text(
                                text = "Corsi",
                                fontSize = 20.sp,
                                modifier = Modifier.padding(8.dp),
                                fontWeight = FontWeight.Bold,
                                fontFamily = MontserratFontFamily
                            )
                        }
                        professors.keys.forEach { course ->
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = selectedProfessors.containsKey(course),
                                        onCheckedChange = {
                                            if (it) {
                                                selectCourse(course)
                                            } else {
                                                deselectCourse(course)
                                            }
                                        },
//                                    modifier = Modifier.padding(8.dp)
                                    )
                                    Text(
                                        text = "${courseMap[course]!!.title}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    if (selectedProfessors.containsKey(course)) {
                                        Text(
                                            text = "(${selectedProfessors[course]!!.size}/${professors[course]!!.size})",
                                            fontSize = 16.sp
                                        )
                                    }
                                }

                                professors.get(course)?.let { professors ->
                                    Column {
                                        professors.forEach { professor ->
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(horizontal = 16.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Spacer(modifier = Modifier.width(32.dp))
                                                Checkbox(
                                                    checked = selectedProfessors[course]?.contains(
                                                        professor
                                                    ) == true,
                                                    onCheckedChange = {
                                                        if (it) {
                                                            selectProfessor(course, professor)
                                                        } else {
                                                            deselectProfessor(course, professor)
                                                            if (selectedProfessors[course]?.isEmpty() == true) {
                                                                deselectCourse(course)
                                                            }
                                                        }
                                                    },
//                                                modifier = Modifier.padding(8.dp)
                                                )
                                                Text(
                                                    text = "${professor.name} ${professor.surname}",
                                                    fontSize = 16.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    })
            }
        }
    }
}

