package com.cryptocom.wallet.data.mapper

import android.util.Log
import com.cryptocom.wallet.data.model.BalanceDto
import com.cryptocom.wallet.data.model.CurrencyDto
import com.cryptocom.wallet.data.model.RateTier
import com.cryptocom.wallet.domain.model.Currency
import com.cryptocom.wallet.domain.model.ExchangeRate
import com.cryptocom.wallet.domain.model.WalletBalance
import java.math.BigDecimal

// --- Currency Mapper --- //
fun CurrencyDto.toDomain(): Currency {
    return Currency(
        id = this.coinId,
        name = this.name,
        symbol = this.symbol,
        iconUrl = this.iconUrl
    )
}

@JvmName("currencyDtoListToDomain")
fun List<CurrencyDto>.toDomain(): List<Currency> {
    return this.map { it.toDomain() }
}

// --- Rate Mapper --- //
fun RateTier.toDomain(): ExchangeRate? {
    val rateDouble = this.rates.firstOrNull()?.rate ?: return null
    
    return try {
        val rateDecimal = BigDecimal.valueOf(rateDouble)
        ExchangeRate(
            fromSymbol = this.fromCurrency,
            toSymbol = this.toCurrency,
            rate = rateDecimal
        )
    } catch (e: NumberFormatException) {
        Log.e("Mappers", "Error converting rate Double: $rateDouble for ${this.fromCurrency} -> ${this.toCurrency}. Error: ${e.message}")
        null
    }
}

@JvmName("rateTierListToDomain")
fun List<RateTier>.toDomain(): List<ExchangeRate> {
    return this.mapNotNull { it.toDomain() }
}

// --- Balance Mapper --- //
fun BalanceDto.toDomain(): WalletBalance? {
    return try {
        val amountDecimal = BigDecimal.valueOf(this.amount)
        WalletBalance(
            currencySymbol = this.currencySymbol,
            amount = amountDecimal
        )
    } catch (e: NumberFormatException) {
        Log.e("Mapper","Error converting balance Double: ${this.amount} for ${this.currencySymbol}. Error: ${e.message}")
        null
    }
}

@JvmName("balanceDtoListToDomain")
fun List<BalanceDto>.toDomain(): List<WalletBalance> {
    return this.mapNotNull { it.toDomain() }
} 