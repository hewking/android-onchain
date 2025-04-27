package com.cryptocom.wallet.data.repository

import com.cryptocom.wallet.data.datasource.local.LocalAssetDataSource
import com.cryptocom.wallet.data.mapper.toDomain
import com.cryptocom.wallet.data.model.BalancesResponseDto
import com.cryptocom.wallet.data.model.CurrenciesResponseDto
import com.cryptocom.wallet.data.model.RatesResponseDto
import com.cryptocom.wallet.domain.common.Result
import com.cryptocom.wallet.domain.model.Currency
import com.cryptocom.wallet.domain.model.ExchangeRate
import com.cryptocom.wallet.domain.model.WalletBalance
import com.cryptocom.wallet.domain.repository.CurrencyRepository
import com.cryptocom.wallet.domain.repository.RateRepository
import com.cryptocom.wallet.domain.repository.WalletRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Named

// Helper function for safe JSON parsing within a Flow
suspend inline fun <reified T> safeJsonParse(jsonString: String, json: Json): T {
    return json.decodeFromString<T>(jsonString)
}

// Base class or helper for common repository logic (optional)

// --- CurrencyRepository Implementation --- //
class CurrencyRepositoryImpl @Inject constructor(
    private val localDataSource: LocalAssetDataSource,
    private val json: Json, // Inject Json parser
    @Named("IODispatcher") private val ioDispatcher: CoroutineDispatcher // Inject IO Dispatcher
) : CurrencyRepository {

    override fun getSupportedCurrencies(): Flow<Result<List<Currency>>> = flow {
        emit(Result.Loading) // Emit loading state
        try {
            val jsonString = localDataSource.getCurrenciesJsonString()
            val responseDto = safeJsonParse<CurrenciesResponseDto>(jsonString, json)
            val domainList = responseDto.currencies.toDomain()
            emit(Result.Success(domainList))
        } catch (e: Exception) {
            // Catch parsing or IO errors
            emit(Result.Error(e))
        }
    }.flowOn(ioDispatcher) // Ensure operations run on the IO dispatcher
}

// --- RateRepository Implementation --- //
class RateRepositoryImpl @Inject constructor(
    private val localDataSource: LocalAssetDataSource,
    private val json: Json,
    @Named("IODispatcher") private val ioDispatcher: CoroutineDispatcher
) : RateRepository {

    override fun getExchangeRates(): Flow<Result<List<ExchangeRate>>> = flow {
        emit(Result.Loading)
        try {
            val jsonString = localDataSource.getRatesJsonString()
            val responseDto = safeJsonParse<RatesResponseDto>(jsonString, json)
            val domainList = responseDto.tiers.toDomain()
            emit(Result.Success(domainList))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }.flowOn(ioDispatcher)
}

// --- WalletRepository Implementation --- //
class WalletRepositoryImpl @Inject constructor(
    private val localDataSource: LocalAssetDataSource,
    private val json: Json,
    @Named("IODispatcher") private val ioDispatcher: CoroutineDispatcher
) : WalletRepository {

    override fun getWalletBalances(): Flow<Result<List<WalletBalance>>> = flow {
        emit(Result.Loading)
        try {
            val jsonString = localDataSource.getBalancesJsonString()
            val responseDto = safeJsonParse<BalancesResponseDto>(jsonString, json)
            val domainList = responseDto.balances.toDomain()
            emit(Result.Success(domainList))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }.flowOn(ioDispatcher)
} 