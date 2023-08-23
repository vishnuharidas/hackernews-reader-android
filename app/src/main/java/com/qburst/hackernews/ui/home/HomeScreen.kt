package com.qburst.hackernews.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.qburst.hackernews.domain.model.HNItemWithTimeAgo
import com.qburst.hackernews.ui.home.viewmodel.HomeUiState
import com.qburst.hackernews.ui.home.viewmodel.HomeViewModel
import com.qburst.hackernews.ui.home.viewmodel.hasMore


@Composable
fun HomeScreen(
    onDetails: (id: Long) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {

    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing = viewModel.uiState.state == HomeUiState.State.Loading),
        onRefresh = {
            viewModel.getTopStories(force = true)
        },
        modifier = Modifier.fillMaxSize()
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

            HomeUiState.State.Loading,
            HomeUiState.State.LoadingMore,
            HomeUiState.State.LoadingMoreError,
            HomeUiState.State.Success ->
                NewsList(
                    viewModel.uiState,
                    onClick = {
                        onDetails(it.item.id)
                    },
                    onMore = { if (viewModel.uiState.hasMore()) viewModel.fetchNextPage() }
                )

        }

    }

}

@Composable
private fun NewsList(
    uiState: HomeUiState,
    onClick: (HNItemWithTimeAgo) -> Unit,
    onMore: () -> Unit,
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

            lastVisibleItem.index >= listState.layoutInfo.totalItemsCount - 3 // 3nd last item

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

                ListItem(
                    index + 1,
                    item,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onClick(item) }
                )

            }

            // Loading more indicator
            if (uiState.state == HomeUiState.State.LoadingMore) {
                item {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(30.dp),
                            strokeWidth = 1.dp
                        )
                    }
                }
            }

            if (uiState.state == HomeUiState.State.LoadingMoreError) {

                item {
                    Text(
                        uiState.error?.localizedMessage
                            ?: "Unable to fetch latest stories at this time. Please try later.",
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

    }
}

@Composable
private fun ListItem(
    index: Int,
    itemWithTimeAgo: HNItemWithTimeAgo,
    modifier: Modifier = Modifier
) {

    Box(
        modifier = modifier
            .padding(16.dp)
    ) {

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {

            Text(
                "$index. ${itemWithTimeAgo.item.title ?: "<no title>"}",
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Normal
                )
            )

            val meta = buildAnnotatedString {

                append(itemWithTimeAgo.item.score?.toString() ?: "0")
                append(" points")
                append(" by ")
                withStyle(SpanStyle(fontWeight = FontWeight.Medium)) {
                    append(itemWithTimeAgo.item.by ?: "Unknown")
                }

                append(" posted ")
                withStyle(SpanStyle(fontWeight = FontWeight.Medium)) {
                    append(itemWithTimeAgo.timeAgo)
                }

                append("  \uD83D\uDCAC ")
                append(itemWithTimeAgo.item.descendants?.toString() ?: "no comments")
            }

            Text(
                meta,
                style = TextStyle(
                    color = Color.Gray
                )
            )


        }

    }

}