package com.qburst.hackernews

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.qburst.hackernews.ui.home.viewmodel.HomeUiState
import com.qburst.hackernews.ui.home.viewmodel.HomeViewModel
import com.qburst.hackernews.ui.theme.HackerNewsReaderTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val vm by viewModels<HomeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HackerNewsReaderTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {

                    when (vm.uiState.state) {

                        HomeUiState.State.None,
                        HomeUiState.State.Empty -> Text("Empty")
                        HomeUiState.State.Error -> Text("Error: ${vm.uiState.error?.localizedMessage}")
                        HomeUiState.State.Loading -> CircularProgressIndicator()
                        HomeUiState.State.LoadingMore -> Text("Loading More")
                        HomeUiState.State.Success -> LazyColumn() {
                            vm.uiState.list.forEach {
                                item(key = it.id) {

                                    Text(
                                        it.title ?: " !~~ ",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { }
                                            .padding(16.dp)
                                    )
                                }
                            }
                        }


                    }


                }
            }
        }

        vm.getTopStories()
    }
}
