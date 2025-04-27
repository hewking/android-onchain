package com.cryptocom.wallet.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context {
        return context
    }

    @Provides
    @Singleton
    fun provideJsonParser(): Json {
        return Json { ignoreUnknownKeys = true } // Configure as needed
    }

    @Provides
    @Singleton
    @Named("IODispatcher")
    fun provideIODispatcher(): CoroutineDispatcher {
        return Dispatchers.IO
    }

    @Provides
    @Singleton
    @Named("DefaultDispatcher")
    fun provideDefaultDispatcher(): CoroutineDispatcher {
        return Dispatchers.Default // For CPU-intensive work like sorting/filtering large lists
    }

    @Provides
    @Singleton
    @Named("MainDispatcher")
    fun provideMainDispatcher(): CoroutineDispatcher {
        return Dispatchers.Main // For UI updates
    }
} 