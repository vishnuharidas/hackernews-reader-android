package com.qburst.hackernews.di

import com.qburst.hackernews.data.repository.HackerNewsApi
import com.qburst.hackernews.data.repository.stories.remote.StoriesApiSource
import com.qburst.hackernews.data.repository.stories.remote.StoriesRemoteSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun providesStoriesRemoteSource(hackerNewsApi: HackerNewsApi): StoriesRemoteSource {
        return StoriesApiSource(hackerNewsApi)
    }

}