package com.sam.sheriaapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sam.sheriaapp.data.local.dao.AccountDao
import com.sam.sheriaapp.data.local.dao.BillingDao
import com.sam.sheriaapp.data.local.dao.DashboardDao
import com.sam.sheriaapp.data.local.entity.AccountEntity
import com.sam.sheriaapp.data.local.entity.BillingEntity
import com.sam.sheriaapp.data.local.entity.DashboardEntity

@Database(entities = [DashboardEntity::class, AccountEntity::class, BillingEntity::class], version = 3, exportSchema = false)
@TypeConverters(DateTimeConverters::class, DateConverters::class)
abstract class SheriaDatabase : RoomDatabase() {
    abstract fun dashboardDao(): DashboardDao
    abstract fun accountDao(): AccountDao
    abstract  fun billingDao(): BillingDao

}