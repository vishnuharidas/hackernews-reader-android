package com.qburst.hackernews.ui.story_details

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.qburst.hackernews.ui.story_details.viewmodel.ItemDetailsViewModel

@Composable
fun ItemDetailsScreen(
    navController: NavHostController,
    itemId: Long,
    viewModel: ItemDetailsViewModel = hiltViewModel()
) {
    Text("Story Details: $itemId")
}
