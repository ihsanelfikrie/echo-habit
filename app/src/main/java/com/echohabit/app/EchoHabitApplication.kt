package com.echohabit.app

import android.app.Application
import android.util.Log
import com.echohabit.app.di.appModule
import com.echohabit.app.di.repositoryModule
import com.echohabit.app.di.viewModelModule
import com.google.firebase.FirebaseApp
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class EchoHabitApplication : Application() {

    companion object {
        private const val TAG = "EchoHabitApp"
    }

    override fun onCreate() {
        super.onCreate()

        // Setup crash handler for debugging
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e(TAG, "UNCAUGHT EXCEPTION in thread ${thread.name}", throwable)
            // Log the full stack trace
            throwable.printStackTrace()
        }

        try {
            Log.d(TAG, "=== Application Starting ===")

            // Initialize Firebase
            try {
                FirebaseApp.initializeApp(this)
                Log.d(TAG, "✓ Firebase initialized")
            } catch (e: Exception) {
                Log.e(TAG, "✗ Firebase init failed (non-critical)", e)
            }

            // Initialize Koin for Dependency Injection
            Log.d(TAG, "Starting Koin initialization...")
            startKoin {
                androidLogger(Level.DEBUG) // Change to DEBUG to see DI issues
                androidContext(this@EchoHabitApplication)
                modules(
                    appModule,
                    repositoryModule,
                    viewModelModule
                )
            }
            Log.d(TAG, "✓ Koin initialized")

            Log.d(TAG, "=== Application Started Successfully ===")
        } catch (e: Exception) {
            Log.e(TAG, "✗ CRITICAL ERROR in Application.onCreate", e)
            throw e
        }
    }
}