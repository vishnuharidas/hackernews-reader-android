package com.qburst.hackernews.ui.story_details

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.qburst.hackernews.ui.story_details.viewmodel.StoryDetailsViewModel

@Composable
fun StoryDetails(
    navController: NavHostController,
    storyId: Long,
    viewModel: StoryDetailsViewModel = hiltViewModel()
) {
    Text("Story Details: $storyId")
}
