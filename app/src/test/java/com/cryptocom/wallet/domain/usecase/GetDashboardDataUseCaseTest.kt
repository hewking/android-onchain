package com.cryptocom.wallet.domain.usecase

import app.cash.turbine.test
import com.cryptocom.wallet.domain.common.Result
import com.cryptocom.wallet.domain.model.Currency
import com.cryptocom.wallet.domain.model.DashboardData
import com.cryptocom.wallet.domain.model.ExchangeRate
import com.cryptocom.wallet.domain.model.WalletBalance
import com.cryptocom.wallet.domain.repository.CurrencyRepository
import com.cryptocom.wallet.domain.repository.RateRepository
import com.cryptocom.wallet.domain.repository.WalletRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

class GetDashboardDataUseCaseTest {

    private lateinit var currencyRepository: CurrencyRepository
    private lateinit var rateRepository: RateRepository
    private lateinit var walletRepository: WalletRepository
    private lateinit var useCase: GetDashboardDataUseCase

    // Sample Data
    private val btc = Currency("1", "Bitcoin", "BTC", null)
    private val eth = Currency("2", "Ethereum", "ETH", null)
    private val cro = Currency("3", "Cronos", "CRO", null)
    private val usd = Currency("USD", "US Dollar", "USD", null) // Assuming USD is a currency for rates

    private val currencies = listOf(btc, eth, cro, usd)
    private val balances = listOf(
        WalletBalance("BTC", BigDecimal("0.5")),
        WalletBalance("ETH", BigDecimal("10.0"))
    )
    private val rates = listOf(
        ExchangeRate("BTC", "USD", BigDecimal("60000.00")),
        ExchangeRate("ETH", "USD", BigDecimal("3000.00")),
        ExchangeRate("CRO", "USD", BigDecimal("0.10"))
    )

    @Before
    fun setUp() {
        currencyRepository = mockk()
        rateRepository = mockk()
        walletRepository = mockk()
        useCase = GetDashboardDataUseCase(currencyRepository, rateRepository, walletRepository)
    }

    @Test
    fun `invoke should return success with aggregated data when all repositories return success`() = runTest {
        // Arrange
        coEvery { currencyRepository.getSupportedCurrencies() } returns flowOf(Result.Success(currencies))
        coEvery { rateRepository.getExchangeRates() } returns flowOf(Result.Success(rates))
        coEvery { walletRepository.getWalletBalances() } returns flowOf(Result.Success(balances))

        // Act & Assert
        useCase().test {
            val result = awaitItem()
            assertTrue(result is Result.Success)
            val data = (result as Result.Success<DashboardData>).data

            assertEquals(BigDecimal("60000.00"), data.totalUsdValue) // 0.5 BTC * 60000 + 10 ETH * 3000
            assertEquals(2, data.balances.size)

            val btcBalance = data.balances.find { it.currency.symbol == "BTC" }
            assertEquals(BigDecimal("0.5"), btcBalance?.balanceAmount)
            assertEquals(BigDecimal("30000.00"), btcBalance?.balanceUsdValue) // 0.5 * 60000

            val ethBalance = data.balances.find { it.currency.symbol == "ETH" }
            assertEquals(BigDecimal("10.0"), ethBalance?.balanceAmount)
            assertEquals(BigDecimal("30000.00"), ethBalance?.balanceUsdValue) // 10 * 3000

            awaitComplete()
        }
    }

    @Test
    fun `invoke should return error when currency repository returns error`() = runTest {
        // Arrange
        val exception = RuntimeException("Failed to load currencies")
        coEvery { currencyRepository.getSupportedCurrencies() } returns flowOf(Result.Error(exception))
        coEvery { rateRepository.getExchangeRates() } returns flowOf(Result.Success(rates))
        coEvery { walletRepository.getWalletBalances() } returns flowOf(Result.Success(balances))

        // Act & Assert
        useCase().test {
            val result = awaitItem()
            assertTrue(result is Result.Error)
            assertEquals(exception, (result as Result.Error).exception)
            awaitComplete()
        }
    }

