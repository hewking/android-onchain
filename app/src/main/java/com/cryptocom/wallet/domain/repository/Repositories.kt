package com.cryptocom.wallet.domain.repository

import com.cryptocom.wallet.domain.common.Result
import com.cryptocom.wallet.domain.model.Currency
import com.cryptocom.wallet.domain.model.ExchangeRate
import com.cryptocom.wallet.domain.model.WalletBalance
import kotlinx.coroutines.flow.Flow

/**
 * Interface for accessing supported currency data.
 */
interface CurrencyRepository {
    /**
     * Retrieves a list of all supported cryptocurrencies.
     * Returns a Flow emitting the result (Success or Error).
     */
    fun getSupportedCurrencies(): Flow<Result<List<Currency>>>
}

/**
 * Interface for accessing exchange rate data.
 */
interface RateRepository {
    /**
     * Retrieves a list of exchange rates, typically to USD.
     * Returns a Flow emitting the result (Success or Error).
     */
    fun getExchangeRates(): Flow<Result<List<ExchangeRate>>>
}

/**
 * Interface for accessing user wallet balance data.
 */
interface WalletRepository {
    /**
     * Retrieves the user's current wallet balances for various currencies.
     * Returns a Flow emitting the result (Success or Error).
     */
    fun getWalletBalances(): Flow<Result<List<WalletBalance>>>
} 