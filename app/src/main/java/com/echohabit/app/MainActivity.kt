package com.echohabit.app

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.echohabit.app.ui.EchoHabitNavigation
import com.echohabit.app.ui.theme.EchoHabitTheme

class MainActivity : ComponentActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "=== MainActivity onCreate ===")

        try {
            // Enable edge-to-edge
            WindowCompat.setDecorFitsSystemWindows(window, false)
            Log.d(TAG, "✅ Edge-to-edge enabled")

            setContent {
                Log.d(TAG, "✅ setContent called")

                EchoHabitTheme {
                    Log.d(TAG, "✅ Theme applied")

                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        Log.d(TAG, "✅ Starting Navigation")
                        EchoHabitNavigation()
                    }
                }
            }

            Log.d(TAG, "=== MainActivity onCreate COMPLETE ===")
        } catch (e: Exception) {
            Log.e(TAG, "✗ CRASH in MainActivity.onCreate", e)
            e.printStackTrace()
            throw e
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "MainActivity onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "MainActivity onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "MainActivity onPause")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "MainActivity onDestroy")
    }
}