package com.cryptocom.wallet.presentation.dashboard

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.cryptocom.wallet.databinding.ActivityDashboardBinding
import com.cryptocom.wallet.presentation.dashboard.model.DashboardEffect
import com.cryptocom.wallet.presentation.dashboard.model.DashboardUiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@AndroidEntryPoint
class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private val viewModel: DashboardViewModel by viewModels()
    private lateinit var dashboardAdapter: DashboardAdapter

    private val usdFormat = NumberFormat.getCurrencyInstance(Locale.US)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        dashboardAdapter = DashboardAdapter() // Instantiate the adapter
        binding.rvBalances.apply {
            adapter = dashboardAdapter
            val linearLayoutManager = LinearLayoutManager(this@DashboardActivity)
            layoutManager = linearLayoutManager
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Observe UI State
                launch {
                    viewModel.uiState.collect { state -> renderState(state) }
                }

                // Observe Effects
                launch {
                    viewModel.effect.collect { effect -> handleEffect(effect) }
                }
            }
        }
    }

    private fun renderState(state: DashboardUiState) {
        Log.d("DashboardActivity", "Observing UI state" + state.error)

        binding.progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE
        binding.tvError.visibility = if (state.error != null && !state.isLoading) View.VISIBLE else View.GONE
        binding.rvBalances.visibility = if (state.error == null && !state.isLoading) View.VISIBLE else View.GONE

        binding.tvError.text = state.error
        binding.tvTotalBalanceValue.text = usdFormat.format(state.totalUsdValue)

        // Update adapter data only if it has changed
        if (dashboardAdapter.currentList != state.balances) {
            dashboardAdapter.submitList(state.balances)
        }
        
        // Hide total balance labels if loading or error
        val labelsVisible = !state.isLoading && state.error == null
        binding.tvTotalBalanceLabel.visibility = if(labelsVisible) View.VISIBLE else View.INVISIBLE
        binding.tvTotalBalanceValue.visibility = if(labelsVisible) View.VISIBLE else View.INVISIBLE
    }

    private fun handleEffect(effect: DashboardEffect) {
        when (effect) {
            is DashboardEffect.ShowErrorToast -> {
                Toast.makeText(this, effect.message, Toast.LENGTH_SHORT).show()
            }
            // Handle other effects
        }
    }
} 