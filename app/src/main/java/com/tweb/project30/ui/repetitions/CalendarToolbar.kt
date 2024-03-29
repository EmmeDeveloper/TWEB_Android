package com.tweb.project30.ui.repetitions

import SegmentText
import SegmentedControl
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tweb.project30.ui.theme.MontserratFontFamily
import kotlinx.coroutines.android.awaitFrame
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale

enum class CalendarMode {
    Day,
    Month
}

@Composable
fun CalendarToolbar(
    daysOfWeek: List<LocalDate>,
    currentDay: LocalDate,
    onDaySelected: (LocalDate) -> Unit,
    onModeSelected: (CalendarMode) -> Unit,
    mode: RepetitionMode = RepetitionMode.GLOBAL_REPETITIONS,
    selectableDays: List<LocalDate> = emptyList(),
) {
    val listState = rememberLazyListState() // Create here to avoid recomposition

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .drawWithContent {
                drawContent()
                drawLine(
                    color = Color.Gray,
                    strokeWidth = 1f,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height)
                )
            }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            val listItems = remember { listOf("Giorno", "Mese") }
            var selectedItem by remember { mutableStateOf(listItems.first()) }

            SegmentedControl(
                segments = listItems,
                selectedSegment = selectedItem,
                onSegmentSelected = {
                    selectedItem = it
                    onModeSelected(if (selectedItem == listItems.first()) CalendarMode.Day else CalendarMode.Month)
                },
                modifier = Modifier
                    .padding(bottom = 12.dp, start = 16.dp, end = 16.dp)
                    .fillMaxWidth()
            ) {
                SegmentText(it)
            }

            if (selectedItem == "Giorno") {
                if (mode == RepetitionMode.GLOBAL_REPETITIONS)
                    CalendarWeek(
                        daysOfWeek = daysOfWeek,
                        currentDay = currentDay,
                        onDaySelected = onDaySelected,
                        pastDaySelectable = true
                    )
            } else
                CalendarMonth(
                    currentDay = currentDay,
                    onDaySelected = onDaySelected,
                    listState = listState,
                    selectableDays = if (mode == RepetitionMode.MY_REPETITIONS) selectableDays else null
                )
        }
    }
}

@Composable
fun CalendarWeek(
    daysOfWeek: List<LocalDate>,
    currentDay: LocalDate,
    onDaySelected: (LocalDate) -> Unit,
    pastDaySelectable: Boolean = false,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        daysOfWeek.forEach {

            val isEnabled = it.dayOfWeek !in listOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY) &&
                    (pastDaySelectable || it.isAfter(LocalDate.now().minus(1, ChronoUnit.DAYS)))

            Surface(
                Modifier
                    .clickable(enabled = isEnabled) { onDaySelected(it) }
                    .weight(1f)
                    .padding(4.dp),
            ) {
                Column(
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
                        color = if (isEnabled) Color.Black else Color.Gray.copy(alpha = 0.5f)
                    )

                    val isToday = it == LocalDate.now()
                    val isSelected = it == currentDay

                    val backgroundColor = when {
                        isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                        else -> Color.Transparent
                    }

                    Box(
                        modifier = Modifier
                            .background(backgroundColor, CircleShape)
                            .aspectRatio(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = it.dayOfMonth.toString(),
                            modifier = Modifier
                                .wrapContentSize(Alignment.Center),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            color =
                            if (isToday) MaterialTheme.colorScheme.primary
                            else if (isEnabled) Color.Black
                            else Color.Gray.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }

    }
}

@Composable
fun CalendarMonth(
    currentDay: LocalDate,
    onDaySelected: (LocalDate) -> Unit,
    pastDaySelectable: Boolean = false,
    listState: LazyListState,
    selectableDays: List<LocalDate>? = null
) {
    val days = remember { getDaysOfYear(currentDay.year) }
    val dayOfWeek = remember { listOf("L", "M", "M", "G", "V", "S", "D") }

    val months = remember {
        days
            .sortedBy { it.month }
            .groupBy { it.month }
    }

    val internalCurrentDay = remember { mutableStateOf(currentDay) }

    // Days of the week
    Column {
        // Days of the week header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            repeat(7) { index ->
                Text(
                    text = dayOfWeek[index],
//                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                        .padding(8.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    LazyColumn(
        state = listState,
    ) {

        months.forEach { (month, dates) ->
            val monthName = month.getDisplayName(
                TextStyle.FULL,
                Locale("it", "IT")
            ).uppercase()


            val weeks = ((1 until dates.first().dayOfWeek.value).map { null } + dates).chunked(7)

            item {
                Text(
                    text = monthName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .padding(top = 16.dp, start = 16.dp, end = 16.dp),
                    color =
                    // Se il mese contiene un giorno nella lista dei selezionabili
                    if (selectableDays != null) {
                        if (selectableDays.any { it.month == month }) Color.Black
                        else Color.Gray.copy(alpha = 0.5f)
                    } else {
                        if (pastDaySelectable || month.value >= LocalDate.now().month.value) Color.Black
                        else Color.Gray.copy(alpha = 0.5f)
                    }
                )
            }


            weeks.forEach() { week ->
                item {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        repeat(7) { index ->
                            val day = week.getOrNull(index)

                            if (day != null) {
                                val isSelected = day == currentDay
                                val isToday = day == LocalDate.now()
                                val daySelectable =
                                    if (selectableDays != null)
                                        day in selectableDays
                                    else
                                        day.dayOfWeek !in listOf(
                                            DayOfWeek.SATURDAY,
                                            DayOfWeek.SUNDAY
                                        ) && (pastDaySelectable || day >= LocalDate.now())

                                val textColor =
                                    if (isToday) MaterialTheme.colorScheme.primary
                                    else if (daySelectable) Color.Black
                                    else Color.Gray.copy(alpha = 0.5f)

                                val backgroundColor = when {
                                    isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                    else -> Color.Transparent
                                }

                                Box(
                                    modifier = Modifier
                                        .background(backgroundColor, CircleShape)
                                        .weight(1f)
                                        .align(Alignment.CenterVertically)
                                        .padding(8.dp)
                                        .aspectRatio(1f)
                                        .clickable(enabled = daySelectable) { onDaySelected(day) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = day.dayOfMonth.toString(),
                                        modifier = Modifier
                                            .wrapContentSize(Alignment.Center),
                                        textAlign = TextAlign.Center,
                                        fontWeight = FontWeight.Bold,
                                        color = textColor
                                    )
                                }

                            } else {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Jump to current day
    LaunchedEffect(internalCurrentDay.value) {
        val cellBeforeDate = (1 until currentDay.month.value)
            .map {
                val firstDayOfMonth = LocalDate.of(currentDay.year, it, 1)
                val lastDayOfMonth = firstDayOfMonth.plusMonths(1).minusDays(1)
                firstDayOfMonth.dayOfWeek.value - 1 + (7 - lastDayOfMonth.dayOfWeek.value) // Initial offset + last week offset
            }
            .sum() + currentDay.dayOfYear + LocalDate.of(
            currentDay.year,
            currentDay.month,
            1
        ).dayOfWeek.value - 1
        awaitFrame()
        listState.animateScrollToItem(currentDay.month.value + cellBeforeDate / 7 - (if (cellBeforeDate % 7 == 0) 1 else 0))
    }
}

fun getDaysOfYear(year: Int): List<LocalDate> {
    val firstDay = LocalDate.of(year, 1, 1)
    val lastDay = LocalDate.of(year, 12, 31)
    val daysBetween = ChronoUnit.DAYS.between(firstDay, lastDay)
    return (0..daysBetween).map { firstDay.plusDays(it) }
}