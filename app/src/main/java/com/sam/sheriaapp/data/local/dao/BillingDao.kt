package com.sam.sheriaapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sam.sheriaapp.data.local.entity.BillingEntity


@Dao
interface BillingDao {
    @Query("SELECT * FROM billing ORDER BY billingDate DESC")
    suspend fun getBillingHistory(): List<BillingEntity>

    @Query("SELECT * FROM billing WHERE planStatus LIKE '%Active%' LIMIT 1")
    suspend fun getCurrentPlan(): BillingEntity?

    @Query("SELECT paymentMethod FROM billing LIMIT 1")
    suspend fun getPaymentMethod(): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBilling(billing: BillingEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllBilling(billingList: List<BillingEntity>)


}