     @Test
    fun `invoke should return error when rate repository returns error`() = runTest {
        // Arrange
        val exception = RuntimeException("Failed to load rates")
        coEvery { currencyRepository.getSupportedCurrencies() } returns flowOf(Result.Success(currencies))
        coEvery { rateRepository.getExchangeRates() } returns flowOf(Result.Error(exception))
        coEvery { walletRepository.getWalletBalances() } returns flowOf(Result.Success(balances))

        // Act & Assert
        useCase().test {
            val result = awaitItem()
            assertTrue(result is Result.Error)
            assertEquals(exception, (result as Result.Error).exception)
            awaitComplete()
        }
    }

    @Test
    fun `invoke should return error when wallet repository returns error`() = runTest {
        // Arrange
        val exception = RuntimeException("Failed to load balances")
        coEvery { currencyRepository.getSupportedCurrencies() } returns flowOf(Result.Success(currencies))
        coEvery { rateRepository.getExchangeRates() } returns flowOf(Result.Success(rates))
        coEvery { walletRepository.getWalletBalances() } returns flowOf(Result.Error(exception))

        // Act & Assert
        useCase().test {
            val result = awaitItem()
            assertTrue(result is Result.Error)
            assertEquals(exception, (result as Result.Error).exception)
            awaitComplete()
        }
    }


    @Test
    fun `invoke should handle missing rates for a balance gracefully`() = runTest {
         // Arrange
         val balancesWithMissingRate = balances + WalletBalance("CRO", BigDecimal("1000")) // CRO rate exists
         val ratesWithoutEth = rates.filter { it.fromSymbol != "ETH" }

         coEvery { currencyRepository.getSupportedCurrencies() } returns flowOf(Result.Success(currencies))
         coEvery { rateRepository.getExchangeRates() } returns flowOf(Result.Success(ratesWithoutEth)) // ETH rate missing
         coEvery { walletRepository.getWalletBalances() } returns flowOf(Result.Success(balancesWithMissingRate))

         // Act & Assert
         useCase().test {
             val result = awaitItem()
             assertTrue(result is Result.Success)
             val data = (result as Result.Success<DashboardData>).data

             // Total value should only include BTC and CRO
             assertEquals(BigDecimal("30100.00"), data.totalUsdValue) // 0.5 BTC * 60000 + 1000 CRO * 0.10

             assertEquals(3, data.balances.size) // Includes BTC, ETH, CRO

             val btcBalance = data.balances.find { it.currency.symbol == "BTC" }
             assertEquals(BigDecimal("30000.00"), btcBalance?.balanceUsdValue)

             val ethBalance = data.balances.find { it.currency.symbol == "ETH" }
             assertEquals(BigDecimal.ZERO, ethBalance?.balanceUsdValue) // ETH USD value should be zero

             val croBalance = data.balances.find { it.currency.symbol == "CRO" }
             assertEquals(BigDecimal("100.00"), croBalance?.balanceUsdValue) // 1000 * 0.10

             awaitComplete()
         }
     }

    @Test
    fun `invoke should return empty list and zero total when wallet balance is empty`() = runTest {
        // Arrange
        coEvery { currencyRepository.getSupportedCurrencies() } returns flowOf(Result.Success(currencies))
        coEvery { rateRepository.getExchangeRates() } returns flowOf(Result.Success(rates))
        coEvery { walletRepository.getWalletBalances() } returns flowOf(Result.Success(emptyList())) // Input is empty

        // Act & Assert
        useCase().test {
            val result = awaitItem()
            assertTrue("Result should be Success", result is Result.Success)
            val data = (result as Result.Success<DashboardData>).data

            // Check total value first
            assertEquals(
                "Total USD value should be zero when balances are empty",
                BigDecimal.ZERO.setScale(2), // Ensure scale matches use case output
                data.totalUsdValue
            )
            // Check balances list
            assertTrue(
                "Balances list should be empty when input balances are empty",
                data.balances.isEmpty()
            )

            awaitComplete()
        }
    }

} 