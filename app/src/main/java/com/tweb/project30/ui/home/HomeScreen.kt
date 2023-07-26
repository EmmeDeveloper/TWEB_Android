package com.tweb.project30.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tweb.project30.R
import com.tweb.project30.ui.components.RepetitionCardComponentHome
import com.tweb.project30.ui.theme.MontserratFontFamily
import com.tweb.project30.util.supportWideScreen
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onUserActionClicked: () -> Unit,
    onRepetitionClicked: (repetitionID: String?) -> Unit
) {

    val uiState = viewModel.uiState.collectAsState()

    HomeScreen(uiState, onUserActionClicked, onRepetitionClicked)
}

@Composable
private fun HomeScreen(
    homeState: State<HomeUIState>,
    onUserActionClicked: () -> Unit,
    onRepetitionClicked: (repetitionID: String?) -> Unit
) {

    if (homeState.value.isLogged) {
        HomeLoggedScreen(
            homeState = homeState.value,
            onUserActionClicked = onUserActionClicked,
            onRepetitionClicked = onRepetitionClicked
        )
    } else {
        HomeNotLoggedScreen(
            homeState = homeState.value,
            onUserActionClicked = onUserActionClicked
        )
    }
}

@Composable
private fun HomeLoggedScreen(
    homeState: HomeUIState,
    onUserActionClicked: () -> Unit,
    onRepetitionClicked: (repetitionID: String?) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

        // Logo

        val boldStyle = SpanStyle(
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = MontserratFontFamily
        )

        ClickableText(
            text =
            AnnotatedString(
                "Ciao, ",
                spanStyle = boldStyle
            ) +
                    AnnotatedString(
                        "${
                            homeState.currentUser!!.account.replaceFirstChar {
                                if (it.isLowerCase()) it.titlecase(
                                    Locale.getDefault()
                                ) else it.toString()
                            }
                        }",
                        spanStyle = boldStyle.copy(
                            color = MaterialTheme.colorScheme.primary,
                        )
                    ) +
                    AnnotatedString(
                        " \uD83D\uDC4B",
                        spanStyle = boldStyle
                    ),
            onClick = { onUserActionClicked() }
        )
        Text(
            text = "Bentornato in Project30.",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .padding(top = 4.dp, bottom = 16.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier
                    .weight(1f),
                text =
                AnnotatedString(
                    "Le tue "
                ) +
                        AnnotatedString(
                            "lezioni",
                            spanStyle = SpanStyle(
                                fontWeight = FontWeight.Bold
                            )
                        ),
                fontSize = 22.sp,
                style = MaterialTheme.typography.bodyLarge,

                )
            AddButton(
                onClick = { onRepetitionClicked(null) }
            )


        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Prossima lezione",
            fontSize = 22.sp,
            fontFamily = MontserratFontFamily
        )

        // Previous
        if (homeState.nextRepetition != null) {

            RepetitionCardComponentHome(
                repetition = homeState.nextRepetition!!,
                onRepetitionClicked = { onRepetitionClicked(it) })
        } else {
            Text(text = "Nessuna lezione ancora da svolgere, prenotane una adesso!", modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Lezione precedente",
            fontSize = 22.sp,
            fontFamily = MontserratFontFamily,

            )


        // Next
        if (homeState.previousRepetition != null) {
            Box(modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp)) {
                RepetitionCardComponentHome(
                    repetition = homeState.previousRepetition!!,
                    onRepetitionClicked = { onRepetitionClicked(it) })
            }
        } else {
            Text(text = "Nessuna lezione già svolta", modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),fontSize = 16.sp)
        }

    }

}

@Composable
private fun HomeNotLoggedScreen(
    homeState: HomeUIState,
    onUserActionClicked: () -> Unit
) {
    Surface(
        modifier = Modifier
            .supportWideScreen()
            .padding(vertical = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Project30",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = Modifier
                    .padding(top = 44.dp, bottom = 8.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary
            )

            val image: Painter = painterResource(R.drawable.home_image)
            Image(
                painter = image,
                contentDescription = "App Logo",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                contentScale = ContentScale.Crop
            )

            Text(
                text = "Trova l'insegnante ideale",
                fontWeight = FontWeight.Bold,
                fontSize = 29.sp,
                modifier = Modifier
                    .padding(top = 16.dp, bottom = 8.dp, start = 24.dp, end = 24.dp)
                    .fillMaxWidth()
            )

            Text(
                text = "Online o in presenza, trova l'insegnante che fa per te e inizia a studiare subito!",
                fontSize = 22.sp,
                modifier = Modifier
                    .padding(top = 8.dp, bottom = 8.dp, start = 24.dp, end = 24.dp)
                    .fillMaxWidth()
            )
        }
    }

}

@Composable
fun AddButton(
    modifier: Modifier = Modifier,
    onClick: (isEnabled: Boolean) -> Unit = {},
    enable: Boolean = true,
) {
    Button(
        onClick = { onClick(enable) },
        modifier = modifier
            .width(124.dp)
            .shadow(0.dp),
        shape = RoundedCornerShape(28.dp),
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
            contentColor = MaterialTheme.colorScheme.primary
        ),
//        colors = ButtonDefaults.buttonColors(
//            containerColor = backgroundColor,
//            contentColor = fontColor
//        ),
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Aggiungi",
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Add",
                    modifier = Modifier
                        .size(16.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(16.dp)
                        ),
                    tint = Color.White
                )

            }
        }
    }
}

