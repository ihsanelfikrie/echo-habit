package com.echohabit.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.echohabit.app.ui.theme.DeepPurple
import com.echohabit.app.util.Constants

@Composable
fun LevelIndicator(
    level: Int,
    modifier: Modifier = Modifier
) {
    val levelData = Constants.LEVELS.getOrNull(level - 1) ?: Constants.LEVELS.first()

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = DeepPurple.copy(alpha = 0.15f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = levelData.emoji,
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = "Level $level",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = DeepPurple
            )
        }
    }
}