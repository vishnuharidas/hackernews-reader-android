package com.qburst.hackernews.ui.story_details.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qburst.hackernews.data.model.HNItem
import com.qburst.hackernews.data.repository.items.ItemsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ItemDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val itemsRepository: ItemsRepository
) : ViewModel() {

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
                    state = ItemDetailsUiState.State.Success(item)
                )
            } else {
                _uiState.copy(
                    state = ItemDetailsUiState.State.Failure("Unable to get item details now.")
                )
            }

        }

    }

}

data class ItemDetailsUiState(
    val state: State,
    val comments: List<HNItem>? = null,
    val error: String? = null
) {
    sealed class State {
        object Loading : State()
        class Success(val item: HNItem) : State()
        class Failure(val error: String) : State()
    }

}

