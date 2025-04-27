package com.cryptocom.wallet.domain.usecase

import com.cryptocom.wallet.domain.common.Result
import com.cryptocom.wallet.domain.model.*
import com.cryptocom.wallet.domain.repository.CurrencyRepository
import com.cryptocom.wallet.domain.repository.RateRepository
import com.cryptocom.wallet.domain.repository.WalletRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject

class GetDashboardDataUseCase @Inject constructor(
    private val currencyRepository: CurrencyRepository,
    private val rateRepository: RateRepository,
    private val walletRepository: WalletRepository
) {

    // Combine the flows from the repositories
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<Result<DashboardData>> = combine(
        currencyRepository.getSupportedCurrencies(),
        rateRepository.getExchangeRates(),
        walletRepository.getWalletBalances()
    ) { currenciesResult, ratesResult, balancesResult ->
        // Handle potential errors from individual flows first
        val currencies = (currenciesResult as? Result.Success)?.data
        val rates = (ratesResult as? Result.Success)?.data
        val balances = (balancesResult as? Result.Success)?.data

        // If any result is an error, propagate the first error found
        val firstError = listOf(currenciesResult, ratesResult, balancesResult)
            .filterIsInstance<Result.Error>()
            .firstOrNull()

        if (firstError != null) {
            return@combine Result.Error(firstError.exception)
        }

        // If any data is missing (should ideally not happen if no error), treat as error
        if (currencies == null || rates == null || balances == null) {
            // Check if any source actually returned success but with empty data, which might be valid
            if ( (currenciesResult is Result.Success && currencies == null) || 
                 (ratesResult is Result.Success && rates == null) || 
                 (balancesResult is Result.Success && balances == null) ) {
                 // If sources are successful but empty, proceed with empty lists
                 // This prevents errors if, e.g., the rates file is valid JSON but contains an empty array
                 val safeCurrencies = currencies ?: emptyList()
                 val safeRates = rates ?: emptyList()
                 val safeBalances = balances ?: emptyList()
                 try {
                     val aggregatedBalances = aggregateBalances(safeCurrencies, safeRates, safeBalances)
                     val totalUsdValue = calculateTotalUsdValue(aggregatedBalances)
                     Result.Success(DashboardData(aggregatedBalances, totalUsdValue))
                 } catch (e: Exception) {
                     Result.Error(e)
                 }
            } else {
                // If null data wasn't from a success state, it's likely an unexpected issue
                return@combine Result.Error(IllegalStateException("Missing data despite no explicit error"))
            }
        } else {
            // Process the data if all successful and non-null
            try {
                val aggregatedBalances = aggregateBalances(currencies, rates, balances)
                val totalUsdValue = calculateTotalUsdValue(aggregatedBalances)
                Result.Success(DashboardData(aggregatedBalances, totalUsdValue))
            } catch (e: Exception) {
                // Catch potential calculation errors
                Result.Error(e)
            }
        }

    }.catch { e ->
        // Catch exceptions during the flow combination itself
        emit(Result.Error(e))
    }

    private fun aggregateBalances(
        currencies: List<Currency>,
        rates: List<ExchangeRate>,
        balances: List<WalletBalance>
    ): List<AggregatedBalance> {
        val currenciesMap = currencies.associateBy { it.symbol }
        val ratesMap = rates.filter { it.toSymbol == "USD" }.associateBy { it.fromSymbol }

        return balances.mapNotNull { balance ->
            val currency = currenciesMap[balance.currencySymbol]
            val rate = ratesMap[balance.currencySymbol]?.rate

            if (currency != null && rate != null && balance.amount > BigDecimal.ZERO) { // Only include if balance > 0
                val usdValue = balance.amount.multiply(rate).setScale(2, RoundingMode.HALF_UP)
                AggregatedBalance(
                    currency = currency,
                    balanceAmount = balance.amount,
                    balanceUsdValue = usdValue
                )
            } else {
                // Exclude currencies with no balance, missing info, or missing rate
                null
            }
        }.sortedByDescending { it.balanceUsdValue } // Sort by USD value
    }

    private fun calculateTotalUsdValue(aggregatedBalances: List<AggregatedBalance>): BigDecimal {
        return aggregatedBalances.fold(BigDecimal.ZERO) { sum, balance ->
            sum.add(balance.balanceUsdValue)
        }.setScale(2, RoundingMode.HALF_UP)
    }
} 