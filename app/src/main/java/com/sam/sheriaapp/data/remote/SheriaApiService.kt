package com.sam.sheriaapp.data.remote

import com.sam.sheriaapp.data.remote.model.AccountDto
import com.sam.sheriaapp.data.remote.model.BillingDto
import com.sam.sheriaapp.data.remote.model.DashboardDto
import com.sam.sheriaapp.data.remote.model.UpdateProfileImageRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface SheriaApiService {
    @GET("dashboard")
    suspend fun getDashboardData(): DashboardDto

    @GET("accounts")
    suspend fun  getAllAccounts(): List<AccountDto>

    @GET("accounts/{id}")
    suspend fun getAccountById(@Path("id") accountId: String): AccountDto

    @PUT("accounts/{id}/profile-image")
    suspend fun updateProfileImage(
        @Path("id") accountId: String,
        @Body request: UpdateProfileImageRequest
    )

    // Add billing endpoints
    @GET("billing/history")
    suspend fun getBillingHistory(): List<BillingDto>

    @GET("billing/current-plan")
    suspend fun getCurrentPlan(): BillingDto

    @GET("billing/payment-method")
    suspend fun getPaymentMethod(): String


    companion object{
        const val BASE_URL = "https://api.sheria.ai/"

    }

}