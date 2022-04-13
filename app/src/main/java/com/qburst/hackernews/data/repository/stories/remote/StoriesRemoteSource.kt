package com.qburst.hackernews.data.repository.stories.remote

import com.qburst.hackernews.data.model.HNItem
import com.qburst.hackernews.data.model.HNTopStories
import com.qburst.hackernews.data.model.Resource

interface StoriesRemoteSource {

    suspend fun getTopStories(): Resource<HNTopStories>

    suspend fun getStoryDetails(storyId: Long): Resource<HNItem>
}