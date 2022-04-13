package com.qburst.hackernews.data.repository.stories.local

import com.qburst.hackernews.data.model.HNItem

class StoriesMemorySource : StoriesLocalSource {

    private val storiesMap = HashMap<Long, HNItem?>()

    override suspend fun saveItem(item: HNItem) {
        storiesMap[item.id] = item
    }

    override suspend fun saveItems(items: List<HNItem>) {
        items.forEach { saveItem(it) }
    }

    override suspend fun getItemById(itemId: Long): HNItem? {
        return storiesMap[itemId]
    }

    override suspend fun getItemsById(list: List<Long>): List<HNItem?> {
        return list.map { storiesMap[it] }
    }
}