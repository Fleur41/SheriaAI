package com.sam.sheriaapp.domain.model

import java.util.Date

data class Billing (
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