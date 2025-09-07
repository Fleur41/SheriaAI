package com.sam.sheriaapp.data.remote.model

import com.sam.sheriaapp.data.DateAdapter
import java.util.Date

data class BillingDto (
    val id: Int,
    val planName: String,
    val planStatus: String,
    val amount: Double,
    val currency: String,
    val billingDate: Date,
    val paymentMethod: String,
    val cardLastFour: String,
    val expiryDate: String
)