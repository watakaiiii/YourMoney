package com.watakai.yourmoney.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.marosseleng.compose.material3.datetimepickers.date.ui.dialog.DatePickerDialog
import com.watakai.yourmoney.components.TableRow
import com.watakai.yourmoney.components.UnstyledTextField
import com.watakai.yourmoney.models.Recurrence
import com.watakai.yourmoney.ui.theme.*
import com.watakai.yourmoney.viewmodels.AddViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun Add(navController: NavController, vm: AddViewModel = viewModel()) {
  val state by vm.uiState.collectAsState()

  val recurrences = listOf(
    Recurrence.None,
    Recurrence.Daily,
    Recurrence.Weekly,
    Recurrence.Monthly,
    Recurrence.Yearly
  )

  Scaffold(topBar = {
    MediumTopAppBar(
      title = { Text("Добавить расход") },
      colors = TopAppBarDefaults.mediumTopAppBarColors(
        containerColor = TopAppBarBackground
      )
    )
  }, content = { innerPadding ->
    Column(
      modifier = Modifier.padding(innerPadding),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Column(
        modifier = Modifier
          .padding(16.dp)
          .clip(Shapes.large)
          .background(BackgroundElevated)
          .fillMaxWidth()
      ) {
        TableRow(label = "Сумма", detailContent = {
          UnstyledTextField(
            value = state.amount,
            onValueChange = vm::setAmount,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("0") },
            arrangement = Arrangement.End,
            maxLines = 1,
            textStyle = TextStyle(
              textAlign = TextAlign.Right,
            ),
            keyboardOptions = KeyboardOptions(
              keyboardType = KeyboardType.Number,
            )
          )
        })
        Divider(
          modifier = Modifier.padding(start = 16.dp),
          thickness = 1.dp,
          color = DividerColor
        )
        TableRow(label = "Повторяемость", detailContent = {
          var recurrenceMenuOpened by remember {
            mutableStateOf(false)
          }
          TextButton(
            onClick = { recurrenceMenuOpened = true }, shape = Shapes.large
          ) {
            Text(state.recurrence?.name ?: Recurrence.None.name)
            DropdownMenu(expanded = recurrenceMenuOpened,
              onDismissRequest = { recurrenceMenuOpened = false }) {
              recurrences.forEach { recurrence ->
                DropdownMenuItem(text = { Text(recurrence.name) }, onClick = {
                  vm.setRecurrence(recurrence)
                  recurrenceMenuOpened = false
                })
              }
            }
          }
        })
        Divider(
          modifier = Modifier.padding(start = 16.dp),
          thickness = 1.dp,
          color = DividerColor
        )
        var datePickerShowing by remember {
          mutableStateOf(false)
        }
        TableRow(label = "Дата", detailContent = {
          TextButton(onClick = { datePickerShowing = true }) {
            Text(state.date.toString())
          }
          if (datePickerShowing) {
            DatePickerDialog(onDismissRequest = { datePickerShowing = false },
              onDateChange = { it ->
                vm.setDate(it)
                datePickerShowing = false
              },
              initialDate = state.date,
              title = { Text("Выберите дату", style = Typography.titleLarge) })
          }
        })
        Divider(
          modifier = Modifier.padding(start = 16.dp),
          thickness = 1.dp,
          color = DividerColor
        )
        TableRow(label = "Заметка", detailContent = {
          UnstyledTextField(
            value = state.note,
            placeholder = { Text("Оставьте заметку") },
            arrangement = Arrangement.End,
            onValueChange = vm::setNote,
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(
              textAlign = TextAlign.Right,
            ),
          )
        })
        Divider(
          modifier = Modifier.padding(start = 16.dp),
          thickness = 1.dp,
          color = DividerColor
        )
        TableRow(label = "Категория", detailContent = {
          var categoriesMenuOpened by remember {
            mutableStateOf(false)
          }
          TextButton(
            onClick = { categoriesMenuOpened = true }, shape = Shapes.large
          ) {
            Text(
              state.category?.name ?: "Выберите категорию",
              color = state.category?.color ?: Color.White
            )
            DropdownMenu(expanded = categoriesMenuOpened,
              onDismissRequest = { categoriesMenuOpened = false }) {
              state.categories?.forEach { category ->
                DropdownMenuItem(text = {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                      modifier = Modifier.size(10.dp),
                      shape = CircleShape,
                      color = category.color
                    ) {}
                    Text(
                      category.name, modifier = Modifier.padding(start = 8.dp)
                    )
                  }
                }, onClick = {
                  vm.setCategory(category)
                  categoriesMenuOpened = false
                })
              }
            }
          }
        })
      }
      Button(
        onClick = vm::submitExpense,
        modifier = Modifier.padding(16.dp),
        shape = Shapes.large,
        enabled = state.category != null
      ) {
        Text("Добавить расход")
      }
    }
  })
}
