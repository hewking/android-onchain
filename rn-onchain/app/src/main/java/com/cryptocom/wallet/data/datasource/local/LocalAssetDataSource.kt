package com.cryptocom.wallet.data.datasource.local

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

/**
 * Interface for accessing raw data from local storage (assets).
 */
interface LocalAssetDataSource {
    suspend fun getCurrenciesJsonString(): String
    suspend fun getRatesJsonString(): String
    suspend fun getBalancesJsonString(): String
}

/**
 * Implementation of LocalAssetDataSource that reads from Android assets.
 */
class LocalAssetDataSourceImpl @Inject constructor(
    @ApplicationContext private val context: Context // Use @ApplicationContext for Hilt
) : LocalAssetDataSource {

    private suspend fun readJsonFromAssets(fileName: String): String {
        // Perform file IO on IO dispatcher
        // Ensure this is called from a context that switches dispatchers if needed (e.g., Repository)
        return withContext(Dispatchers.IO) { 
            try {
                context.assets.open(fileName).bufferedReader().use {
                    it.readText()
                }
            } catch (e: IOException) {
                // Consider more specific error handling or logging
                Log.e("LocalAssertDataSource", "Error reading $fileName from assets: ${e.message}")
                throw IOException("Error reading $fileName from assets", e)
            }
        }
    }

    override suspend fun getCurrenciesJsonString(): String {
        return readJsonFromAssets("currencies.json")
    }

    override suspend fun getRatesJsonString(): String {
        return readJsonFromAssets("rates.json")
    }

    override suspend fun getBalancesJsonString(): String {
        return readJsonFromAssets("balances.json")
    }
} 