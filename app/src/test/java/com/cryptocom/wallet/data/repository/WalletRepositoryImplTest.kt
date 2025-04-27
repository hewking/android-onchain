package com.cryptocom.wallet.data.repository

import app.cash.turbine.test
import com.cryptocom.wallet.data.datasource.local.LocalAssetDataSource
import com.cryptocom.wallet.domain.common.Result
import com.cryptocom.wallet.domain.model.WalletBalance
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Assert.* // Import Assert directly for convenience
import org.junit.Before
import org.junit.Test
import java.io.IOException
import java.math.BigDecimal

@ExperimentalCoroutinesApi
class WalletRepositoryImplTest {

    private lateinit var localDataSource: LocalAssetDataSource
    private lateinit var repository: WalletRepositoryImpl
    private val testDispatcher = UnconfinedTestDispatcher() // Use Unconfined for immediate execution
    private val json = Json { ignoreUnknownKeys = true } // Default Json instance for testing

    @Before
    fun setUp() {
        localDataSource = mockk()
        // Pass dispatcher and json parser
        repository = WalletRepositoryImpl(localDataSource, json, testDispatcher)
    }

    @Test
    fun `getWalletBalances should emit Loading then Success with parsed balances`() = runTest {
        // Arrange
        val validJson = """
            {
                "ok": true,
                "wallet": [
                    {"currency": "BTC", "amount": 0.5},
                    {"currency": "ETH", "amount": 10.0}
                ]
            }
            """
        coEvery { localDataSource.getBalancesJsonString() } returns validJson

        // Act & Assert
        repository.getWalletBalances().test {
            // 1. Loading state
            assertEquals(Result.Loading, awaitItem())

            // 2. Success state
            val result = awaitItem()
            assertTrue(result is Result.Success)
            val balances = (result as Result.Success<List<WalletBalance>>).data
            assertEquals(2, balances.size)
            assertEquals("BTC", balances[0].currencySymbol)
            assertEquals(BigDecimal("0.5"), balances[0].amount)
            assertEquals("ETH", balances[1].currencySymbol)
            assertEquals(BigDecimal("10.0"), balances[1].amount)

            awaitComplete()
        }
    }

    @Test
    fun `getWalletBalances should emit Loading then Error when JSON parsing fails`() = runTest {
        // Arrange
        val invalidJson = """{"wallet": [{"currency": "BTC", "amount": "invalid"}]}""" // Invalid amount type
        coEvery { localDataSource.getBalancesJsonString() } returns invalidJson

        // Act & Assert
        repository.getWalletBalances().test {
            // 1. Loading state
            assertEquals(Result.Loading, awaitItem())

            // 2. Error state
            val result = awaitItem()
            assertTrue(result is Result.Error)
            // Check if the exception is related to serialization/parsing
            assertTrue((result as Result.Error).exception is kotlinx.serialization.SerializationException)

            awaitComplete()
        }
    }

    @Test
    fun `getWalletBalances should emit Loading then Error when DataSource throws IOException`() = runTest {
        // Arrange
        val ioException = IOException("Failed to read file")
        coEvery { localDataSource.getBalancesJsonString() } throws ioException

        // Act & Assert
        repository.getWalletBalances().test {
            // 1. Loading state
            assertEquals(Result.Loading, awaitItem())

            // 2. Error state
            val result = awaitItem()
            assertTrue(result is Result.Error)
            assertEquals(ioException, (result as Result.Error).exception)

            awaitComplete()
        }
    }

    @Test
    fun `getWalletBalances should handle empty wallet list correctly`() = runTest {
        // Arrange
        val emptyJson = """{"ok": true, "wallet": [] }"""
        coEvery { localDataSource.getBalancesJsonString() } returns emptyJson

        // Act & Assert
        repository.getWalletBalances().test {
            assertEquals(Result.Loading, awaitItem())
            val result = awaitItem()
            assertTrue(result is Result.Success)
            val balances = (result as Result.Success<List<WalletBalance>>).data
            assertTrue(balances.isEmpty())
            awaitComplete()
        }
    }
} 