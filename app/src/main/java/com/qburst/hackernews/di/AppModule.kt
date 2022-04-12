package com.qburst.hackernews.di

import com.qburst.hackernews.data.repository.HackerNewsApi
import com.qburst.hackernews.data.repository.topstories.remote.TopStoriesApiSource
import com.qburst.hackernews.data.repository.topstories.remote.TopStoriesRemoteSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun providesTopStoriesRemoteSource(hackerNewsApi: HackerNewsApi): TopStoriesRemoteSource {
        return TopStoriesApiSource(hackerNewsApi)
    }

}