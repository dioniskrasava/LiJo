package com.majo.lijo.ui



import android.os.Bundle
import androidx.activity.ComponentActivity // ?
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.majo.lijo.ui.details.ListDetailsScreen
import com.majo.lijo.ui.main.MainScreen
import com.majo.lijo.ui.theme.LiJoTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint // Обязательно для Hilt!
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LiJoTheme { // Твоя тема
                TodoListAppNavHost()
            }
        }
    }
}

@Composable
fun TodoListAppNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "main") {

        // Главный экран
        composable("main") {
            MainScreen(
                onListClick = { listId ->
                    navController.navigate("details/$listId")
                }
            )
        }

        // Экран деталей списка
        composable(
            route = "details/{listId}",
            arguments = listOf(navArgument("listId") { type = NavType.LongType })
        ) {
            ListDetailsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}