package com.qburst.hackernews.data.repository.topstories.remote

import com.qburst.hackernews.data.model.HNItem
import com.qburst.hackernews.data.model.HNTopStories
import com.qburst.hackernews.data.model.Resource

interface TopStoriesRemoteSource {

    suspend fun getTopStories(): Resource<HNTopStories>

    suspend fun getStoryDetails(storyId: Long): Resource<HNItem>
}