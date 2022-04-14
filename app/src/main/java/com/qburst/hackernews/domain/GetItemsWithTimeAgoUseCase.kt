package com.qburst.hackernews.domain

import com.qburst.hackernews.data.model.Resource
import com.qburst.hackernews.data.repository.stories.StoriesRepository
import com.qburst.hackernews.domain.model.HNItemWithTimeAgo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetItemsWithTimeAgoUseCase(
    private val repository: StoriesRepository,
    private val default: String = "sometime ago"
) {

    companion object {
        private const val SECOND_MILLIS = 1000
        private const val MINUTE_MILLIS = 60 * SECOND_MILLIS
        private const val HOUR_MILLIS = 60 * MINUTE_MILLIS
        private const val DAY_MILLIS = 24 * HOUR_MILLIS
    }

    suspend operator fun invoke(list: List<Long>): Flow<Resource<List<HNItemWithTimeAgo>>> =
        repository.fetchStories(list).map {

            when (it) {
                is Resource.Success -> {
                    val tra = it.data.map { item -> HNItemWithTimeAgo(item, ago(item.time)) }
                    Resource.Success(tra)
                }
                is Resource.Error -> {
                    Resource.Error(it.throwable)
                }
                else -> {
                    Resource.None
                }
            }
        }

    private fun ago(t: Long?): String {

        t ?: return default

        val time = if (t < 1_000_000_000_000L) {
            t * 1000
        } else t

        val now = System.currentTimeMillis()
        if (time == 0L || time > now || time <= 0) {
            return default
        }

        val diff = now - time
        return when {
            diff < MINUTE_MILLIS -> {
                "just now"
            }
            diff < 2 * MINUTE_MILLIS -> {
                "a minute ago"
            }
            diff < 50 * MINUTE_MILLIS -> {
                "${diff / MINUTE_MILLIS} minutes"
            }
            diff < 90 * MINUTE_MILLIS -> {
                "an hour ago"
            }
            diff < 24 * HOUR_MILLIS -> {
                "${diff / HOUR_MILLIS} hours"
            }
            diff < 48 * HOUR_MILLIS -> {
                "yesterday"
            }
            else -> {
                "${diff / DAY_MILLIS} days"
            }
        }
    }
}