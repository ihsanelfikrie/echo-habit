package com.echohabit.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.echohabit.app.data.model.Activity
import com.echohabit.app.ui.theme.ElectricLime
import com.echohabit.app.util.CO2Calculator
import com.echohabit.app.util.toCO2String

@Composable
fun ImpactCard(
    activity: Activity,
    modifier: Modifier = Modifier
) {
    val co2Calculator = CO2Calculator()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(400.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background Photo
            AsyncImage(
                model = activity.photoUrl,
                contentDescription = "Activity Photo",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Gradient Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(24.dp))
            )

            // Stats Overlay
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White.copy(alpha = 0.25f),
                    modifier = Modifier.padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // CO2 Saved
                        Text(
                            text = "+${activity.co2SavedKg.toCO2String()} kg CO₂",
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold,
                            color = ElectricLime
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Activity Type
                        Text(
                            text = "${co2Calculator.getActivityEmoji(activity.activityType)} ${co2Calculator.getActivityDisplayName(activity.activityType)}",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White
                        )

                        // Caption if exists
                        if (activity.caption.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = activity.caption,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }
                }
            }
        }
    }
}