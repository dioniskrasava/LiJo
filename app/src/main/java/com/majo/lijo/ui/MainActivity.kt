package com.majo.lijo.ui



import android.os.Bundle
import androidx.activity.ComponentActivity // ?
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.majo.lijo.data.local.ThemeManager
import com.majo.lijo.ui.details.ListDetailsScreen
import com.majo.lijo.ui.main.MainScreen
import com.majo.lijo.ui.settings.SettingsScreen
import com.majo.lijo.ui.theme.LiJoTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint // Обязательно для Hilt!
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var themeManager: ThemeManager // Внедряем менеджер

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Подписываемся на тему. По умолчанию используем системную, пока данные грузятся
            val isDarkTheme by themeManager.isDarkMode.collectAsState(initial = false)

            LiJoTheme(darkTheme = isDarkTheme) {
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
                },
                onSettingsClick = { // ДОБАВИЛИ ЭТОТ ПАРАМЕТР
                    navController.navigate("settings")
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

        // ДОБАВИЛИ ЭКРАН НАСТРОЕК
        composable("settings") {
            SettingsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}