package com.qburst.hackernews.ui.story_details

import android.text.Html
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.qburst.hackernews.data.model.HNItem
import com.qburst.hackernews.domain.GetTimeAgoUseCase
import com.qburst.hackernews.ui.story_details.viewmodel.ItemDetailsUiState
import com.qburst.hackernews.ui.story_details.viewmodel.ItemDetailsViewModel

@Composable
fun ItemDetailsScreen(
    navController: NavHostController,
    itemId: Long,
    viewModel: ItemDetailsViewModel = hiltViewModel()
) {

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        when (val state = viewModel.uiState.state) {

            ItemDetailsUiState.State.Loading ->
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(40.dp)
                )

            is ItemDetailsUiState.State.Failure ->
                Text(
                    state.error,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                )

            is ItemDetailsUiState.State.Success ->
                ItemDetails(
                    navController,
                    state.item,
                    modifier = Modifier
                        .fillMaxSize()
                )
        }

    }

}

@Composable
private fun ItemDetails(
    navController: NavHostController,
    item: HNItem,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {

        Text(
            item.title ?: "-",
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Normal
            )
        )

        val meta = buildAnnotatedString {

            append(item.score?.toString() ?: "0")
            append(" points")
            append(" by ")
            withStyle(SpanStyle(fontWeight = FontWeight.Medium)) {
                append(item.by ?: "Unknown")
            }

            append(" posted ")
            withStyle(SpanStyle(fontWeight = FontWeight.Medium)) {
                append(GetTimeAgoUseCase(item.time))
            }
            append(" ago")

            append("  \uD83D\uDCAC ")
            append(item.descendants?.toString() ?: "no comments")
        }

        Text(
            meta,
            style = TextStyle(
                color = Color.Gray
            )
        )

        if (!item.text.isNullOrBlank()) { // "Ask HN" post will have text
            Text(
                Html.fromHtml(item.text, Html.FROM_HTML_MODE_LEGACY).toString(),
                style = TextStyle(
                    fontSize = 18.sp
                ),
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        Divider(
            modifier = Modifier.padding(vertical = 16.dp)
        )

    }
}
