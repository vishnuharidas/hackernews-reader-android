package com.qburst.hackernews.ui.home.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qburst.hackernews.data.model.HNItem
import com.qburst.hackernews.data.model.Resource
import com.qburst.hackernews.data.repository.stories.StoriesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val storiesRepository: StoriesRepository
) : ViewModel() {

    companion object {
        private const val PAGE_SIZE = 20
    }

    private var _uiState by mutableStateOf(HomeUiState())
    val uiState: HomeUiState get() = _uiState

    private var topStories = LinkedHashMap<Long, HNItem?>()

    init {
        getTopStories()
    }

    fun getTopStories() {

        _uiState = _uiState.copy(state = HomeUiState.State.Loading)

        viewModelScope.launch {

            storiesRepository.getTopStories().collect {

                if (it is Resource.Success) {

                    topStories.putAll(it.data)

                    _uiState = _uiState.copy(totalCount = it.data.size)

                    // Fetch the stories from the IDs
                    fetchNextPage()

                } else if (it is Resource.Error) {

                    _uiState = _uiState.copy(
                        state = HomeUiState.State.Error,
                        totalCount = 0,
                        error = it.throwable
                    )
                }

            }
        }

    }

    fun fetchNextPage() {

        if (_uiState.state == HomeUiState.State.LoadingMore) { // Skip if already loading
            return
        }

        val list = topStories.filter { it.value == null }

        if (list.isEmpty()) return // Nothing to load now

        _uiState = if (list.size == topStories.size) { // If all are NULL, then loading first time
            _uiState.copy(state = HomeUiState.State.Loading)
        } else { // Loading more.
            _uiState.copy(state = HomeUiState.State.LoadingMore)
        }

        viewModelScope.launch {

            storiesRepository.fetchStories(list.keys.take(PAGE_SIZE)).collect { resource ->

                if (resource is Resource.Success) {

                    resource.data.forEach { topStories[it.id] = it }

                    _uiState = _uiState.copy(
                        state = HomeUiState.State.Success,
                        list = topStories.values.filterNotNull()
                    )

                } else if (resource is Resource.Error) {

                    _uiState = _uiState.copy(
                        state = HomeUiState.State.LoadingMoreError,
                        totalCount = 0,
                        error = resource.throwable
                    )
                }


            }

        }

    }

}

data class HomeUiState(
    val state: State = State.None,
    val list: List<HNItem> = emptyList(),
    val totalCount: Int = 0,
    val error: Throwable? = null
) {
    sealed class State {
        object None : State()
        object Loading : State()
        object Success : State()
        object Empty : State()
        object Error : State()
        object LoadingMore : State()
        object LoadingMoreError : State()
    }
}

fun HomeUiState.hasMore(): Boolean {
    return this.state == HomeUiState.State.Success
            && this.totalCount > 0
            && this.list.size < totalCount
}
