package com.cryptocom.wallet.data.mapper

import com.cryptocom.wallet.data.model.BalanceDto
import com.cryptocom.wallet.data.model.CurrencyDto
import com.cryptocom.wallet.data.model.RateDto
import com.cryptocom.wallet.domain.model.Currency
import com.cryptocom.wallet.domain.model.ExchangeRate
import com.cryptocom.wallet.domain.model.WalletBalance
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

// --- Currency Mapper --- //
fun CurrencyDto.toDomain(): Currency {
    return Currency(
        id = this.id,
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
fun RateDto.toDomain(): ExchangeRate? { // Nullable if rate string is invalid
    return try {
        // Use a parser that handles potential locale differences if necessary,
        // but BigDecimal(String) is usually robust for standard decimal formats.
        val rateDecimal = BigDecimal(this.rate)
        ExchangeRate(
            fromSymbol = this.from,
            toSymbol = this.to,
            rate = rateDecimal
        )
    } catch (e: NumberFormatException) {
        // Log error or handle invalid rate strings appropriately
        System.err.println("Failed to parse rate: ${this.rate} for ${this.from} -> ${this.to}. Error: ${e.message}")
        null // Return null for invalid entries
    }
}

@JvmName("rateDtoListToDomain")
fun List<RateDto>.toDomain(): List<ExchangeRate> {
    return this.mapNotNull { it.toDomain() } // mapNotNull skips null results from invalid DTOs
}

// --- Balance Mapper --- //
fun BalanceDto.toDomain(): WalletBalance? { // Nullable if amount string is invalid
    return try {
        val amountDecimal = BigDecimal(this.amount)
        WalletBalance(
            currencySymbol = this.symbol,
            amount = amountDecimal
        )
    } catch (e: NumberFormatException) {
        System.err.println("Failed to parse balance amount: ${this.amount} for ${this.symbol}. Error: ${e.message}")
        null // Return null for invalid entries
    }
}

@JvmName("balanceDtoListToDomain")
fun List<BalanceDto>.toDomain(): List<WalletBalance> {
    return this.mapNotNull { it.toDomain() } // mapNotNull skips null results from invalid DTOs
} 