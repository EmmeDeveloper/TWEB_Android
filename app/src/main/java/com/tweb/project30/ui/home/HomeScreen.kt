package com.tweb.project30.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tweb.project30.util.supportWideScreen

@Composable
fun HomeScreen() {

    Surface(
        modifier = Modifier.supportWideScreen()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Home",
                style = MaterialTheme.typography.titleMedium
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Home screen")
            }

            Row(modifier = Modifier.fillMaxWidth()) {
                Text(text = "Home screen sotto")
            }
        }
    }
}

