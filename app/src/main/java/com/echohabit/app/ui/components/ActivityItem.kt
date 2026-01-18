package com.echohabit.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.echohabit.app.data.model.Activity
import com.echohabit.app.util.CO2Calculator
import com.echohabit.app.util.DateUtils
import com.echohabit.app.util.toCO2String

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityItem(
    activity: Activity,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val co2Calculator = CO2Calculator()

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        onClick = { onClick?.invoke() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Photo Thumbnail
            AsyncImage(
                model = activity.photoUrl,
                contentDescription = "Activity",
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            // Activity Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "${co2Calculator.getActivityEmoji(activity.activityType)} ${co2Calculator.getActivityDisplayName(activity.activityType)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = DateUtils.getRelativeTimeString(activity.createdAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // CO2 Badge
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text = "+${activity.co2SavedKg.toCO2String()}",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}