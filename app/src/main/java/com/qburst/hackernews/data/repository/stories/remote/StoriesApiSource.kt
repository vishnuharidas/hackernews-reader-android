package com.qburst.hackernews.data.repository.stories.remote

import com.qburst.hackernews.data.model.HNItem
import com.qburst.hackernews.data.model.HNTopStories
import com.qburst.hackernews.data.model.Resource
import com.qburst.hackernews.data.repository.HackerNewsApi
import retrofit2.Response
import javax.inject.Inject


class StoriesApiSource @Inject constructor(
    private val hackerNewsApi: HackerNewsApi
) : StoriesRemoteSource {


    override suspend fun getTopStories(): Resource<HNTopStories> = withExceptionHandling {
        hackerNewsApi.getTopStories()
    }


    override suspend fun getStoryDetails(storyId: Long): Resource<HNItem> = withExceptionHandling {
        hackerNewsApi.getItemDetails(storyId = storyId)
    }


    private suspend fun <T : Any> withExceptionHandling(block: suspend () -> Response<T>): Resource<T> {

        return try {
            val response = block()

            if (response.isSuccessful) {
                Resource.Success(response.body() as T)
            } else {
                Resource.Error(Throwable(response.message()))
            }

        } catch (e: Exception) {
            Resource.Error(Throwable(e.toString()))
        }
    }
}

