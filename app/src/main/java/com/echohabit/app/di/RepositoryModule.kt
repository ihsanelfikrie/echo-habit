package com.echohabit.app.di

import com.echohabit.app.data.repository.ActivityRepository
import com.echohabit.app.data.repository.AuthRepository
import com.echohabit.app.data.repository.LocalActivityRepository
import com.echohabit.app.data.repository.PhotoRepository
import com.echohabit.app.data.repository.StatsRepository
import com.echohabit.app.data.repository.UserRepository
import com.echohabit.app.domain.usecase.CalculateCO2UseCase
import com.echohabit.app.domain.usecase.CalculateStreakUseCase
import com.echohabit.app.domain.usecase.GenerateCardUseCase
import com.echohabit.app.domain.usecase.ShareCardUseCase
import com.echohabit.app.domain.usecase.UploadActivityUseCase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {

    // Repositories
    single { AuthRepository(get(), get()) }
    single { UserRepository(get()) }
    single { ActivityRepository(get()) }
    single { PhotoRepository(get()) }
    single { StatsRepository(get()) }

    // LOCAL REPOSITORY - ADD THIS!
    single {
        LocalActivityRepository(
            context = androidContext(),
            activityDao = get(),
            userDao = get(),
            statsDao = get()
        )
    }

    // Use Cases
    factory { UploadActivityUseCase(get(), get()) }
    factory { GenerateCardUseCase(get()) }
    factory { CalculateStreakUseCase(get()) }
    factory { CalculateCO2UseCase(get()) }
    factory { ShareCardUseCase(get()) }
}