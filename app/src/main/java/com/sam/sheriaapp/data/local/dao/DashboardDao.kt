package com.sam.sheriaapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sam.sheriaapp.data.local.entity.DashboardEntity

@Dao
interface DashboardDao {
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertDashboardData(dashboard: DashboardEntity)

    @Query("SELECT * FROM dashboard LIMIT 1")
    suspend fun getDashboardData(): DashboardEntity?

}