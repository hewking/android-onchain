package com.cryptocom.wallet

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CryptoWalletApp : Application() {
    // Application-level setup can go here if needed
    override fun onCreate() {
        super.onCreate()
        // Initialization code here
    }
} 