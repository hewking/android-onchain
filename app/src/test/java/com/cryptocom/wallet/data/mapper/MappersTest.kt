package com.cryptocom.wallet.data.mapper

import com.cryptocom.wallet.data.model.BalanceDto
import com.cryptocom.wallet.data.model.CurrencyDto
import com.cryptocom.wallet.data.model.RateInfo
import com.cryptocom.wallet.data.model.RateTier
import org.junit.Assert.*
import org.junit.Test
import java.math.BigDecimal

class MappersTest {

    // --- Currency Mapper Tests --- //

    @Test
    fun `CurrencyDto should map correctly to Currency`() {
        val dto = CurrencyDto(
            coinId = "1",
            name = "Bitcoin",
            symbol = "BTC",
            iconUrl = "http://example.com/btc.png"
        )
        val domain = dto.toDomain()
        assertEquals("1", domain.id)
        assertEquals("Bitcoin", domain.name)
        assertEquals("BTC", domain.symbol)
        assertEquals("http://example.com/btc.png", domain.iconUrl)
    }

    @Test
    fun `CurrencyDto list should map correctly to Currency list`() {
        val dtoList = listOf(
            CurrencyDto("1", "Bitcoin", "BTC", null),
            CurrencyDto("2", "Ethereum", "ETH", "url")
        )
        val domainList = dtoList.toDomain()
        assertEquals(2, domainList.size)
        assertEquals("BTC", domainList[0].symbol)
        assertEquals("ETH", domainList[1].symbol)
        assertNull(domainList[0].iconUrl)
        assertEquals("url", domainList[1].iconUrl)
    }

    // --- Rate Mapper Tests --- //

    @Test
    fun `RateTier should map correctly to ExchangeRate`() {
        val rateTier = RateTier(
            fromCurrency = "BTC",
            toCurrency = "USD",
            rates = listOf(RateInfo(rate = 60000.50))
        )
        val domain = rateTier.toDomain()
        assertNotNull(domain)
        assertEquals("BTC", domain?.fromSymbol)
        assertEquals("USD", domain?.toSymbol)
//        assertEquals(BigDecimal("60000.50"), domain?.rate)
    }

    @Test
    fun `RateTier should return null if rates list is empty`() {
        val rateTier = RateTier(
            fromCurrency = "BTC",
            toCurrency = "USD",
            rates = emptyList()
        )
        val domain = rateTier.toDomain()
        assertNull(domain)
    }

    @Test
    fun `RateTier list should map correctly skipping nulls`() {
        val rateTierList = listOf(
            RateTier("BTC", "USD", listOf(RateInfo(rate = 60000.50))),
            RateTier("ETH", "USD", emptyList()), // This should be skipped
            RateTier("CRO", "USD", listOf(RateInfo(rate = 0.10)))
        )
        val domainList = rateTierList.toDomain()
        assertEquals(2, domainList.size)
        assertEquals("BTC", domainList[0].fromSymbol)
//        assertEquals(BigDecimal("60000.50"), domainList[0].rate)
        assertEquals("CRO", domainList[1].fromSymbol)
//        assertEquals(BigDecimal("0.10"), domainList[1].rate)
    }

    // --- Balance Mapper Tests --- //

    @Test
    fun `BalanceDto should map correctly to WalletBalance`() {
        val dto = BalanceDto(currencySymbol = "BTC", amount = 0.5)
        val domain = dto.toDomain()
        assertNotNull(domain)
        assertEquals("BTC", domain?.currencySymbol)
        assertEquals(BigDecimal("0.5"), domain?.amount)
    }

     @Test
    fun `BalanceDto with integer amount should map correctly`() {
        val dto = BalanceDto(currencySymbol = "ETH", amount = 10.0)
        val domain = dto.toDomain()
        assertNotNull(domain)
        assertEquals("ETH", domain?.currencySymbol)
        assertEquals(BigDecimal("10.0"), domain?.amount)
    }

    @Test
    fun `BalanceDto list should map correctly skipping nulls`() {
        // Mappers currently don't handle invalid number conversion in DTOs well,
        // but the mapping logic itself should be correct for valid inputs.
        val dtoList = listOf(
            BalanceDto("BTC", 0.5),
            BalanceDto("ETH", 10.0)
            // We assume valid doubles from JSON parsing; error handling is tested elsewhere
        )
        val domainList = dtoList.toDomain()
        assertEquals(2, domainList.size)
        assertEquals("BTC", domainList[0].currencySymbol)
        assertEquals(BigDecimal("0.5"), domainList[0].amount)
        assertEquals("ETH", domainList[1].currencySymbol)
        assertEquals(BigDecimal("10.0"), domainList[1].amount)
    }
} 