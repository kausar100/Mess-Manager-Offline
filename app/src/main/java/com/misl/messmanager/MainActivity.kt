package com.misl.messmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.misl.messmanager.presentation.screens.BazarScreen
import com.misl.messmanager.presentation.screens.DashboardScreen
import com.misl.messmanager.presentation.screens.MealScreen
import com.misl.messmanager.presentation.screens.MemberScreen
import com.misl.messmanager.presentation.screens.SummaryScreen
import com.misl.messmanager.presentation.screens.UtilityScreen
import com.misl.messmanager.ui.theme.MessManagerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MessManagerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "dashboard") {

        composable("dashboard") {
            DashboardScreen(navController = navController)
        }

        composable("members") {
            MemberScreen(viewModel = hiltViewModel(), navController = navController)
        }

        composable("bazar") {
            BazarScreen(navController = navController, viewModel = hiltViewModel())
        }

        composable("summary") {
            SummaryScreen(viewModel = hiltViewModel(), navController = navController)
        }

        composable("meals") {
            MealScreen(navController = navController)
        }


        composable("utilities") {
            UtilityScreen(navController = navController)
        }
    }
}