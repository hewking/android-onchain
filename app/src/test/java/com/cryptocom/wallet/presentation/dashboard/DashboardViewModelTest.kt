package com.cryptocom.wallet.presentation.dashboard

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.cryptocom.wallet.domain.common.Result
import com.cryptocom.wallet.domain.model.AggregatedBalance
import com.cryptocom.wallet.domain.model.Currency
import com.cryptocom.wallet.domain.model.DashboardData
import com.cryptocom.wallet.domain.usecase.GetDashboardDataUseCase
import com.cryptocom.wallet.presentation.dashboard.model.DashboardUiState
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal

@ExperimentalCoroutinesApi
class DashboardViewModelTest {

    // Rule for testing Architecture Components
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var getDashboardDataUseCase: GetDashboardDataUseCase
    private lateinit var viewModel: DashboardViewModel

    // Sample Data
    private val btc = Currency("1", "Bitcoin", "BTC", null)
    private val eth = Currency("2", "Ethereum", "ETH", null)
    private val aggregatedBtc = AggregatedBalance(btc, BigDecimal("0.5"), BigDecimal("30000"))
    private val aggregatedEth = AggregatedBalance(eth, BigDecimal("2.0"), BigDecimal("6000"))
    private val dashboardDataSuccess = DashboardData(
        balances = listOf(aggregatedBtc, aggregatedEth),
        totalUsdValue = BigDecimal("36000")
    )
    private val genericException = RuntimeException("Network Error")

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher) // Set main dispatcher for ViewModelScope
        getDashboardDataUseCase = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // Reset main dispatcher
    }

    @Test
    fun `initial state should be loading`() = runTest {
        // Arrange
        coEvery { getDashboardDataUseCase() } returns flow { /* Don't emit yet */ }

        // Act
        viewModel = DashboardViewModel(getDashboardDataUseCase)

        // Assert
        assertEquals(DashboardUiState(isLoading = true), viewModel.uiState.value)
    }

    @Test
    fun `uiState should update to success when use case returns success`() = runTest {
        // Arrange
        coEvery { getDashboardDataUseCase() } returns flowOf(Result.Success(dashboardDataSuccess))

        // Act
        viewModel = DashboardViewModel(getDashboardDataUseCase)

        // Assert
        viewModel.uiState.test {
            // Initial state
            assertEquals(DashboardUiState(isLoading = true), awaitItem())

            // Success state
            val successState = awaitItem()
            assertEquals(false, successState.isLoading)
            assertEquals(null, successState.error)
            assertEquals(BigDecimal("36000"), successState.totalUsdValue)
            assertEquals(listOf(aggregatedBtc, aggregatedEth), successState.balances)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `uiState should update to error when use case returns error`() = runTest {
        // Arrange
        coEvery { getDashboardDataUseCase() } returns flowOf(Result.Error(genericException))

        // Act
        viewModel = DashboardViewModel(getDashboardDataUseCase)

        // Assert
        viewModel.uiState.test {
            // Initial state
            assertEquals(DashboardUiState(isLoading = true), awaitItem())

            // Error state
            val errorState = awaitItem()
            assertEquals(false, errorState.isLoading)
            assertEquals(genericException.message, errorState.error)
            assertEquals(BigDecimal.ZERO, errorState.totalUsdValue)
            assertTrue(errorState.balances.isEmpty())

            cancelAndIgnoreRemainingEvents()
        }
    }

     @Test
    fun `uiState should show loading then success`() = runTest {
        // Arrange
        val flow = flow {
            // No emission initially to check loading state
            kotlinx.coroutines.delay(100) // Simulate network delay
            emit(Result.Success(dashboardDataSuccess))
        }
        coEvery { getDashboardDataUseCase() } returns flow

        // Act
        viewModel = DashboardViewModel(getDashboardDataUseCase)

        // Assert
        viewModel.uiState.test {
            // Initial state (Loading)
            assertEquals(DashboardUiState(isLoading = true), awaitItem())

            // Success state arrives after delay
            val successState = awaitItem()
            assertEquals(false, successState.isLoading)
            assertEquals(null, successState.error)
            assertEquals(dashboardDataSuccess.totalUsdValue, successState.totalUsdValue)
            assertEquals(dashboardDataSuccess.balances, successState.balances)

            cancelAndIgnoreRemainingEvents()
        }
    }

} 