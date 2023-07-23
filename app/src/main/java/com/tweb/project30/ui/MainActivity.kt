package com.tweb.project30.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.tweb.project30.ui.theme.Project30Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // This app draws behind the system bars, so we want to handle fitting system windows
//        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            Project30Theme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    Project30App()
                }
            }
        }
    }
}

// TODOS
// Logout, profilo
// Pagina home normale
// Ultimo filtro non rimuovibile


