package com.qburst.hackernews.data.repository.topstories

import com.qburst.hackernews.data.repository.topstories.remote.TopStoriesRemoteSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class TopStoriesRepository @Inject constructor(
    private val remoteSource: TopStoriesRemoteSource
) {

    fun getTopStories() {

        CoroutineScope(Dispatchers.IO).launch {

            val stories = remoteSource.getTopStories()

            stories?.forEach {
                println("Story: $it")
            }

        }

    }

}