package com.tweb.project30.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tweb.project30.R
import com.tweb.project30.ui.components.ErrorDialog
import com.tweb.project30.ui.components.LoadingButton
import com.tweb.project30.util.supportWideScreen


@Composable
fun LoginScrenn(
    viewModel: LoginViewModel,
    onLoginCompleted: () -> Unit,
) {
    val uiState = viewModel.uiState.collectAsState()
    LoginScreen(
        onErrorDialogClosed = viewModel::resetRequestState,
        onLoginSubmitted = { account, password ->
            viewModel.login(account, password, onLoginCompleted)
        },
        onPasswordChange = viewModel::onPasswordChange,
        onAccountChange = viewModel::onAccountChange,
        loginState = uiState,
    )
}

@Composable
private fun LoginScreen(
    onErrorDialogClosed: () -> Unit = {},
    onLoginSubmitted: (account: String, password: String) -> Unit,
    onPasswordChange: (newValue: String) -> Unit,
    onAccountChange: (newValue: String) -> Unit,
    loginState: State<LoginUIState>,
) {
    Surface(
        modifier = Modifier.supportWideScreen()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                AppLogoAndText()
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                // Login form
                Login(
                    onLoginSubmitted = onLoginSubmitted,
                    onAccountChange = onAccountChange,
                    onPasswordChange = onPasswordChange,
                    loginState = loginState
                )
            }

            if (loginState.value.requestState == LoginRequestState.INTERNET_ERROR) {
                ErrorDialog(
                    title = stringResource(id = R.string.connection_error_title),
                    message = stringResource(id = R.string.connection_error_message),
                    confirmButton = stringResource(id = R.string.retry_label),
                    onConfirm = {
                        onErrorDialogClosed()
                        onLoginSubmitted(loginState.value.account, loginState.value.password)
                    },
                    onDismiss = onErrorDialogClosed
                )
            }

        }
    }
}

@Composable
private fun AppLogoAndText(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.wrapContentHeight(align = Alignment.CenterVertically)
    ) {
        Logo(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = 76.dp, vertical = 24.dp)
        )
        Text(
            text = stringResource(id = R.string.app_name),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth()
        )
    }
}

@Composable
private fun Logo(
    modifier: Modifier = Modifier,
    lightTheme: Boolean = LocalContentColor.current.luminance() < 0.5f,
) {
    val assetId = if (lightTheme) {
        R.drawable.ic_logo_light
    } else {
        R.drawable.ic_logo_dark
    }
    Image(
        painter = painterResource(id = assetId),
        modifier = modifier,
        contentDescription = null
    )
}

