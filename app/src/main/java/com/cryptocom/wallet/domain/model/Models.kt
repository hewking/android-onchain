package com.cryptocom.wallet.domain.model

import java.math.BigDecimal

/**
 * Represents a cryptocurrency supported by the app.
 */
data class Currency(
    val id: String,
    val name: String,
    val symbol: String,
    val iconUrl: String? // URL to the currency's icon, nullable if not available
)

/**
 * Represents an exchange rate between two currencies.
 */
data class ExchangeRate(
    val fromSymbol: String, // e.g., "BTC"
    val toSymbol: String,   // e.g., "USD"
    val rate: BigDecimal    // The amount of 'toSymbol' equivalent to 1 unit of 'fromSymbol'
)

/**
 * Represents the balance of a specific cryptocurrency in the user's wallet.
 */
data class WalletBalance(
    val currencySymbol: String, // e.g., "BTC"
    val amount: BigDecimal      // The quantity held
)

/**
 * Represents an aggregated view of a specific currency balance, including its USD value.
 */
data class AggregatedBalance(
    val currency: Currency,
    val balanceAmount: BigDecimal,
    val balanceUsdValue: BigDecimal
)

/**
 * Represents the consolidated data required for the dashboard UI.
 */
data class DashboardData(
    val balances: List<AggregatedBalance>,
    val totalUsdValue: BigDecimal
) 