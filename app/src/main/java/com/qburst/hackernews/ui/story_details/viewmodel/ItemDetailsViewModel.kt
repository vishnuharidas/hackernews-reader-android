package com.qburst.hackernews.ui.story_details.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ItemDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val itemId: Long = savedStateHandle.get("itemId") ?: 0L

    init {
        println("SAVED STATE: STORY ID = $itemId")
    }

}