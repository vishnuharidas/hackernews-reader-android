package com.qburst.hackernews.data.repository.items.local

import com.qburst.hackernews.data.model.HNItem
import com.qburst.hackernews.data.model.HNTopStories

class ItemsMemorySource : ItemsLocalSource {

    private var topStories = mutableListOf<Long>()

    private val itemsMap = HashMap<Long, HNItem?>()

    private var cachedAt = 0L // Nothing cached when starting, so set the time to 0 (oh, 1970! 😁)

    companion object {
        private const val SECOND_MILLIS = 1000
        private const val MINUTE_MILLIS = 60 * SECOND_MILLIS

        private const val CACHE_PERIOD_MILLIS = 10 * MINUTE_MILLIS /* 10 minutes cache */
    }

    override suspend fun isValid(): Boolean {

        return System.currentTimeMillis() - cachedAt < CACHE_PERIOD_MILLIS
    }

    override suspend fun saveTopStories(stories: HNTopStories) {

        if (stories.isEmpty()) return // without caching an empty list

        topStories.apply {
            clear()
            addAll(stories)
        }

        cachedAt = System.currentTimeMillis()
    }

    override suspend fun getTopStories(): HNTopStories = topStories

    override suspend fun saveItem(item: HNItem) {
        itemsMap[item.id] = item
    }

    override suspend fun saveItems(items: List<HNItem>) {
        items.forEach { saveItem(it) }
    }

    override suspend fun getItemById(itemId: Long): HNItem? {
        return itemsMap[itemId]
    }

    override suspend fun getItemsById(list: List<Long>): List<HNItem?> {
        return list.map { itemsMap[it] }
    }
}