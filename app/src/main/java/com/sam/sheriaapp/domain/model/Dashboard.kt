package com.sam.sheriaapp.domain.model


import com.sam.sheriaapp.data.local.entity.DashboardEntity
import java.time.LocalDateTime

data class Dashboard(
    val totalLawFirms: Int = 125,
    val researchRequests: Int = 780,
    val activeUsers: Int = 350,
    val revenue: Int = 15000,
    val recentActivities: List<RecentActivity> = defaultRecentActivities(),
    val lastUpdated: LocalDateTime = LocalDateTime.now()
) {
    fun toEntity() = DashboardEntity(
        totalLawFirms = totalLawFirms,
        researchRequests = researchRequests,
        activeUsers = activeUsers,
        revenue = revenue,
        lastUpdated = lastUpdated
    )

    data class RecentActivity(
        val description: String,
        val type: ActivityType,
        val timestamp: LocalDateTime = LocalDateTime.now()
    ) {
        fun getDisplayText(): String {
            return when (type) {
                ActivityType.NEW_LAW_FIRM -> "+ New Law Firm Added"
                ActivityType.NEW_USER -> "+ New User Added"
                ActivityType.RESEARCH_REQUEST -> "☐ Research Request Received"
            }
        }
    }

    enum class ActivityType {
        NEW_LAW_FIRM,
        NEW_USER,
        RESEARCH_REQUEST
    }

    companion object {
        private fun defaultRecentActivities() = listOf(
            RecentActivity(
                description = "Law Firm A",
                type = ActivityType.NEW_LAW_FIRM
            ),
            RecentActivity(
                description = "User B",
                type = ActivityType.NEW_USER
            ),
            RecentActivity(
                description = "Law Firm C",
                type = ActivityType.RESEARCH_REQUEST
            )
        )
    }
}
//import com.sam.sheriaapp.data.local.entity.DashboardEntity
//import java.time.LocalDateTime
//data class Dashboard(
//    val totalLawFirms: Int = 125,
//    val researchRequests: Int = 780,
//    val activeUsers: Int = 350,
//    val revenue: Int = 15000,
//    val recentActivities: List<RecentActivity> = listOf(
//        RecentActivity(
//            description = "Law Firm A",
//            type = RecentActivity.ActivityType.NEW_LAW_FIRM,
//            timestamp = LocalDateTime.now().minusDays(1)
//        ),
//        RecentActivity(
//            description = "User B",
//            type = RecentActivity.ActivityType.NEW_USER,
//            timestamp = LocalDateTime.now().minusHours(3)
//        ),
//        RecentActivity(
//            description = "Law Firm C",
//            type = RecentActivity.ActivityType.RESEARCH_REQUEST,
//            timestamp = LocalDateTime.now().minusHours(1)
//        )
//    ),
//    val lastUpdated: LocalDateTime = LocalDateTime.now()
//) {
//    fun toEntity() = DashboardEntity(
//        totalLawFirms = totalLawFirms,
//        researchRequests = researchRequests,
//        activeUsers = activeUsers,
//        revenue = revenue,
//        lastUpdated = lastUpdated
//    )
//}
//
//data class RecentActivity(
//    val description: String,
//    val type: ActivityType,
//    val timestamp: LocalDateTime = LocalDateTime.now()
//) {
//    enum class ActivityType {
//        NEW_LAW_FIRM,
//        NEW_USER,
//        RESEARCH_REQUEST;
//
//        fun getDisplayText(): String {
//            return when (this) {
//                NEW_LAW_FIRM -> "+ New Law Firm Added"
//                NEW_USER -> "+ New User Added"
//                RESEARCH_REQUEST -> "☐ Research Request Received"
//            }
//        }
//
//        fun getIcon(): IconType {
//            return when (this) {
//                NEW_LAW_FIRM -> IconType.PERSON_ADD  //IconType.BUSINESS
//                NEW_USER -> IconType.PERSON_ADD
//                RESEARCH_REQUEST -> IconType.ASSIGNMENT
//            }
//        }
//    }
//}

enum class IconType {
    BUSINESS,
    PERSON_ADD,
    ASSIGNMENT
}
//data class Dashboard(
//    val totalLawFirms: Int = 0,
//    val researchRequests: Int = 0,
//    val activeUsers: Int = 0,
//    val revenue: Int = 0,
//    val recentActivities: List<RecentActivity> = emptyList()
//){
//    fun toEntity() = DashboardEntity(
//        totalLawFirms = totalLawFirms,
//        researchRequests = researchRequests,
//        activeUsers = activeUsers,
//        revenue = revenue
//    )
//}
//data class RecentActivity(
//    val description: String,
//    val type: ActivityType
//){
//    enum class ActivityType {
//        NEW_LAW_FIRM, NEW_USER, RESEARCH_REQUEST
//    }
//}

