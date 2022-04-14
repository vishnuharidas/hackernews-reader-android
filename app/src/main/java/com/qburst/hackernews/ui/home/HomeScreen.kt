package com.qburst.hackernews.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.qburst.hackernews.data.model.HNItem
import com.qburst.hackernews.ui.home.viewmodel.HomeUiState
import com.qburst.hackernews.ui.home.viewmodel.HomeViewModel
import com.qburst.hackernews.ui.home.viewmodel.hasMore
import kotlinx.coroutines.flow.collect


@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel()
) {

    when (viewModel.uiState.state) {

        HomeUiState.State.None,
        HomeUiState.State.Empty -> Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Text(
                "No stories at this time. Please try later.",
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .padding(16.dp),
                textAlign = TextAlign.Center
            )
        }

        HomeUiState.State.Error -> Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                viewModel.uiState.error?.localizedMessage ?: "Unknown error. Please try later.",
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .padding(16.dp),
                textAlign = TextAlign.Center
            )
        }

        HomeUiState.State.Loading -> Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .width(40.dp)
                    .align(Alignment.Center),
                strokeWidth = 1.dp
            )
        }

        HomeUiState.State.LoadingMore,
        HomeUiState.State.LoadingMoreError,
        HomeUiState.State.Success ->
            NewsList(
                viewModel.uiState,
                onClick = { item -> navController.navigate("details/${item.id}") },
                onMore = { if (viewModel.uiState.hasMore()) viewModel.fetchNextPage() }
            )

    }

}

@Composable
private fun NewsList(
    uiState: HomeUiState,
    onClick: (HNItem) -> Unit,
    onMore: () -> Unit,
    onRetryPage: (() -> Unit)? = null,
) {

    val onLoadMore by rememberUpdatedState(onMore)

    val listState = rememberLazyListState()

    val canLoadMore = remember {

        // Using derived state because reading the listState otherwise will cause a recomposition,
        // which will trigger an infinite recomposition.
        //
        // Ref: https://issuetracker.google.com/issues/216499432
        //
        // A derived state will not trigger the recomposition of this entire Composable, instead it
        // will recompose only those who are reading the value of it.

        derivedStateOf {

            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull() ?: return@derivedStateOf true

            lastVisibleItem.index >= listState.layoutInfo.totalItemsCount - 2 // 2nd last item

        }
    }

    LaunchedEffect(canLoadMore) {
        snapshotFlow { canLoadMore.value }
            .collect {
                if (it) onLoadMore()
            }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        LazyColumn(
            state = listState
        ) {

            itemsIndexed(uiState.list) { index, item ->

                Text(
                    "$index\n${item.title}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onClick(item) }
                        .padding(16.dp)
                )

            }

        }

        // Loading more indicator
        if (uiState.state == HomeUiState.State.LoadingMore) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.5f))
                    .padding(8.dp)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(20.dp),
                    strokeWidth = 2.dp
                )
            }
        }

        if (uiState.state == HomeUiState.State.LoadingMoreError) {
            Text(
                uiState.error?.localizedMessage ?: "Unable to fetch latest stories at this time. Please try later.",
                modifier = Modifier
                    .align(if (uiState.list.isNullOrEmpty()) Alignment.Center else Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.5f))
                    .padding(8.dp),
                textAlign = TextAlign.Center
            )
        }

    }
}