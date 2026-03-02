package com.echorpg.ui.character

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.echorpg.data.Girl
import com.echorpg.repository.GirlRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class CharactersViewModel(private val repo: GirlRepository) : ViewModel() {
    val unlockedGirls = repo.getAllUnlockedGirls()
        .map { list ->
            list.map { Girl(it.id, it.name, it.fromStory, it.status, it.relationshipLevel) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
}