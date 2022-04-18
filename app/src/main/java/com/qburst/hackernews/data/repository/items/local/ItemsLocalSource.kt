package com.qburst.hackernews.data.repository.items.local

import com.qburst.hackernews.data.model.HNItem

interface ItemsLocalSource {

    suspend fun saveItem(item: HNItem)

    suspend fun saveItems(items: List<HNItem>)

    suspend fun getItemById(itemId: Long): HNItem?

    suspend fun getItemsById(list: List<Long>): List<HNItem?>

}