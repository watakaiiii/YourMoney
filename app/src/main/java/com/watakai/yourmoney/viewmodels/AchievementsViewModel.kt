package com.watakai.yourmoney.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.watakai.yourmoney.db
import com.watakai.yourmoney.models.Achievement
import io.realm.kotlin.ext.query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AchievementsState(
    val achievements: List<Achievement> = listOf()
)

class AchievementsViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(AchievementsState())
    val uiState: StateFlow<AchievementsState> = _uiState.asStateFlow()

    init {
        _uiState.update { currentState ->
            currentState.copy(
                achievements = db.query<Achievement>().find().sortedByDescending { it.order }
            )
        }
        viewModelScope.launch(Dispatchers.IO) {
            db.query<Achievement>().asFlow().collect { changes ->
                _uiState.update { currentState ->
                    currentState.copy(
                        achievements = changes.list
                    )
                }
            }
        }
    }
}