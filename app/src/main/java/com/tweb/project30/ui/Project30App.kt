package com.tweb.project30.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.tweb.project30.R
import com.tweb.project30.data.user.UserRepository
import com.tweb.project30.ui.components.ErrorDialog
import com.tweb.project30.ui.home.HomeScreen
import com.tweb.project30.ui.home.HomeViewModel
import com.tweb.project30.ui.home.HomeViewModelFactory
import com.tweb.project30.ui.login.LoginScrenn
import com.tweb.project30.ui.login.LoginViewModel
import com.tweb.project30.ui.login.LoginViewModelFactory
import com.tweb.project30.ui.profile.ProfileScreen
import com.tweb.project30.ui.profile.ProfileViewModel
import com.tweb.project30.ui.profile.ProfileViewModelFactory
import com.tweb.project30.ui.repetitions.FilterScreen
import com.tweb.project30.ui.repetitions.RepetitionsScreen
import com.tweb.project30.ui.repetitions.RepetitionsViewModel
import com.tweb.project30.ui.repetitions.RepetitionsViewModelFactory
import com.tweb.project30.ui.repetitions.UserRepetitionsScreen
import com.tweb.project30.ui.repetitions.UserRepetitionsViewModel
import com.tweb.project30.ui.repetitions.UserRepetitionsViewModelFactory

@Composable
fun Project30App(
    appState: Project30AppState = rememberProject30AppState()
) {
//    if (appState.isOnline) {
    BottomNavigationBar(appState)
//    } else {
//        OfflineDialog { appState.refreshOnline() }
//    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavigationBar(appState: Project30AppState) {
    val navController = appState.navController
    val loginVM: LoginViewModel = viewModel(factory = LoginViewModelFactory())
    val homeVM: HomeViewModel = viewModel(factory = HomeViewModelFactory())
    val repetitionsVM: RepetitionsViewModel = viewModel(factory = RepetitionsViewModelFactory())
    val userRepetitionsVM: UserRepetitionsViewModel =
        viewModel(factory = UserRepetitionsViewModelFactory())
    val profileVM: ProfileViewModel = viewModel(factory = ProfileViewModelFactory())


    Scaffold(
        bottomBar = {
            BottomBar(navController = navController)
        }
    ) {
        Box(
            modifier = Modifier.padding(it)
        ) {
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route
            ) {

                composable(Screen.Login.route) {
                    LoginScrenn(
                        viewModel = loginVM,
                        onLoginCompleted = { navController.navigate(Screen.Home.route) }
                    )
                }

                composable(Screen.Home.route) {
                    HomeScreen(
                        viewModel = homeVM,
                        onUserActionClicked = {
                            navController.navigate(
                                if (UserRepository.isLogged?.value == true) Screen.Profile.route else Screen.Login.route
                            )
                        },
                        onRepetitionClicked = { navController.navigate(Screen.Repetitions.route) }
                    )
                }

                composable(Screen.NestedScreen.Filter.route) {
                    FilterScreen(repetitionsVM, onBackPressed = { navController.popBackStack() })
                }

                composable(Screen.Repetitions.route) {
                    UserRepetitionsScreen(
                        userRepetitionsVM,
                        onLoginClicked = { navController.navigate(Screen.Login.route) }
                    )
                }

                composable(Screen.Calendar.route) {
                    RepetitionsScreen(
                        repetitionsVM,
                        onFilterButtonClicked = { navController.navigate(Screen.NestedScreen.Filter.route) },
                        onLoginClicked = { navController.navigate(Screen.Login.route) }
                    )
                }

                composable(Screen.Profile.route) {
                    ProfileScreen(
                        profileVM,
                        onLogout = { navController.navigate(Screen.Login.route) }
                    )
                }
            }
        }
    }
}

@Composable
fun BottomBar(navController: NavHostController) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val isLogged by UserRepository.isLogged.observeAsState(false)

    var screens: List<Screen> = listOf(
        Screen.Home,
        Screen.Repetitions,
        Screen.Calendar,
    )

    if (isLogged) {
        screens = screens.plus(Screen.Profile)
    } else
        screens = screens.plus(Screen.Login)

    BottomAppBar() {
        screens.forEach { screen ->
            AddItem(
                screen = screen,
                currentDestination = currentDestination,
                navController = navController
            )
        }
    }

//    Row(
//        modifier = Modifier
////            .padding(start = 10.dp, end = 10.dp, top = 8.dp, bottom = 8.dp)
//            .height(56.dp)
//            .background(Color.Transparent)
//            .fillMaxWidth()
//            .drawBehind {
//                drawLine(
//                    color = Color(0xFF009688),
//                    start = Offset(0f, 0f),
//                    end = Offset(size.width, 0f),
//                    strokeWidth = 1f
//                )
//            },
//        horizontalArrangement = Arrangement.SpaceEvenly,
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        screens.forEach { screen ->
//            AddItem(
//                screen = screen,
//                currentDestination = currentDestination,
//                navController = navController
//            )
//        }
//    }
}

@Composable
fun RowScope.AddItem(
    screen: Screen,
    currentDestination: NavDestination?,
    navController: NavHostController
) {
    NavigationBarItem(
        label = {
            Text(text = screen.label)
        },
        icon = {
            Icon(
                imageVector = screen.icon,
                contentDescription = "Navigation Icon"
            )
        },
        selected = currentDestination?.hierarchy?.any {
            it.route?.contains(screen.route) ?: false
        } == true,
        onClick = {
            navController.navigate(screen.route) {
                popUpTo(navController.graph.findStartDestination().id)
                launchSingleTop = true
                restoreState = true
            }
        },
//        alwaysShowLabel = false
    )
}

@Composable
fun OfflineDialog(onRetry: () -> Unit) {
    ErrorDialog(
        title = stringResource(R.string.connection_error_title),
        message = stringResource(R.string.connection_error_message),
        confirmButton = stringResource(R.string.retry_label),
        onConfirm = onRetry,
        onDismiss = {}
    )
}