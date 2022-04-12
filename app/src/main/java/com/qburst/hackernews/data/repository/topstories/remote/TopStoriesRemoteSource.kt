package com.qburst.hackernews.data.repository.topstories.remote

import com.qburst.hackernews.data.model.HNItem
import com.qburst.hackernews.data.model.HNTopStories

interface TopStoriesRemoteSource {

    suspend fun getTopStories(): HNTopStories?

    suspend fun getStoryDetails(storyId: Long): HNItem?
}