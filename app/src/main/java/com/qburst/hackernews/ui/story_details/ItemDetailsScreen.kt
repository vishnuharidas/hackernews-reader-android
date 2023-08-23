package com.qburst.hackernews.ui.story_details

import android.content.Intent
import android.net.Uri
import android.text.Html
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.qburst.hackernews.R
import com.qburst.hackernews.data.model.HNItemType
import com.qburst.hackernews.data.model.getTypeValue
import com.qburst.hackernews.ui.story_details.viewmodel.ItemDetailsUiState
import com.qburst.hackernews.ui.story_details.viewmodel.ItemDetailsViewModel
import com.qburst.hackernews.ui.theme.HackerNewsReaderTheme

@Composable
fun ItemDetailsScreen(
    onDetailsClick: (itemId: Long) -> Unit,
    itemId: Long,
    viewModel: ItemDetailsViewModel = hiltViewModel()
) {

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        when (viewModel.uiState.state) {

            ItemDetailsUiState.State.Loading ->
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(40.dp)
                )

            is ItemDetailsUiState.State.Failure ->
                Text(
                    viewModel.uiState.error ?: "Unknown error. Please try later.",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                )

            ItemDetailsUiState.State.LoadingMore,
            is ItemDetailsUiState.State.Success -> {
                ItemDetails(
                    onDetailsClick = onDetailsClick,
                    uiState = viewModel.uiState,
                    modifier = Modifier.fillMaxSize(),
                    onNextPage = {
                        viewModel.fetchNextComments()
                    }
                )

            }

        }

    }

}

