package com.echorpg.ui.chapterselect

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.echorpg.data.StoryProgressEntity
import com.echorpg.repository.StoryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class ChapterSelectViewModel(
    private val repository: StoryRepository,
    private val storyTitle: String
) : ViewModel() {

    val progress = repository.getProgress(storyTitle.lowercase().replace(" ", "_"))
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)
}