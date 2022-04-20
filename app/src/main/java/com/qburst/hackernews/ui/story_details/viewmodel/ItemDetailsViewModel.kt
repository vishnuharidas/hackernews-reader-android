package com.qburst.hackernews.ui.story_details.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qburst.hackernews.data.model.Resource
import com.qburst.hackernews.data.repository.items.ItemsRepository
import com.qburst.hackernews.domain.GetItemsWithTimeAgoUseCase
import com.qburst.hackernews.domain.GetTimeAgoUseCase
import com.qburst.hackernews.domain.model.HNItemWithTimeAgo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ItemDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val itemsRepository: ItemsRepository
) : ViewModel() {

    private val getTimeAgoUseCase = GetTimeAgoUseCase()
    private val getItemsWithTimeAgoUseCase = GetItemsWithTimeAgoUseCase(repository = itemsRepository)

    private val itemId: Long = savedStateHandle.get("itemId") ?: 0L

    private var _uiState by mutableStateOf(ItemDetailsUiState(state = ItemDetailsUiState.State.Loading))
    val uiState: ItemDetailsUiState get() = _uiState

    init {
        println("SAVED STATE: STORY ID = $itemId")

        fetchItemDetails()

    }

    private fun fetchItemDetails() {

        viewModelScope.launch {

            _uiState = _uiState.copy(state = ItemDetailsUiState.State.Loading)

            val item = itemsRepository.getItemDetails(itemId = itemId)

            _uiState = if (item != null) {
                _uiState.copy(
                    state = ItemDetailsUiState.State.Success,
                    item = HNItemWithTimeAgo(item, getTimeAgoUseCase(item.time)),
                    comments = emptyList()
                )
            } else {
                _uiState.copy(
                    state = ItemDetailsUiState.State.Failure,
                    error = "Unable to get item details now."
                )
            }

        }

    }

    fun fetchNextComments() {

        val item = _uiState.item?.item ?: return

        val fullIds = item.kids ?: emptyList()

        if (fullIds.isNullOrEmpty()) return

        val loadedIds = _uiState.comments?.map { it.item.id } ?: emptyList()

        // Take 10 comments, and then fetch them.
        val nextIds = fullIds - loadedIds

        if (nextIds.isNullOrEmpty()) return

        viewModelScope.launch {

            _uiState = _uiState.copy(
                state = ItemDetailsUiState.State.LoadingMore
            )

            getItemsWithTimeAgoUseCase(nextIds.take(10)).collect {

                if (it is Resource.Success) {

                    _uiState = _uiState.copy(
                        state = ItemDetailsUiState.State.Success,
                        comments = _uiState.comments?.plus(
                            it.data
                        )
                    )

                }
            }


        }

    }

}

data class ItemDetailsUiState(
    val state: State,
    val item: HNItemWithTimeAgo? = null,
    val comments: List<HNItemWithTimeAgo>? = null,
    val error: String? = null
) {
    sealed class State {
        object Loading : State()
        object LoadingMore : State()
        object Success : State()
        object Failure : State()
    }

}