@Composable
private fun ItemDetails(
    onDetailsClick: (itemId: Long) -> Unit,
    uiState: ItemDetailsUiState,
    modifier: Modifier = Modifier,
    onNextPage: () -> Unit
) {

    val itemWithTimeAgo = uiState.item

    val hnItem = itemWithTimeAgo?.item
    val comments = uiState.comments

    if (hnItem == null) {
        Box(
            modifier = modifier
        ) {
            Text(
                uiState.error ?: "Unknown error. Please try later.",
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp)
            )
        }
        return
    }

    val context = LocalContext.current

    val listState = rememberLazyListState()

    val canLoadMore = remember {

        derivedStateOf {

            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull() ?: return@derivedStateOf true

            lastVisibleItem.index >= listState.layoutInfo.totalItemsCount - 2 // 2nd last item

        }
    }

    LaunchedEffect(canLoadMore) {
        snapshotFlow { canLoadMore.value }
            .collect {
                if (it) onNextPage()
            }
    }

    Box(
        modifier = modifier
    ) {

        LazyColumn(
            modifier = modifier,
            state = listState
        ) {

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 16.dp),
                    verticalAlignment = Alignment.Top
                ) {

                    if (!hnItem.title.isNullOrEmpty()) {
                        Text(
                            hnItem.title,
                            style = TextStyle(
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Normal
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = if (hnItem.url != null) 8.dp else 0.dp)
                        )
                    }

                    if (hnItem.url != null) {
                        IconButton(
                            onClick = {

                                if (hnItem.getTypeValue() == HNItemType.Story
                                    && hnItem.title?.startsWith("Ask HN:") == false /* Ask HN will be opened in a screen */) {
                                    context.startActivity(
                                        Intent(Intent.ACTION_VIEW, Uri.parse(hnItem.url))
                                    )
                                }

                            },
                            modifier = Modifier
                                .border(width = 1.dp, color = Color.LightGray, shape = CircleShape)
                        ) {

                            Icon(Icons.Outlined.ArrowForward, contentDescription = "Open in Browser")

                        }
                    }

                }
            }

            item {
                val meta = buildAnnotatedString {

                    if (hnItem.score != null) {
                        append(hnItem.score.toString())
                        append(" points ")
                    }

                    append("by ")
                    withStyle(SpanStyle(fontWeight = FontWeight.Medium)) {
                        append(hnItem.by ?: "Unknown")
                    }

                    append(" posted ")
                    withStyle(SpanStyle(fontWeight = FontWeight.Medium)) {
                        append(itemWithTimeAgo.timeAgo)
                    }

                    append("  \uD83D\uDCAC ")
                    if (hnItem.getTypeValue() == HNItemType.Comment) {
                        append(hnItem.kids?.size?.toString() ?: "no replies")
                    } else {
                        append(hnItem.descendants?.toString() ?: "no comments")
                    }
                }

                Text(
                    meta,
                    style = TextStyle(
                        color = Color.Gray
                    ),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            if (!hnItem.text.isNullOrBlank()) { // "Ask HN" post will have text
                item {
                    Text(
                        Html.fromHtml(hnItem.text, Html.FROM_HTML_MODE_LEGACY).toString(),
                        style = TextStyle(
                            fontSize = 18.sp
                        ),
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .padding(top = 16.dp)
                    )
                }
            }

            // Links in the post
            if (!itemWithTimeAgo.links.isNullOrEmpty()) {

                item {
                    Divider(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .padding(vertical = 16.dp)
                    )
                }

                item {
                    Text(
                        "Links (${itemWithTimeAgo.links.size}):",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontStyle = FontStyle.Italic
                        ),
                        modifier = Modifier
                            .padding(start = 16.dp, top = 8.dp, bottom = 8.dp)

                    )
                }

                itemWithTimeAgo.links.forEachIndexed { index, it ->

                    item {
                        LinksListItem(
                            index = index + 1,
                            text = it,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    context.startActivity(
                                        Intent(Intent.ACTION_VIEW, Uri.parse(it))
                                    )
                                }
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                        )
                    }
                }
            }


            item {
                Divider(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(vertical = 16.dp)
                )
            }

            if (!comments.isNullOrEmpty()) {

                comments.filter { it.item.deleted != true && it.item.dead != true }
                    .forEach {

                        val commentItem = it.item

                        item {

                            Column(
                                modifier
                                    .clickable {
                                        onDetailsClick(commentItem.id)
                                    }
                                    .padding(vertical = 16.dp)
                            ) {

                                val meta = buildAnnotatedString {

                                    append("by ")
                                    withStyle(SpanStyle(fontWeight = FontWeight.Medium)) {
                                        append(commentItem.by ?: "Unknown")
                                    }

                                    append(" ")
                                    withStyle(SpanStyle(fontWeight = FontWeight.Medium)) {
                                        append(it.timeAgo)
                                    }

                                    if (commentItem.kids?.size ?: 0 > 0) {
                                        append(" ")
                                        append(commentItem.kids?.size?.toString() ?: "0")
                                        append(" replies")
                                    }

                                }

                                Text(
                                    meta,
                                    style = TextStyle(
                                        color = Color.Gray
                                    ),
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp)
                                )

                                Text(
                                    Html.fromHtml(commentItem.text ?: "---", Html.FROM_HTML_MODE_LEGACY).toString(),
                                    style = TextStyle(
                                        fontSize = 15.sp
                                    ),
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp)
                                        .padding(top = 8.dp)
                                )

                                if (commentItem.kids?.size ?: 0 > 0) {

                                    Text(
                                        "view ${
                                            context.resources.getQuantityString(
                                                R.plurals.replies,
                                                commentItem.kids?.size ?: 0,
                                                commentItem.kids?.size ?: 0
                                            )
                                        } ➡",
                                        modifier = Modifier
                                            .padding(start = 16.dp, top = 8.dp)
                                            .border(1.dp, Color.LightGray, RoundedCornerShape(40.dp))
                                            .clip(RoundedCornerShape(40.dp))
                                            .clickable {
                                                onDetailsClick(commentItem.id)
                                            }
                                            .padding(vertical = 4.dp, horizontal = 8.dp)

                                    )

                                }

                            }

                        }

                        item {
                            Divider()
                        }

                    }

            }

            // Loading more indicator
            if (uiState.state == ItemDetailsUiState.State.LoadingMore) {
                item {
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
            }

        }

    }
}

@Composable
internal fun LinksListItem(
    index: Int,
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            "$index",
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier
                .padding(end = 12.dp)
        )

        Text(
            text,
            modifier = Modifier
                .weight(1.0f)
        )

        Icon(
            Icons.Outlined.KeyboardArrowRight,
            tint = Color.Gray,
            contentDescription = "Open Link in Browser",
            modifier = Modifier
                .padding(start = 4.dp),
        )

    }
}

@Preview
@Composable
fun LinksListItemPreview() {
    Surface {
        HackerNewsReaderTheme {
            LinksListItem(
                index = 1,
                text = "https://news.ycombinator.com/a-long-long-path/with-a-long-long-stub",
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        //
                    }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            )
        }
    }
}