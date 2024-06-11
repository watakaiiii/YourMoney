package com.watakai.yourmoney.viewmodels

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.watakai.yourmoney.db
import com.watakai.yourmoney.models.Achievement
import com.watakai.yourmoney.models.Category
import com.watakai.yourmoney.models.User
import io.realm.kotlin.ext.query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CategoriesState(
  val newCategoryColor: Color = Color.White,
  val newCategoryName: String = "",
  val colorPickerShowing: Boolean = false,
  val categoriesCount: Int = 0,
  val categoiesNames: MutableList<String> = mutableListOf(),
  val categories: List<Category> = listOf()
)

class CategoriesViewModel : ViewModel() {
  private val _uiState = MutableStateFlow(CategoriesState())
  val uiState: StateFlow<CategoriesState> = _uiState.asStateFlow()

  init {
    _uiState.update { currentState ->
      currentState.copy(
        categories = db.query<Category>().find()
      )
    }

    viewModelScope.launch(Dispatchers.IO) {
      db.query<Category>().asFlow().collect { changes ->
        _uiState.update { currentState ->
          currentState.copy(
            categories = changes.list
          )
        }
      }
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
      val frozenAchievement = db.query<Achievement>("order == $0", order).find().first()
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
            liveUser.lastAchievement = frozenAchievement
            liveUser.progress = liveUser.progress + 1.5f

            if(liveUser.progress >= liveUser.toNextLevel) {
              userLevelUp()
            }
          }
        }
      }
    }
  }

  fun updateAchievement(order: Int, categoriesCount: Int) {
    viewModelScope.launch(Dispatchers.IO) {
      val frozenAchievement = db.query<Achievement>("order == $0", order).find().first()
      if (!frozenAchievement.isUnlocked) {
        db.write {
          findLatest(frozenAchievement)?.let { liveAchievement ->
            liveAchievement.progress = categoriesCount.toFloat()
          }
        }
      }

      if (frozenAchievement.toEnd <= categoriesCount.toFloat()) {
        unlockAchievement(order)
      }

    }
  }

  fun setNewCategoryColor(color: Color) {
    _uiState.update { currentState ->
      currentState.copy(
        newCategoryColor = color
      )
    }
  }

  fun setNewCategoryName(name: String) {
    _uiState.update { currentState ->
      currentState.copy(
        newCategoryName = name
      )
    }
  }

  fun showColorPicker() {
    _uiState.update { currentState ->
      currentState.copy(
        colorPickerShowing = true
      )
    }
  }

  fun hideColorPicker() {
    _uiState.update { currentState ->
      currentState.copy(
        colorPickerShowing = false
      )
    }
  }

  fun createNewCategory() {
    viewModelScope.launch(Dispatchers.IO) {
      val isDuplicate = _uiState.value.newCategoryName in _uiState.value.categoiesNames
      if ((_uiState.value.newCategoryName != "") and !(isDuplicate)) {
        _uiState.value.categoiesNames.add(_uiState.value.newCategoryName)
        db.write {
          this.copyToRealm(Category(
            _uiState.value.newCategoryName,
            _uiState.value.newCategoryColor
          ))
        }

        _uiState.update { currentState ->
          currentState.copy(
            categoriesCount = db.query<Category>().find().size
          )
        }


        updateAchievement(0, _uiState.value.categoriesCount)
        updateAchievement(2, _uiState.value.categoriesCount)

        _uiState.update { currentState ->
          currentState.copy(
            newCategoryColor = Color.White,
            newCategoryName = ""
          )
        }
      }

    }
  }

  fun deleteCategory(category: Category) {
    viewModelScope.launch(Dispatchers.IO) {
      db.write {
        val deletingCategory = this.query<Category>("_id == $0", category._id).find().first()
        delete(deletingCategory)
      }
    }
  }
}