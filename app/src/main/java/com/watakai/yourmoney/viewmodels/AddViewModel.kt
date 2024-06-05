package com.watakai.yourmoney.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.watakai.yourmoney.db
import com.watakai.yourmoney.models.Achievement
import com.watakai.yourmoney.models.Category
import com.watakai.yourmoney.models.Expense
import com.watakai.yourmoney.models.Recurrence
import com.watakai.yourmoney.models.User
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.RealmResults
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime

data class AddScreenState(
  val amount: String = "",
  val expensesCount: Int = 0,
  val recurrence: Recurrence = Recurrence.None,
  val date: LocalDate = LocalDate.now(),
  val note: String = "",
  val category: Category? = null,
  val categories: RealmResults<Category>? = null
)

class AddViewModel : ViewModel() {
  private val _uiState = MutableStateFlow(AddScreenState())
  val uiState: StateFlow<AddScreenState> = _uiState.asStateFlow()

  init {
    _uiState.update { currentState ->
      currentState.copy(
        categories = db.query<Category>().find()
      )
    }
  }

  fun userLevelUp() {
    viewModelScope.launch(Dispatchers.IO) {
      val frozenUser = db.query<User>().find().first()
      db.write {
        findLatest(frozenUser)?.let { liveUser ->
          liveUser.level = liveUser.level + 1
          liveUser.toNextLevel = liveUser.toNextLevel + 5f
        }
      }
    }
  }

  fun unlockAchievement(order: Int) {
    viewModelScope.launch(Dispatchers.IO) {
      val frozenAchievement = db.query<Achievement>("order==$0", order).find().first()
      val frozenUser = db.query<User>().find().first()
      if (!frozenAchievement.isUnlocked) {
        db.write {
          findLatest(frozenAchievement)?.let {
              liveAchievement ->
            liveAchievement.isUnlocked = true
            liveAchievement.progress = liveAchievement.toEnd
          }
        }
        db.write {
          findLatest(frozenUser)?.let{
            liveUser ->
            liveUser.achievements = liveUser.achievements + 1
            liveUser.lastAchievementOrder = frozenAchievement.order
            liveUser.progress = liveUser.progress + 1.5f

            if(liveUser.progress >= liveUser.toNextLevel) {
              userLevelUp()
            }
          }
        }
      }
    }
  }

  fun updateAchievement(order: Int, expensesCount: Int) {
    viewModelScope.launch(Dispatchers.IO) {
      val frozenAchievement = db.query<Achievement>("order == $0", order).find().first()
      if (!frozenAchievement.isUnlocked) {
        db.write {
          findLatest(frozenAchievement)?.let { liveAchievement ->
            liveAchievement.progress = expensesCount.toFloat()
          }
        }
      }

      if (frozenAchievement.toEnd <= expensesCount.toFloat()) {
        unlockAchievement(order)
      }

    }
  }

  fun setAmount(amount: String) {
    var parsed = amount.toDoubleOrNull()

    if (amount.isEmpty()) {
      parsed = 0.0
    }

    if (parsed != null) {
      _uiState.update { currentState ->
        currentState.copy(
          amount = amount.trim().ifEmpty { "0" },
        )
      }
    }
  }

  fun setRecurrence(recurrence: Recurrence) {
    _uiState.update { currentState ->
      currentState.copy(
        recurrence = recurrence,
      )
    }
  }

  fun setDate(date: LocalDate) {
    _uiState.update { currentState ->
      currentState.copy(
        date = date,
      )
    }
  }

  fun setNote(note: String) {
    _uiState.update { currentState ->
      currentState.copy(
        note = note,
      )
    }
  }

  fun setCategory(category: Category) {
    _uiState.update { currentState ->
      currentState.copy(
        category = category,
      )
    }
  }

  fun submitExpense() {
    if (_uiState.value.category != null) {
      viewModelScope.launch(Dispatchers.IO) {
        val now = LocalDateTime.now()
        db.write {
          this.copyToRealm(
            Expense(
              _uiState.value.amount.toDouble(),
              _uiState.value.recurrence,
              _uiState.value.date.atTime(now.hour, now.minute, now.second),
              _uiState.value.note,
              this.query<Category>("_id == $0", _uiState.value.category!!._id)
                .find().first(),
            )
          )
        }

        _uiState.update { currentState ->
          currentState.copy(
            expensesCount = db.query<Expense>().find().size
          )
        }

        updateAchievement(1, _uiState.value.expensesCount)
        updateAchievement(3, _uiState.value.expensesCount)

        _uiState.update { currentState ->
          currentState.copy(
            amount = "",
            recurrence = Recurrence.None,
            date = LocalDate.now(),
            note = "",
          )
        }
      }
    }
  }
}