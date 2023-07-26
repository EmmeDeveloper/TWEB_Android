package com.tweb.project30.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tweb.project30.R
import com.tweb.project30.data.user.UserRepository
import com.tweb.project30.ui.components.LoadingButton
import com.tweb.project30.ui.theme.MontserratFontFamily

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onLogout: () -> Unit,
) {

    val uiState = viewModel.uiState.collectAsState()

    if (!UserRepository.isLogged.value!!) {
        return
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            var id = UserRepository.currentUser.id;
            var drawableResId =
                when (id.toInt() % 3) {
                    0 -> R.drawable.fake1
                    1 -> R.drawable.fake2
                    else -> R.drawable.fake3
                }


            // Image profile
            Image(
                painter = painterResource(id = drawableResId),
                contentDescription = null,
                modifier = Modifier
                    .padding(top = 24.dp)
                    .size(128.dp)
                    .aspectRatio(1f)
                    .clip(CircleShape)
                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
                contentScale = ContentScale.Crop,
            )

            Text(
                text = UserRepository.currentUser.name + " " + UserRepository.currentUser.surname,
                modifier = Modifier.padding(top = 16.dp),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                fontFamily = MontserratFontFamily,
            )

            Text(
                text = UserRepository.currentUser.role,
                modifier = Modifier.padding(top = 8.dp),
                fontSize = 16.sp,
                fontFamily = MontserratFontFamily,
            )

            Spacer(modifier = Modifier.height(32.dp))

            var map = mapOf(
                "Email" to UserRepository.currentUser.email,
                "Account" to UserRepository.currentUser.account,
                "Indirizzo" to UserRepository.currentUser.address,
                "Data di nascita" to UserRepository.currentUser.birthDate,
                "Telefono" to UserRepository.currentUser.phone,
                "Membro dal" to UserRepository.currentUser.memberSince,
            )

            map.forEach() {
                Item(it.key, it.value ?: "")
                // Add divider if not last item
                if (it.key != map.keys.last()) {
                    Divider()
                }
            }

            val coroutineScope = rememberCoroutineScope()

            LoadingButton(
                onClick = { viewModel.logout(onLogout) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 16.dp),
                loading = uiState.value.loading
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Disconnetti")
                }

            }
        }
    }
}

@Composable
private fun Item(
    title: String,
    value: String,
) {

    Row(
        modifier = Modifier
            .height(36.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,

        ) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontFamily = MontserratFontFamily,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = value,
            fontSize = 16.sp,
            fontFamily = MontserratFontFamily,
        )
    }

}


