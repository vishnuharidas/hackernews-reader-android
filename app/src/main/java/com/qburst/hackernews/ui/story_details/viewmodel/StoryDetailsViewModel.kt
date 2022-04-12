package com.qburst.hackernews.ui.story_details.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StoryDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val storyId: Long = savedStateHandle.get("storyId") ?: 0L

    init {
        println("SAVED STATE: STORY ID = $storyId")
    }

}