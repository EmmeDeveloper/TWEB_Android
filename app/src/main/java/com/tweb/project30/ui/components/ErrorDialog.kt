package com.tweb.project30.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun ErrorDialog(
    title: String,
    message: String,
    confirmButton: String = "Ok",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {

    MaterialTheme {

        Column {
            AlertDialog(
                title = { Text(text = title) },
                text = { Text(text = message) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onConfirm()
                        }
                    ) {
                        Text(text = confirmButton)
                    }
                },
                onDismissRequest = {
                    onDismiss()
                }
            )
        }

    }

}