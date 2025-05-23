package com.cryptocom.wallet.presentation.dashboard

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.cryptocom.wallet.R
import com.cryptocom.wallet.databinding.ItemDashboardBalanceBinding
import com.cryptocom.wallet.domain.model.AggregatedBalance
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.Locale

class DashboardAdapter : ListAdapter<AggregatedBalance, DashboardAdapter.BalanceViewHolder>(BalanceDiffCallback()) {

    // Consider adding an image loading library (Glide/Coil) here
    // private val imageLoader: ImageLoader = ...

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BalanceViewHolder {
        val binding = ItemDashboardBalanceBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BalanceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BalanceViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class BalanceViewHolder(private val binding: ItemDashboardBalanceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val usdFormat = NumberFormat.getCurrencyInstance(Locale.US).apply {
            // Optional: Customize formatting (e.g., minimum/maximum fraction digits)
        }

        @SuppressLint("SetTextI18n") // For simple concatenation, fine. Use plurals/string resources for more complex text.
        fun bind(item: AggregatedBalance) {
            binding.tvCurrencyName.text = item.currency.name
            // Combine amount and symbol for tvBalanceAmount
            val formattedAmount = item.balanceAmount.stripTrailingZeros().toPlainString()
            binding.tvBalanceAmount.text = "$formattedAmount ${item.currency.symbol}"
            // Format USD value with currency symbol prefix
            binding.tvBalanceUsdValue.text = usdFormat.format(item.balanceUsdValue)

            // Log the URL being loaded
            Log.d("DashboardAdapter", "Loading image URL: ${item.currency.iconUrl}")

            // Use Coil to load the image
            binding.ivCurrencyIcon.load(item.currency.iconUrl) {
                crossfade(true) // Optional: Add fade-in animation
                placeholder(R.mipmap.logo) // Changed placeholder
                error(R.mipmap.logo)       // Changed error placeholder
//                transform(CircleCropTransformation()) // Optional: Make icons circular
            }
        }
    }

    class BalanceDiffCallback : DiffUtil.ItemCallback<AggregatedBalance>() {
        override fun areItemsTheSame(oldItem: AggregatedBalance, newItem: AggregatedBalance): Boolean {
            return oldItem.currency.id == newItem.currency.id
        }

        override fun areContentsTheSame(oldItem: AggregatedBalance, newItem: AggregatedBalance): Boolean {
            // Compare all fields that affect the visual representation
            return oldItem == newItem
        }
    }
} 