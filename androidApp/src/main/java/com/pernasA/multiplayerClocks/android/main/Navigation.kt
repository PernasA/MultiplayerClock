package com.pernasA.multiplayerClocks.android.main

import android.app.Application
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.pernasA.multiplayerClocks.android.view.ChooseTimerPage
import com.pernasA.multiplayerClocks.android.view.GamePage

import com.pernasA.multiplayerClocks.android.view.HomePage
import com.pernasA.multiplayerClocks.android.view.LoadPreviousGamePage
import com.pernasA.multiplayerClocks.android.view.SelectPlayersPage
import com.pernasA.multiplayerClocks.android.viewModel.SharedViewModel
import com.pernasA.multiplayerClocks.android.viewModel.SoundsController
import com.pernasA.multiplayerclock.android.R
import kotlinx.coroutines.CoroutineScope

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class NameOfScreen(@StringRes val title: Int) {
    StartNav(title = R.string.main_page_appbar_title),
    SelectPlayersNav(title = R.string.select_players_title),
    LoadGameNav(title = R.string.load_saved_game_title),
    ChooseTimerNav(title = R.string.choose_timer_title),
    GamePageNav(title = R.string.game_page_title)
}

@Composable
fun Navigation(
    navController: NavHostController = rememberNavController(),
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = getCurrentScreen(backStackEntry?.destination?.route)
    val localContext = LocalContext.current.applicationContext as Application
    val soundsController = SoundsController(localContext)
    val sharedViewModel: SharedViewModel = viewModel()

    sharedViewModel.setNavController(navController)
    sharedViewModel.setSoundsController(soundsController)

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    modifier = Modifier.padding(16.dp),
                    shape = RoundedCornerShape(12.dp),
                    containerColor = Color(0xFF333333), // Color de fondo personalizado
                    contentColor = Color.White,
                    actionColor = Color.Yellow, // Color del botón de acción
                )
            }
                       },
        modifier = Modifier,
        topBar = {
            MyAppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() }
            )
        }
    ) { innerPadding ->
        CreateNavigationHost(
            navController,
            innerPadding,
            sharedViewModel,
            snackbarHostState,
            coroutineScope
        )
    }
}

@Composable
private fun CreateNavigationHost(
    navController: NavHostController,
    innerPadding: PaddingValues,
    sharedViewModel: SharedViewModel,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope
) {
    NavHost(
        navController = navController,
        startDestination = NameOfScreen.StartNav.name,
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        composable(route = NameOfScreen.StartNav.name) {
            val scope = rememberCoroutineScope()
            var isLoading by remember { mutableStateOf(false) }
            val localContext = LocalContext.current
            HomePage(
                selectPlayersOnClick = {
                    scope.launch {
                        isLoading = true
                        delay(1000)
                        navController.navigate(NameOfScreen.SelectPlayersNav.name)
                        isLoading = false
                    }
                },

                loadGameOnClick = {
                    scope.launch {
                        isLoading = true
                        val hasLoadedGame = sharedViewModel.loadGame(localContext)
                        delay(500)
                        if (hasLoadedGame) {
                            navController.navigate(NameOfScreen.GamePageNav.name)
                        } else {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "No tiene ningún juego guardado. Por favor inicie una nueva partida.",
                                    actionLabel = "OK",
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }

                        isLoading = false
                    }
                },
                isLoading = isLoading,
                sharedViewModel
            )
        }

        composable(route = NameOfScreen.SelectPlayersNav.name) {
            val scope = rememberCoroutineScope()
            var isLoading by remember { mutableStateOf(false) }
            SelectPlayersPage(
                goToChooseTimerOnClick = {
                    scope.launch {
                        isLoading = true
                        delay(1000)
                        navController.navigate(
                            NameOfScreen.ChooseTimerNav.name
                        )
                        isLoading = false
                    }
                },
                sharedViewModel
            )
        }

        composable(
            route = NameOfScreen.ChooseTimerNav.name,
        ) {
            ChooseTimerPage(
                goToGamePageOnClick = {
                    navController.navigate(NameOfScreen.GamePageNav.name)
                },
                sharedViewModel
            )
        }

        composable(route = NameOfScreen.LoadGameNav.name) {
            LoadPreviousGamePage(
                chooseTimerOnClick = { ->
                    navController.navigate(NameOfScreen.ChooseTimerNav.name)
                },
                sharedViewModel
            )
        }

        composable(route = NameOfScreen.GamePageNav.name) {
            GamePage(
                sharedViewModel
            )
        }
    }
}



fun getCurrentScreen(route: String?): NameOfScreen {
    return when {
        route == null -> NameOfScreen.StartNav
        route == NameOfScreen.SelectPlayersNav.name -> NameOfScreen.SelectPlayersNav
        route == NameOfScreen.LoadGameNav.name -> NameOfScreen.LoadGameNav
        route == NameOfScreen.GamePageNav.name -> NameOfScreen.GamePageNav
        route == NameOfScreen.ChooseTimerNav.name -> NameOfScreen.ChooseTimerNav
        else -> NameOfScreen.StartNav
    }
}
