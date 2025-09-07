package com.sam.sheriaapp.data.remote.model

import com.sam.sheriaapp.domain.model.Dashboard
import java.time.LocalDateTime

data class DashboardDto(
    val totalLawFirms: Int,
    val researchRequests: Int,
    val activeUsers: Int,
    val revenue: Int,
    val recentActivities: List<RecentActivityDto>,
    val lastUpdated: String? = null
) {
    fun toDomain() = Dashboard(
        totalLawFirms = totalLawFirms,
        researchRequests = researchRequests,
        activeUsers = activeUsers,
        revenue = revenue,
        recentActivities = recentActivities.map { it.toDomain() },
        lastUpdated = lastUpdated?.let { LocalDateTime.parse(it) } ?: LocalDateTime.now()
    )
}

data class RecentActivityDto(
    val type: String,
    val description: String,
    val timestamp: String? = null
) {
    fun toDomain() = Dashboard.RecentActivity(
        description = description,
        type = when (type.uppercase()) {
            "NEW_LAW_FIRM" -> Dashboard.ActivityType.NEW_LAW_FIRM
            "NEW_USER" -> Dashboard.ActivityType.NEW_USER
            "RESEARCH_REQUEST" -> Dashboard.ActivityType.RESEARCH_REQUEST
            else -> throw IllegalArgumentException("Unknown activity type: $type")
        },
        timestamp = timestamp?.let { LocalDateTime.parse(it) } ?: LocalDateTime.now()
    )
}

//data class DashboardDto(
//    val totalLawFirms: Int,
//    val researchRequests: Int,
//    val activeUsers: Int,
//    val revenue: Int,
//    val recentActivities: List<RecentActivityDto>
//) {
//    fun toDomain() = Dashboard(
//        totalLawFirms = totalLawFirms,
//        researchRequests = researchRequests,
//        activeUsers = activeUsers,
//        revenue = revenue,
//        recentActivities = recentActivities.map { it.toDomain() }
//    )
//}
//
//data class RecentActivityDto(
//    val type: String,
//    val description: String
//){
//    fun toDomain() = Dashboard.RecentActivity(
//        type = when (type) {
//            "NEW_LAW_FIRM" -> Dashboard.ActivityType.NEW_LAW_FIRM
//            "NEW_USER" -> Dashboard.ActivityType.NEW_USER
//            else -> Dashboard.ActivityType.RESEARCH_REQUEST
//        },
//        description = description
//
//    )
//}