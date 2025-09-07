package com.sam.sheriaapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.sam.sheriaapp.data.local.DateConverters
import java.util.Date

@Entity(tableName = "billing")
data class BillingEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val planName:  String,
    val planStatus: String,
    val amount: Double,
    val currency: String,
    val billingDate: Date,
    val paymentMethod: String,
    val cardLastFour: String,
    val expiryDate: String
)