@Composable
private fun Login(
    modifier: Modifier = Modifier,
    onLoginSubmitted: (email: String, password: String) -> Unit,
    onPasswordChange: (newValue: String) -> Unit,
    onAccountChange: (newValue: String) -> Unit,
    loginState: State<LoginUIState>,
) {
    Column(
        modifier =
        modifier
            .clip(RoundedCornerShape(topEnd = 16.dp, topStart = 16.dp))
            .background(Color.White)
    ) {

        val showDialogNotImplemented = remember { mutableStateOf(false) }

        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(id = R.string.login),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(16.dp))

            val focusRequester = remember { FocusRequester() }
            Email(
                showError = loginState.value.requestState == LoginRequestState.WRONG_PSW_OR_EMAIL,
                email = loginState.value.account,
                onImeAction = { focusRequester.requestFocus() },
                onAccountChange = onAccountChange
            )

            Spacer(modifier = Modifier.height(16.dp))
            val onSubmit = {
                if (!loginState.value.loading) {
                    onLoginSubmitted(loginState.value.account, loginState.value.password)
                }
            }
            Password(
                password = loginState.value.password,
                showError = loginState.value.requestState == LoginRequestState.WRONG_PSW_OR_EMAIL,
                label = stringResource(id = R.string.password),
                modifier = Modifier.focusRequester(focusRequester),
                onImeAction = { onSubmit() },
                onPasswordChange = onPasswordChange
            )

            RememberMeComponent()

            LoadingButton(
                onClick = { onSubmit() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                enabled = loginState.value.requestState == LoginRequestState.NONE,
                loading = loginState.value.loading,
            ) {
                Text(text = stringResource(id = R.string.login_button))
            }

            ForgotPasswordComponent {
                showDialogNotImplemented.value = true
            }

            LoginSignupDivisorComponent()

            SignupComponent {
                showDialogNotImplemented.value = true
            }

            if (showDialogNotImplemented.value) {
                ErrorDialog(
                    title = stringResource(id = R.string.not_implemented_title),
                    message = stringResource(id = R.string.not_implemented_body),
                    confirmButton = "Ok",
                    onConfirm = { showDialogNotImplemented.value = false },
                    onDismiss = { showDialogNotImplemented.value = false }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Email(
    email: String = "",
    showError: Boolean,
    onAccountChange: (newValue: String) -> Unit,
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {}
) {
    OutlinedTextField(
        value = email,
        onValueChange = onAccountChange,
        label = {
            Text(
                text = stringResource(id = R.string.email),
                style = MaterialTheme.typography.labelMedium,
            )
        },
        modifier = Modifier
            .fillMaxWidth(),
        textStyle = MaterialTheme.typography.bodyLarge,
        isError = showError,
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = imeAction,
            keyboardType = KeyboardType.Email
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                onImeAction()
            }
        ),
        supportingText = {
            if (showError) {
                Text(
                    text = "Invalid account or password",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun Password(
    password: String = "",
    showError: Boolean,
    onPasswordChange: (newValue: String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    imeAction: ImeAction = ImeAction.Done,
    onImeAction: () -> Unit = {}
) {
    val showPassword = rememberSaveable { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current

    OutlinedTextField(
        value = password,
        onValueChange = onPasswordChange,
        modifier = modifier
            .fillMaxWidth()
            .width(24.dp),
        textStyle = MaterialTheme.typography.bodyLarge,
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
            )
        },
        trailingIcon = {
            if (showPassword.value) {
                IconButton(onClick = { showPassword.value = false }) {
                    Icon(
                        imageVector = Icons.Filled.Visibility,
                        contentDescription = stringResource(id = R.string.hide_password)
                    )
                }
            } else {
                IconButton(onClick = { showPassword.value = true }) {
                    Icon(
                        imageVector = Icons.Filled.VisibilityOff,
                        contentDescription = stringResource(id = R.string.show_password)
                    )
                }
            }
        },
        isError = showError,
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = imeAction,
            keyboardType = KeyboardType.Password
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                keyboardController?.hide()
                onImeAction()
            }
        ),
        supportingText = {
            if (showError) {
                Text(
                    text = "Invalid account or password",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        visualTransformation = if (showPassword.value) VisualTransformation.None else PasswordVisualTransformation()
    )
}

@Composable
fun RememberMeComponent() {
    val (checkedState, onStateChange) = remember { mutableStateOf(true) }
    Row(
        Modifier
            .fillMaxWidth()
            .height(56.dp)
            .toggleable(
                value = checkedState,
                onValueChange = { onStateChange(!checkedState) },
                role = Role.Checkbox
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checkedState,
            onCheckedChange = null // null recommended for accessibility with screenreaders
        )
        Text(
            text = stringResource(id = R.string.remember_me),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 12.dp)
        )
    }
}

@Composable
fun ForgotPasswordComponent(
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        ClickableText(
            text = AnnotatedString(
                stringResource(id = R.string.forgot_password),
                spanStyle = SpanStyle(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.Underline,
                    fontSize = 14.sp
                )
            ),
            onClick = { onClick() }
        )
    }
}

@Composable
fun LoginSignupDivisorComponent() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Divider()
        Text(
            text = stringResource(id = R.string.or),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .background(Color.White)
                .padding(horizontal = 4.dp)
        )
    }
}

@Composable
fun SignupComponent(
    onClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        ClickableText(
            text =
            AnnotatedString(
                stringResource(id = R.string.dont_have_account)
            ) + AnnotatedString(" ") +
                    AnnotatedString(
                        stringResource(id = R.string.sign_up),
                        spanStyle = SpanStyle(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            textDecoration = TextDecoration.Underline,
                            fontSize = 14.sp
                        )
                    ),
            onClick = { onClick() }
        )
    }
}

//@Composable
//fun LoginScreenPreview() {
//    Project30Theme {
//        LoginScreen(
//            onLoginSubmitted = { _, _ -> },
//            loginState = State<LoginUIState>(),
//        )
//    }
//}