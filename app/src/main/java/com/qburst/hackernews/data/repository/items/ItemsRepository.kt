package com.qburst.hackernews.data.repository.items

import com.qburst.hackernews.data.model.HNItem
import com.qburst.hackernews.data.model.Resource
import com.qburst.hackernews.data.repository.items.local.ItemsLocalSource
import com.qburst.hackernews.data.repository.items.remote.ItemsRemoteSource
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class ItemsRepository @Inject constructor(
    private val remoteSource: ItemsRemoteSource,
    private val localSource: ItemsLocalSource
) {


    fun getTopStories() = flow {

        when (val resource = remoteSource.getTopStories()) {

            Resource.None -> {}

            is Resource.Error -> emit(resource)

            is Resource.Success -> {

                emit(
                    Resource.Success(resource.data.associateWith { null })
                )

            }
        }

    }

    suspend fun fetchItems(ids: List<Long>) = flow {

        val list = mutableListOf<HNItem>()

        coroutineScope {

            ids.forEach { itemId ->

                launch {
                    val item = getItemDetails(itemId)

                    item?.let { hnItem ->

                        // Keep a local copy for future use
                        localSource.saveItem(hnItem)

                        list.add(item)
                    }
                }
            }
        }

        if (ids.size == list.size) {
            emit(Resource.Success(list.toList()))
        } else {
            emit(Resource.Error(Throwable("Unable to fetch items at this time")))
        }

    }


    private suspend fun getItemDetails(itemId: Long): HNItem? {

        return when (val res = remoteSource.getItemDetails(itemId = itemId)) {
            is Resource.Error -> null
            Resource.None -> null
            is Resource.Success -> res.data
        }

    }

}