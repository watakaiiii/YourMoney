package com.watakai.yourmoney.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.watakai.yourmoney.components.AchievementRow
import com.watakai.yourmoney.ui.theme.TopAppBarBackground
import com.watakai.yourmoney.viewmodels.AchievementsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Achievements(
    navController: NavController,
    vm: AchievementsViewModel = viewModel()
) {
    val state by vm.uiState.collectAsState()
    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = { Text("Достижения") },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = TopAppBarBackground
                )
            )
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp)
                    .fillMaxWidth()
                    .verticalScroll(
                        rememberScrollState()
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                state.achievements.forEach() {
                    AchievementRow(achievement = it)
                }

            }
        }
    )
}