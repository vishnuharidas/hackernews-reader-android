package com.qburst.hackernews.data.model

sealed class Resource<out T : Any> {
    object None : Resource<Nothing>()
    class Success<out T : Any>(val data: T) : Resource<T>()
    class Error(val throwable: Throwable?) : Resource<Nothing>()
}