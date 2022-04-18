package com.qburst.hackernews.data.repository.items.remote

import com.qburst.hackernews.data.model.HNItem
import com.qburst.hackernews.data.model.HNTopStories
import com.qburst.hackernews.data.model.Resource

interface ItemsRemoteSource {

    suspend fun getTopStories(): Resource<HNTopStories>

    suspend fun getItemDetails(itemId: Long): Resource<HNItem>
}