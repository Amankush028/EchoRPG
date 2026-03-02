package com.echorpg.ui.mystories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.echorpg.data.StoryProgressEntity
import com.echorpg.repository.StoryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MyStoriesViewModel(private val repo: StoryRepository) : ViewModel() {
    val progressList = repo.getAllProgress().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun deleteStory(storyId: String) {
        viewModelScope.launch {
            repo.deleteProgress(storyId)
            repo.deleteMessages(storyId)
        }
    }
}