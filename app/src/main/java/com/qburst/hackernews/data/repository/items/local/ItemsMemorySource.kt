package com.qburst.hackernews.data.repository.items.local

import com.qburst.hackernews.data.model.HNItem

class ItemsMemorySource : ItemsLocalSource {

    private val itemsMap = HashMap<Long, HNItem?>()

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