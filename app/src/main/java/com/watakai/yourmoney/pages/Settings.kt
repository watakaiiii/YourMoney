package com.watakai.yourmoney.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.watakai.yourmoney.components.TableRow
import com.watakai.yourmoney.db
import com.watakai.yourmoney.models.Achievement
import com.watakai.yourmoney.models.Category
import com.watakai.yourmoney.models.Expense
import com.watakai.yourmoney.models.User
import com.watakai.yourmoney.ui.theme.BackgroundElevated
import com.watakai.yourmoney.ui.theme.DividerColor
import com.watakai.yourmoney.ui.theme.Shapes
import com.watakai.yourmoney.ui.theme.TopAppBarBackground
import io.realm.kotlin.ext.query
import kotlinx.coroutines.launch



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Settings(navController: NavController) {
  val coroutineScope = rememberCoroutineScope()
  var deleteConfirmationShowing by remember {
    mutableStateOf(false)
  }

  val eraseAllData: () -> Unit = {
    coroutineScope.launch {
      db.write {
        val expenses = this.query<Expense>().find()
        val categories = this.query<Category>().find()
        val achievements = this.query<Achievement>().find()
        val user = this.query<User>().find().first()

        delete(expenses)
        delete(categories)

        achievements.forEach() {
          achievement ->
          val frozenAchievement = db.query<Achievement>("order == $0", achievement.order).find().first()
          findLatest(frozenAchievement)?.let {
              liveAchievement ->
            liveAchievement.isUnlocked = false
            liveAchievement.progress = 0f
          }
        }

        val frozenUser = user
        findLatest(frozenUser)?.let{
          liveUser ->
          liveUser.level = 1
          liveUser.progress = 0f
          liveUser.achievements = 0
          liveUser.toNextLevel = 5f
          liveUser.lastAchievementOrder = -1
        }

      }
      deleteConfirmationShowing = false
    }
  }

  Scaffold(
    topBar = {
      MediumTopAppBar(
        title = { Text("Настройки") },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
          containerColor = TopAppBarBackground
        )
      )
    },
    content = { innerPadding ->
      Column(modifier = Modifier.padding(innerPadding)) {
        Column(
          modifier = Modifier
            .padding(16.dp)
            .clip(Shapes.large)
            .background(BackgroundElevated)
            .fillMaxWidth()
        ) {
          TableRow(
                  label = "Профиль",
          hasArrow = true,
          modifier = Modifier.clickable {
            navController.navigate("settings/profile")
          })
          Divider(
            modifier = Modifier
              .padding(start = 16.dp), thickness = 1.dp, color = DividerColor
          )
          TableRow(
            label = "Достижения",
            hasArrow = true,
            modifier = Modifier.clickable {
              navController.navigate("settings/achievements")
            })
          Divider(
            modifier = Modifier
              .padding(start = 16.dp), thickness = 1.dp, color = DividerColor
          )
          TableRow(
            label = "Категории",
            hasArrow = true,
            modifier = Modifier.clickable {
              navController.navigate("settings/categories")
            })
          Divider(
            modifier = Modifier
              .padding(start = 16.dp), thickness = 1.dp, color = DividerColor
          )
          TableRow(
            label = "Удалить все данные",
            isDestructive = true,
            modifier = Modifier.clickable {
              deleteConfirmationShowing = true
            })

          if (deleteConfirmationShowing) {
            AlertDialog(
              onDismissRequest = { deleteConfirmationShowing = false },
              title = { Text("Вы уверены?") },
              text = { Text("Это действие необратимо.") },
              confirmButton = {
                TextButton(onClick = eraseAllData) {
                  Text("Удалить все данные")
                }
              },
              dismissButton = {
                TextButton(onClick = { deleteConfirmationShowing = false }) {
                  Text("Отмена")
                }
              }
            )
          }
        }
      }
    }
  )
}