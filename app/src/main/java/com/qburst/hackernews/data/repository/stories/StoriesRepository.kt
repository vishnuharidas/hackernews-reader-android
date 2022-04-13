package com.qburst.hackernews.data.repository.stories

import com.qburst.hackernews.data.model.HNItem
import com.qburst.hackernews.data.model.Resource
import com.qburst.hackernews.data.repository.stories.remote.StoriesRemoteSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class StoriesRepository @Inject constructor(
    private val remoteSource: StoriesRemoteSource
) {

    private val _topMap = LinkedHashMap<Long, HNItem?>()

    private val _topStoriesFlow = MutableStateFlow<Resource<List<HNItem>>>(Resource.None)
    val topStoriesFlow: Flow<Resource<List<HNItem>>> get() = _topStoriesFlow

    fun getTopStories() {

        CoroutineScope(Dispatchers.IO).launch {

            when (val resource = remoteSource.getTopStories()) {

                Resource.None -> {}

                is Resource.Error -> _topStoriesFlow.emit(resource)

                is Resource.Success -> {

                    // Add to the Map first, then fetch 10 items per page
                    resource.data.forEach { _topMap[it] = null }

                    nextPage()
                }
            }

        }

    }

    suspend fun nextPage() {

        CoroutineScope(Dispatchers.IO).launch {

            // Find the next 10 items with null values
            coroutineScope {

                _topMap.filter { it.value == null }.keys.take(10).forEach { storyId ->

                    launch {
                        val item = getStoryDetails(storyId)

                        item?.let { hnItem ->
                            _topMap[storyId] = hnItem

                            // Emit the stories
                            _topStoriesFlow.emit(
                                Resource.Success(_topMap.values.toList().filterNotNull())
                            )
                        }
                    }


                }
            }

            // If no data are available even after trying to fetch items, send an error text
            if (_topMap.values.filterNotNull().isEmpty()) {
                _topStoriesFlow.emit(
                    Resource.Success(emptyList())
                )
            }
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