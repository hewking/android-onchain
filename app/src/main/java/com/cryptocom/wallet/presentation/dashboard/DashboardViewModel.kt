package com.cryptocom.wallet.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cryptocom.wallet.domain.common.Result
import com.cryptocom.wallet.domain.usecase.GetDashboardDataUseCase
import com.cryptocom.wallet.presentation.dashboard.model.DashboardEffect
import com.cryptocom.wallet.presentation.dashboard.model.DashboardEvent
import com.cryptocom.wallet.presentation.dashboard.model.DashboardUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getDashboardDataUseCase: GetDashboardDataUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState(isLoading = true))
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    // Use SharedFlow for one-shot events (Effects)
    private val _effect = MutableSharedFlow<DashboardEffect>()
    val effect: SharedFlow<DashboardEffect> = _effect.asSharedFlow()

    init {
        // Trigger initial data load when ViewModel is created
        handleEvent(DashboardEvent.LoadData)
    }

    /**
     * Handles events triggered by the UI or internal logic.
     */
    fun handleEvent(event: DashboardEvent) {
        when (event) {
            is DashboardEvent.LoadData -> loadDashboardData()
        }
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            getDashboardDataUseCase()
                .onStart { _uiState.update { it.copy(isLoading = true, error = null) } }
                .catch { throwable ->
                    // Catch unexpected errors in the flow itself (upstream)
                    _uiState.update { it.copy(isLoading = false, error = throwable.localizedMessage ?: "An unexpected error occurred") }
                    _effect.emit(DashboardEffect.ShowErrorToast(throwable.localizedMessage ?: "Error loading data"))
                }
                .collect { result ->
                    when (result) {
                        is Result.Success -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    balances = result.data.balances,
                                    totalUsdValue = result.data.totalUsdValue,
                                    error = null
                                )
                            }
                        }
                        is Result.Error -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    error = result.exception.localizedMessage ?: "Failed to load data"
                                )
                            }
                            // Optionally emit an effect here too
                            _effect.emit(DashboardEffect.ShowErrorToast(result.exception.localizedMessage ?: "Error"))
                        }
                        is Result.Loading -> {
                            // Handled by onStart, but can update here if needed
                            _uiState.update { it.copy(isLoading = true, error = null) }
                        }
                    }
                }
        }
    }
} 