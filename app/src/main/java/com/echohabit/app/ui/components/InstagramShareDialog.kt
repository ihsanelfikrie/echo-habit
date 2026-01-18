package com.echohabit.app.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.echohabit.app.data.model.Activity
import com.echohabit.app.ui.theme.PrimaryGreen
import com.echohabit.app.util.ShareHelper
import com.echohabit.app.util.showToast

@Composable
fun InstagramShareDialog(
    activity: Activity,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val shareHelper = remember { ShareHelper(context) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(24.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Share to Instagram",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Outlined.Close, "Close")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Info
                Text(
                    text = "Share this activity to your Instagram Story",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF666666)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Share Button
                Button(
                    onClick = {
                        // TODO: Generate card first, then share
                        context.showToast("Generating card for Instagram...")
                        // shareHelper.shareToInstagramStory(bitmap)
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE4405F), // Instagram pink
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Share,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Share to Story",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Alternative: Open Instagram
                TextButton(
                    onClick = {
                        openInstagram(context)
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Open Instagram App")
                }
            }
        }
    }
}

private fun openInstagram(context: Context) {
    try {
        val intent = context.packageManager.getLaunchIntentForPackage("com.instagram.android")
        if (intent != null) {
            context.startActivity(intent)
        } else {
            context.showToast("Instagram not installed")
        }
    } catch (e: Exception) {
        context.showToast("Failed to open Instagram")
    }
}