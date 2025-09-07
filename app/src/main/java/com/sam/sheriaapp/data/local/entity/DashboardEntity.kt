package com.sam.sheriaapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sam.sheriaapp.domain.model.Dashboard
import java.time.LocalDateTime

@Entity(tableName = "dashboard")
data class DashboardEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val totalLawFirms: Int,
    val researchRequests: Int,
    val activeUsers: Int,
    val revenue: Int,
    val lastUpdated: LocalDateTime = LocalDateTime.now()
){
    fun toDomain() = Dashboard(
        totalLawFirms = totalLawFirms,
        researchRequests = researchRequests,
        activeUsers = activeUsers,
        revenue = revenue
    )
}