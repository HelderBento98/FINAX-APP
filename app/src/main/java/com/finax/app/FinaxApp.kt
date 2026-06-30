package com.finax.app

import android.app.Application
import com.finax.app.billing.BillingManager

class FinaxApp : Application() {

    lateinit var billingManager: BillingManager
        private set

    override fun onCreate() {
        super.onCreate()
        billingManager = BillingManager(this)
        billingManager.connect()
    }
}
