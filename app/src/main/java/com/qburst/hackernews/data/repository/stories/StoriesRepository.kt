package com.qburst.hackernews.data.repository.stories

import com.qburst.hackernews.data.model.HNItem
import com.qburst.hackernews.data.model.Resource
import com.qburst.hackernews.data.repository.stories.local.StoriesLocalSource
import com.qburst.hackernews.data.repository.stories.remote.StoriesRemoteSource
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class StoriesRepository @Inject constructor(
    private val remoteSource: StoriesRemoteSource,
    private val localSource: StoriesLocalSource
) {


    fun getTopStories() = flow {

        when (val resource = remoteSource.getTopStories()) {

            Resource.None -> {}

            is Resource.Error -> emit(resource)

            is Resource.Success -> {

                emit(
                    Resource.Success(resource.data.associateWith { null })
                )

            }
        }

    }

    suspend fun fetchStories(ids: List<Long>) = flow {

        val list = mutableListOf<HNItem>()

        coroutineScope {

            ids.forEach { storyId ->

                launch {
                    val item = getStoryDetails(storyId)

                    item?.let { hnItem ->

                        // Keep a local copy for future use
                        localSource.saveItem(hnItem)

                        list.add(item)
                    }
                }
            }
        }

        if (ids.size == list.size) {
            emit(Resource.Success(list.toList()))
        } else {
            emit(Resource.Error(Throwable("Unable to fetch items at this time")))
        }

    }


    private suspend fun getStoryDetails(storyId: Long): HNItem? {

        return when (val res = remoteSource.getStoryDetails(storyId = storyId)) {
            is Resource.Error -> null
            Resource.None -> null
            is Resource.Success -> res.data
        }

    }

}