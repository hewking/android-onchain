package com.cryptocom.wallet.di

import com.cryptocom.wallet.data.datasource.local.LocalAssetDataSource
import com.cryptocom.wallet.data.datasource.local.LocalAssetDataSourceImpl
import com.cryptocom.wallet.data.repository.CurrencyRepositoryImpl
import com.cryptocom.wallet.data.repository.RateRepositoryImpl
import com.cryptocom.wallet.data.repository.WalletRepositoryImpl
import com.cryptocom.wallet.domain.repository.CurrencyRepository
import com.cryptocom.wallet.domain.repository.RateRepository
import com.cryptocom.wallet.domain.repository.WalletRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // Install bindings in the SingletonComponent
abstract class DataModule {

    @Binds
    @Singleton // Scope the binding to Singleton
    abstract fun bindLocalAssetDataSource(impl: LocalAssetDataSourceImpl): LocalAssetDataSource

    @Binds
    @Singleton // Scope the binding to Singleton
    abstract fun bindCurrencyRepository(impl: CurrencyRepositoryImpl): CurrencyRepository

    @Binds
    @Singleton // Scope the binding to Singleton
    abstract fun bindRateRepository(impl: RateRepositoryImpl): RateRepository

    @Binds
    @Singleton // Scope the binding to Singleton
    abstract fun bindWalletRepository(impl: WalletRepositoryImpl): WalletRepository

} 