package com.qburst.hackernews.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.qburst.hackernews.ui.home.HomeScreen
import com.qburst.hackernews.ui.story_details.ItemDetailsScreen
import com.qburst.hackernews.ui.theme.HackerNewsReaderTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HackerNewsReaderTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {

                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = "home"
                    ) {


                        composable("home") {
                            HomeScreen(navController)
                        }

                        composable(
                            "details/{itemId}",
                            arguments = listOf(navArgument("itemId") { type = NavType.LongType })
                        ) {
                            ItemDetailsScreen(
                                navController,
                                it.arguments?.getLong("itemId") ?: 0L
                            )
                        }

                    }


                }
            }
        }

    }
}
