package com.qburst.hackernews.di

import com.qburst.hackernews.data.repository.HackerNewsApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun provideHackerNewsApi(): HackerNewsApi {

        val baseUrl = "https://hacker-news.firebaseio.com/v0/"

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(HackerNewsApi::class.java)

    }
}