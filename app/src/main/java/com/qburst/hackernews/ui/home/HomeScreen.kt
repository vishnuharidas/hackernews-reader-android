package com.qburst.hackernews.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.qburst.hackernews.ui.home.viewmodel.HomeUiState
import com.qburst.hackernews.ui.home.viewmodel.HomeViewModel


@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel()
) {

    when (viewModel.uiState.state) {

        HomeUiState.State.None,
        HomeUiState.State.Empty -> Text("Empty")

        HomeUiState.State.Error -> Text("Error: ${viewModel.uiState.error?.localizedMessage}")

        HomeUiState.State.Loading -> CircularProgressIndicator()

        HomeUiState.State.LoadingMore -> Text("Loading More")

        HomeUiState.State.Success -> LazyColumn {
            viewModel.uiState.list.forEach {
                item(key = it.id) {

                    Text(
                        it.title ?: " !~~ ",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate("details/${it.id}")
                            }
                            .padding(16.dp)
                    )
                }
            }
        }

    }

}