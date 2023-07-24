package com.tweb.project30.ui.repetitions

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tweb.project30.R
import com.tweb.project30.data.user.UserRepository
import com.tweb.project30.util.supportWideScreen


@Composable
fun MyRepetitionsScreen(
    viewModel: RepetitionsViewModel,
    onFilterButtonClicked: () -> Unit,
    onLoginClicked: () -> Unit,
) {
    if (UserRepository.isLogged.value != true) {
        NonLoggedRepetitionsScreen(onLoginClicked)

    } else {
        RepetitionsScreen(viewModel, onFilterButtonClicked, onLoginClicked)
    }
}

@Composable
private fun NonLoggedRepetitionsScreen(
    onLoginClicked: () -> Unit,
) {

    Surface(
        modifier = Modifier
            .supportWideScreen()
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val image: Painter = painterResource(R.drawable.isometric_login)
            Image(
                painter = image,
                contentDescription = "App Logo",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp)
            )

            Text(
                text = "Le tue ripetizioni",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(top = 16.dp, bottom = 8.dp)
                    .fillMaxWidth()
            )

            Text(
                text = "Qui troverai le tue ripetizioni, per poterle gestire in modo semplice e veloce. Effettua l'accesso per visualizzarle",
                fontSize = 16.sp,
                modifier = Modifier
                    .padding(top = 8.dp, bottom = 8.dp)
                    .fillMaxWidth()
            )

            Button(
                onClick = { onLoginClicked() },
                modifier = Modifier
                    .padding(top = 8.dp, bottom = 8.dp)
                    .fillMaxWidth()
            ) {
                Text(text = "Accedi")
            }
        }
    }


}