package com.watakai.yourmoney.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.watakai.yourmoney.db
import com.watakai.yourmoney.models.Achievement
import com.watakai.yourmoney.models.Expense
import com.watakai.yourmoney.models.Recurrence
import com.watakai.yourmoney.models.User
import com.watakai.yourmoney.utils.calculateDateRange
import io.realm.kotlin.ext.query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ExpensesState(
  val recurrence: Recurrence = Recurrence.Daily,
  val sumTotal: Double = 1250.98,
  val expenses: List<Expense> = listOf()
)

class ExpensesViewModel: ViewModel() {
  private val _uiState = MutableStateFlow(ExpensesState())
  val uiState: StateFlow<ExpensesState> = _uiState.asStateFlow()

  init {

    if(db.query<Achievement>().find().size == 0){
      createAchievement(order = 0, "Начало начал", "Добавьте первую категорию", 0f, 1f)
      createAchievement(order = 1, "Первые шаги", "Добавьте первый расход", 0f, 1f)
      createAchievement( order = 2, "Разнообразие", "Добавьте 3 категории", 0f, 3f)
      createAchievement(order = 3, "Первые успехи", "Добавьте 5 расходов", 0f, 5f)
    }

    _uiState.update { currentState ->
      currentState.copy(
        expenses = db.query<Expense>().find()
      )
    }
    viewModelScope.launch(Dispatchers.IO) {
      setRecurrence(Recurrence.Daily)
    }

    viewModelScope.launch(Dispatchers.IO) {
      if (db.query<User>().find().size == 0) {
        db.write {
          this.copyToRealm(
            User(
              User().username,
              User().level,
              User().progress,
              User().toNextLevel,
              User().achievements,
              User().lastAchievementOrder,
              _uiState.value.expenses
            )
          )
        }
      }
    }
  }

  fun createAchievement(order: Int, name: String, description: String, progress: Float, toEnd: Float){
    viewModelScope.launch(Dispatchers.IO) {
      db.write {
        this.copyToRealm(
          Achievement(
            order,
            name,
            description,
            progress,
            toEnd,
            false
          )
        )
      }
    }
  }

  fun setRecurrence(recurrence: Recurrence) {
    val (start, end) = calculateDateRange(recurrence, 0)

    val filteredExpenses = db.query<Expense>().find().filter { expense ->
      (expense.date.toLocalDate().isAfter(start) && expense.date.toLocalDate()
        .isBefore(end)) || expense.date.toLocalDate()
        .isEqual(start) || expense.date.toLocalDate().isEqual(end)
    }

    val sumTotal = filteredExpenses.sumOf { it.amount }

    _uiState.update { currentState ->
      currentState.copy(
        recurrence = recurrence,
        expenses = filteredExpenses,
        sumTotal = sumTotal
      )
    }
  }
}