package com.qburst.hackernews.data.repository.topstories.remote

import com.qburst.hackernews.data.model.HNItem
import com.qburst.hackernews.data.model.HNTopStories
import com.qburst.hackernews.data.repository.HackerNewsApi
import javax.inject.Inject


class TopStoriesApiSource @Inject constructor(
    private val hackerNewsApi: HackerNewsApi
) : TopStoriesRemoteSource {


    override suspend fun getTopStories(): HNTopStories? =
        hackerNewsApi.getTopStories().body()

    override suspend fun getStoryDetails(storyId: Long): HNItem? =
        hackerNewsApi.getItemDetails(storyId = storyId).body()

}

