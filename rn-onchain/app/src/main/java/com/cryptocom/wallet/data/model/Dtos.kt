package com.cryptocom.wallet.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// --- DTOs matching currencies.json --- //
@Serializable
data class CurrencyDto(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("symbol") val symbol: String,
    @SerialName("iconUrl") val iconUrl: String? = null // Make nullable to handle potential missing data
)

// --- DTOs matching rates.json --- //
@Serializable
data class RateDto(
    @SerialName("from") val from: String,
    @SerialName("to") val to: String,
    @SerialName("rate") val rate: String // Keep as String for initial parsing
)

@Serializable
data class RatesResponseDto(
    @SerialName("rates") val rates: List<RateDto>
)

// --- DTOs matching balances.json --- //
@Serializable
data class BalanceDto(
    @SerialName("symbol") val symbol: String,
    @SerialName("amount") val amount: String // Keep as String for initial parsing
)

@Serializable
data class BalancesResponseDto(
    @SerialName("balances") val balances: List<BalanceDto>
) 