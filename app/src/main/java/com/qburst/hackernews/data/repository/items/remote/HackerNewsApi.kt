package com.qburst.hackernews.data.repository.items.remote

import com.qburst.hackernews.data.model.HNItem
import com.qburst.hackernews.data.model.HNTopStories
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

// Official HN API documentation: https://github.com/HackerNews/API
interface HackerNewsApi {

    @GET("topstories.json")
    suspend fun getTopStories(): Response<HNTopStories>

    @GET("item/{itemId}.json")
    suspend fun getItemDetails(
        @Path("itemId") itemId: Long
    ): Response<HNItem>

}