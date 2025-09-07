package com.sam.sheriaapp.domain.repository

import com.sam.sheriaapp.data.local.SheriaDatabase
import com.sam.sheriaapp.data.mapper.toBilling
import com.sam.sheriaapp.data.remote.SheriaApiService
import com.sam.sheriaapp.domain.model.Billing
import java.util.Date // ← Use java.util.Date, not com.google.type.Date
import javax.inject.Inject

interface BillingRepository {
    suspend fun getBillingHistory(): List<Billing>
    suspend fun getCurrentPlan(): Billing
    suspend fun getPaymentMethod(): String
}

class BillingRepositoryImpl @Inject constructor(
    private val apiService: SheriaApiService,
    private val database: SheriaDatabase
) : BillingRepository {

    override suspend fun getBillingHistory(): List<Billing> {
        return try {
            // First try to get from network
            val response = apiService.getBillingHistory()
            response.map { it.toBilling() }
        } catch (e: Exception) {
            // Fall back to local database
            val localData = database.billingDao().getBillingHistory()
            if (localData.isNotEmpty()) {
                localData.map { it.toBilling() }
            } else {
                // Provide sample data if both network and local are empty
                getSampleBillingHistory()
            }
        }
    }

    override suspend fun getCurrentPlan(): Billing {
        return try {
            val response = apiService.getCurrentPlan()
            response.toBilling()
        } catch (e: Exception) {
            try {
                val currentPlan = database.billingDao().getCurrentPlan()
                currentPlan?.toBilling() ?: getSampleCurrentPlan()
            } catch (dbException: Exception) {
                // Provide sample current plan if both fail
                getSampleCurrentPlan()
            }
        }
    }

    override suspend fun getPaymentMethod(): String {
        return try {
            apiService.getPaymentMethod()
        } catch (e: Exception) {
            try {
                database.billingDao().getPaymentMethod() ?: "Visa **** 4242 (Expires 08/2025)"
            } catch (dbException: Exception) {
                // Provide sample payment method if both fail
                "Visa **** 4242 (Expires 08/2025)"
            }
        }
    }

    // Sample data methods
    private fun getSampleCurrentPlan(): Billing {
        return Billing(
            id = 1,
            planName = "Premium",
            planStatus = "Active",
            amount = 99.0,
            currency = "USD",
            billingDate = Date(), // Use java.util.Date
            paymentMethod = "Visa",
            cardLastFour = "4242",
            expiryDate = "08/2025"
        )
    }

    private fun getSampleBillingHistory(): List<Billing> {
        return listOf(
            Billing(
                id = 1,
                planName = "Premium",
                planStatus = "Active",
                amount = 99.0,
                currency = "USD",
                billingDate = Date(System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000), // 1 month ago
                paymentMethod = "Visa",
                cardLastFour = "4242",
                expiryDate = "08/2025"
            ),
            Billing(
                id = 2,
                planName = "Premium",
                planStatus = "Active",
                amount = 99.0,
                currency = "USD",
                billingDate = Date(System.currentTimeMillis() - 60L * 24 * 60 * 60 * 1000), // 2 months ago
                paymentMethod = "Visa",
                cardLastFour = "4242",
                expiryDate = "08/2025"
            ),
            Billing(
                id = 3,
                planName = "Premium",
                planStatus = "Active",
                amount = 99.0,
                currency = "USD",
                billingDate = Date(System.currentTimeMillis() - 90L * 24 * 60 * 60 * 1000), // 3 months ago
                paymentMethod = "Visa",
                cardLastFour = "4242",
                expiryDate = "08/2025"
            )
        )
    }
}

//interface  BillingRepository{
//    suspend fun getBillingHistory(): List<Billing>
//    suspend fun getCurrentPlan(): Billing
//    suspend fun getPaymentMethod(): String
//}
//class BillingRepositoryImpl @Inject constructor(
//    private val api: SheriaApiService,
//    private val database: SheriaDatabase
//): BillingRepository {
//    override suspend fun getBillingHistory(): List<Billing> {
//        return  try {
//            val response = api.getBillingHistory()
//            response.map { it.toBilling() }
//        } catch (e: Exception){
//            database.billingDao().getBillingHistory().map { it.toBilling() }
//        }
//    }
//
//    override suspend fun getCurrentPlan(): Billing {
//        return try {
//            val response = api.getCurrentPlan()
//            response.toBilling()
//        } catch (e: Exception){
//            database.billingDao().getCurrentPlan().toBilling()
//        }
//    }
//
//    override suspend fun getPaymentMethod(): String {
//         return try {
//            api.getPaymentMethod()
//        } catch (e: Exception){
//            database.billingDao().getPaymentMethod()
//        }
//    }
