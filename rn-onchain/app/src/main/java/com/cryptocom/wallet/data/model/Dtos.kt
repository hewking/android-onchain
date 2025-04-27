package com.cryptocom.wallet.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// --- DTOs matching currencies.json --- //
@Serializable
data class CurrencyDto(
    @SerialName("coin_id") val coinId: String,
    @SerialName("name") val name: String,
    @SerialName("symbol") val symbol: String,
    @SerialName("colorful_image_url") val iconUrl: String? = null
)

@Serializable
data class CurrenciesResponseDto(
    @SerialName("currencies") val currencies: List<CurrencyDto>,
    @SerialName("ok") val ok: Boolean? = null
)

// --- DTOs matching rates.json --- //
@Serializable
data class RateInfo(
    @SerialName("amount") val amountFromJson: String? = null,
    @SerialName("rate") val rate: Double
)

@Serializable
data class RateTier(
    @SerialName("from_currency") val fromCurrency: String,
    @SerialName("to_currency") val toCurrency: String,
    @SerialName("rates") val rates: List<RateInfo>
)

@Serializable
data class RatesResponseDto(
    @SerialName("tiers") val tiers: List<RateTier>,
    @SerialName("ok") val ok: Boolean? = null
)

// --- DTOs matching balances.json --- //
@Serializable
data class BalanceDto(
    @SerialName("currency") val currencySymbol: String,
    @SerialName("amount") val amount: Double
)

@Serializable
data class BalancesResponseDto(
    @SerialName("wallet") val balances: List<BalanceDto>,
    @SerialName("ok") val ok: Boolean? = null
) 