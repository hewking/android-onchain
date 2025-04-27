package com.cryptocom.wallet.presentation.dashboard.model

import com.cryptocom.wallet.domain.model.AggregatedBalance
import java.math.BigDecimal

/**
 * Represents the possible states of the Dashboard UI.
 */
data class DashboardUiState(
    val isLoading: Boolean = true,
    val balances: List<AggregatedBalance> = emptyList(),
    val totalUsdValue: BigDecimal = BigDecimal.ZERO,
    val error: String? = null // Error message string or resource ID
)

/**
 * Represents the events that can be triggered from the Dashboard UI or ViewModel.
 * For this simple case, we might only need an initial load event.
 */
sealed class DashboardEvent {
    object LoadData : DashboardEvent() // Event to trigger initial data load
    // Add other events like Refresh, Sort, Filter, etc. if needed
}

/**
 * Represents side effects that the ViewModel might trigger (e.g., showing a Toast).
 * Not strictly required for this basic example but good MVI practice.
 */
sealed class DashboardEffect {
    data class ShowErrorToast(val message: String) : DashboardEffect()
} 