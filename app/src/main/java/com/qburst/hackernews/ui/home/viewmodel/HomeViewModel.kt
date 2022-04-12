package com.qburst.hackernews.ui.home.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qburst.hackernews.data.model.HNItem
import com.qburst.hackernews.data.model.Resource
import com.qburst.hackernews.data.repository.topstories.TopStoriesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val topStoriesRepository: TopStoriesRepository
) : ViewModel() {

    private var _uiState by mutableStateOf(HomeUiState())
    val uiState: HomeUiState get() = _uiState

    init {

        // Collect the data until this VM scope is destroyed
        viewModelScope.launch {
            topStoriesRepository.topStoriesFlow.collect {
                _uiState = _uiState.from(it)
            }
        }
    }

    fun getTopStories() {

        _uiState = _uiState.copy(state = HomeUiState.State.Loading)

        viewModelScope.launch {
            topStoriesRepository.getTopStories()
        }

    }

    fun nextPage() = viewModelScope.launch {
        topStoriesRepository.nextPage()
    }

}

data class HomeUiState(
    val state: State = State.None,
    val list: List<HNItem> = emptyList(),
    val error: Throwable? = null
) {
    sealed class State {
        object None : State()
        object Loading : State()
        object Success : State()
        object Empty : State()
        object Error : State()
        object LoadingMore : State()
    }
}

private fun HomeUiState.from(resource: Resource<List<HNItem>>): HomeUiState {

    return when (resource) {

        is Resource.Error -> this.copy(state = HomeUiState.State.Error, error = resource.throwable)

        Resource.None -> this.copy(state = HomeUiState.State.None)

        is Resource.Success ->
            if (resource.data.isNullOrEmpty()) {
                this.copy(state = HomeUiState.State.Empty)
            } else {
                this.copy(state = HomeUiState.State.Success, list = resource.data)
            }
    }
}

