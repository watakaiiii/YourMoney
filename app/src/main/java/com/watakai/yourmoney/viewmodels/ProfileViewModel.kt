package com.watakai.yourmoney.viewmodels

import androidx.lifecycle.ViewModel
import com.watakai.yourmoney.db
import com.watakai.yourmoney.models.Achievement
import com.watakai.yourmoney.models.User
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.*

data class ProfileScreenState(
    val user: User = User(),
    val username: String = "Пользователь",
    val level: Int = 1,
    val progress: Float = 0f,
    val toTheNextLevel: Float = 5f,
    val achievements: Int = 0,
    val lastAchievementOrder: Int = -1,
    val lastAchievement: Achievement = Achievement()
)

class ProfileViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileScreenState())
    val uiState: StateFlow<ProfileScreenState> = _uiState.asStateFlow()

    init {
        if (db.query<User>().find().size != 0) {
            _uiState.update { currentState ->
                currentState.copy(
                    user = db.query<User>().find().first(),
                )
            }
            _uiState.update { currentState ->
                currentState.copy(
                    lastAchievementOrder = _uiState.value.user.lastAchievementOrder
                )
            }
            if (_uiState.value.user.lastAchievementOrder != -1 ) {
                _uiState.update { currentState ->
                    currentState.copy(
                        lastAchievement = db.query<Achievement>("order == $0", _uiState.value.user.lastAchievementOrder).find().first()
                    )
                }
            }
        }

    }
}