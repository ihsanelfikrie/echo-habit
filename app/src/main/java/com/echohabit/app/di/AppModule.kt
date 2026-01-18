package com.echohabit.app.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.echohabit.app.data.local.AppDatabase
import com.echohabit.app.util.CO2Calculator
import com.echohabit.app.util.CardGenerator
import com.echohabit.app.util.ShareHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

// DataStore extension
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "echo_habit_prefs")

val appModule = module {

    // Firebase instances
    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }
    single { FirebaseStorage.getInstance() }

    // Room Database - LOCAL STORAGE!
    single { AppDatabase.getInstance(androidContext()) }
    single { get<AppDatabase>().activityDao() }
    single { get<AppDatabase>().userDao() }
    single { get<AppDatabase>().statsDao() }

    // DataStore
    single { androidContext().dataStore }

    // Utility classes
    single { CO2Calculator() }
    single { CardGenerator(androidContext()) }
    single { ShareHelper(androidContext()) }
}