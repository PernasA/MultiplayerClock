package com.pernasA.multiplayerClocks.android.main

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.pernasA.multiplayerClocks.android.models.Player
import com.pernasA.multiplayerClocks.android.view.ChooseTimerPage
import com.pernasA.multiplayerClocks.android.view.GamePage

import com.pernasA.multiplayerClocks.android.view.HomePage
import com.pernasA.multiplayerClocks.android.view.LoadPreviousGamePage
import com.pernasA.multiplayerClocks.android.view.SelectPlayersPage
import com.pernasA.multiplayerclock.android.R

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

    Scaffold(
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
            innerPadding
        )
    }
}
@Composable
private fun CreateNavigationHost(
    navController: NavHostController,
    innerPadding: PaddingValues
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
                        delay(500)
                        navController.navigate(NameOfScreen.LoadGameNav.name)
                        isLoading = false
                    }
                },
                isLoading = isLoading
            )
        }

        composable(route = NameOfScreen.SelectPlayersNav.name) {
            val scope = rememberCoroutineScope()
            var isLoading by remember { mutableStateOf(false) }
            SelectPlayersPage(
                chooseTimerOnClick = { playersList, selectedTimeMode ->
                    scope.launch {
                        isLoading = true
                        delay(1000)
                        navController.navigate(
                            "${NameOfScreen.ChooseTimerNav.name}/$playersList/$selectedTimeMode"
                        )
                        isLoading = false
                    }
                },
//                playersList = listOf(), // Aquí se debe pasar tu lista de jugadores
//                selectedTimeMode = 0 // Aquí puedes pasar el tiempo seleccionado
            )
        }

        val gson = Gson()
        composable(
            route = "${NameOfScreen.ChooseTimerNav.name}/{playersList}/{selectedTimeMode}",
            arguments = listOf(
                navArgument("playersList") { type = NavType.StringType },
                navArgument("selectedTimeMode") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val playersJson = backStackEntry.arguments?.getString("playersList") ?: "[]"
            val selectedTimeMode = backStackEntry.arguments?.getInt("selectedTimeMode") ?: -1

            val playersList: List<Player> = gson.fromJson(
                playersJson,
                object : TypeToken<List<Player>>() {}.type
            )

            ChooseTimerPage(
                chooseTimerOnClick = {
                    navController.navigate(NameOfScreen.GamePageNav.name)
                },
                playersList = playersList,
                selectedTimeMode = selectedTimeMode
            )
        }

        composable(route = NameOfScreen.LoadGameNav.name) {
            LoadPreviousGamePage(
                chooseTimerOnClick = { ->
                    navController.navigate(NameOfScreen.ChooseTimerNav.name)
                },
            )
        }

        composable(route = NameOfScreen.GamePageNav.name) {
            GamePage()
        }
    }
}



fun getCurrentScreen(route: String?): NameOfScreen {
    return when {
        route == null -> NameOfScreen.StartNav
        route == NameOfScreen.SelectPlayersNav.name -> NameOfScreen.SelectPlayersNav
        route == NameOfScreen.LoadGameNav.name -> NameOfScreen.LoadGameNav
        route == NameOfScreen.GamePageNav.name -> NameOfScreen.GamePageNav
        route == NameOfScreen.ChooseTimerNav.name -> NameOfScreen.GamePageNav
        else -> NameOfScreen.StartNav
    }
}
