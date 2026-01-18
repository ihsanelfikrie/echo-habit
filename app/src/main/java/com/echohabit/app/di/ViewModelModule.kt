package com.echohabit.app.di

import com.echohabit.app.ui.screens.auth.AuthViewModel
import com.echohabit.app.ui.screens.cardgen.CardViewModel
import com.echohabit.app.ui.screens.home.HomeViewModel
import com.echohabit.app.ui.screens.profile.ProfileViewModel
import com.echohabit.app.ui.screens.splash.SplashViewModel
import com.echohabit.app.ui.screens.stats.StatsViewModel
import com.echohabit.app.ui.screens.upload.UploadViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    // ViewModels
    viewModel { SplashViewModel(get()) }
    viewModel { AuthViewModel(get()) }

    // HomeViewModel - USE LOCAL REPOSITORY!
    viewModel { HomeViewModel(get(), get(), get()) }

    // UploadViewModel - USE LOCAL REPOSITORY!
    viewModel { UploadViewModel(get(), get(), get()) }

    viewModel { CardViewModel(get(), get(), get()) }
    viewModel { ProfileViewModel(get(), get()) }

    // ✅ StatsViewModel - USE LOCAL REPOSITORY!
    viewModel { StatsViewModel(get(), get()) } // authRepository, localActivityRepository
}