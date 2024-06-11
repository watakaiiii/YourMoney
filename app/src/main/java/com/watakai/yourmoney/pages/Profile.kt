package com.watakai.yourmoney.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.twotone.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.watakai.yourmoney.ui.theme.TopAppBarBackground
import com.watakai.yourmoney.ui.theme.Typography
import com.watakai.yourmoney.viewmodels.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Profile(
    navController: NavController,
    vm: ProfileViewModel = viewModel()
) {
    val state by vm.uiState.collectAsState()
    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = { Text("Профиль") },
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
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "${state.user.username}",
                        style = Typography.titleLarge,
                    )
                }
                Row(modifier = Modifier.padding(vertical = 32.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Rounded.AccountCircle,
                        contentDescription = "Profile Picture",
                        modifier = Modifier.size(100.dp)
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Уровень ${state.user.level}",
                        style = Typography.titleSmall,
                    )
                }
                Row(modifier = Modifier.padding(vertical = 32.dp), verticalAlignment = Alignment.CenterVertically) {
                    LinearProgressIndicator(
                        progress = (state.user.progress)/(state.user.toNextLevel),
                        color = Green,
                        modifier = Modifier
                            .height(8.dp)
                            .clip(RoundedCornerShape(16.dp)),
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("${(state.user.progress * 100).toInt()}/${(state.user.toNextLevel * 100).toInt()}")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Достижения: ${state.user.achievements}")
                }
                Row(modifier = Modifier.padding(vertical = 32.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("Последнее достижение",
                        style = Typography.titleSmall)
                }
                if (state.lastAchievement == null) {

                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            state.lastAchievement!!.name,
                            style = Typography.titleSmall
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 32.dp)
                    ) {
                        if (state.lastAchievement!!.isUnlocked) {
                            Icon(Icons.Filled.Star, contentDescription = "Some Icon", modifier = Modifier.size(100.dp))
                        }
                        else {
                            Icon(Icons.TwoTone.Star, contentDescription = "Some Icon", modifier = Modifier.size(100.dp))
                        }
                    }
                }
            }
        }
    )
}