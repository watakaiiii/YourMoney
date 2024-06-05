package com.watakai.yourmoney.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.twotone.Star
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.watakai.yourmoney.models.Achievement
import com.watakai.yourmoney.ui.theme.Typography

@Composable
fun AchievementRow(achievement: Achievement, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                achievement.name,
                style = Typography.titleSmall
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 32.dp)
        ) {
            if (achievement.isUnlocked) {
                Icon(Icons.Filled.Star, contentDescription = "Some Icon", modifier = Modifier.size(100.dp))
            }
            else {
                Icon(Icons.TwoTone.Star, contentDescription = "Some Icon", modifier = Modifier.size(100.dp))
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 32.dp)
        ) {
            Text(
                achievement.description,
                style = Typography.bodyMedium
            )
        }
        Row (verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 32.dp)) {
            LinearProgressIndicator(
                progress = (achievement.progress / achievement.toEnd),
                color = Color.Green,
                modifier = Modifier
                    .height(8.dp)
                    .clip(RoundedCornerShape(16.dp)),
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 32.dp)) {
            Text("${(achievement.progress).toInt()}/${(achievement.toEnd).toInt()}")
        }
        Divider(modifier = Modifier.padding(top = 32.dp, bottom = 4.dp))
    }
}