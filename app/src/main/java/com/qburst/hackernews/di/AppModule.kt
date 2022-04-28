package com.qburst.hackernews.di

import com.qburst.hackernews.data.repository.items.local.ItemsLocalSource
import com.qburst.hackernews.data.repository.items.local.ItemsMemorySource
import com.qburst.hackernews.data.repository.items.remote.HackerNewsApi
import com.qburst.hackernews.data.repository.items.remote.ItemsApiSource
import com.qburst.hackernews.data.repository.items.remote.ItemsRemoteSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun providesItemsRemoteSource(hackerNewsApi: HackerNewsApi): ItemsRemoteSource {
        return ItemsApiSource(hackerNewsApi)
    }

    @Provides
    @Singleton
    fun providesItemsLocalSource(): ItemsLocalSource {
        return ItemsMemorySource()
    }

}