package com.sam.sheriaapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sam.sheriaapp.domain.model.Dashboard
import com.sam.sheriaapp.domain.repository.DashboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch



@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: DashboardRepository
) : ViewModel() {
    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val dashboardData = repository.getDashboardData()

                _state.value = DashboardState(
                    totalLawFirms = dashboardData.totalLawFirms,
                    researchRequests = dashboardData.researchRequests,
                    activeUsers = dashboardData.activeUsers,
                    revenue = dashboardData.revenue,
                    recentActivities = dashboardData.recentActivities,
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                _state.value = DashboardState(
                    isLoading = false,
                    error = e.message ?: "Failed to load dashboard data"
                )
            }
            // Map from 'Dashboard' (domain) to 'DashboardState'
//            val domainDashboard: Dashboard = repository.getDashboardData()
//            val uiState = DashboardState(
//                totalLawFirms = domainDashboard.totalLawFirms,
//                researchRequests = domainDashboard.researchRequests,
//                activeUsers = domainDashboard.activeUsers,
//                revenue = domainDashboard.revenue, // Example: convert Double to Int if needed
//                recentActivities = domainDashboard.recentActivities
//            )
//            _state.value = uiState
        }

    }
}
data class DashboardState(
    val totalLawFirms: Int = 125,
    val researchRequests: Int = 780,
    val activeUsers: Int = 350,
    val revenue: Int = 15_000,
    val recentActivities: List<Dashboard.RecentActivity> = listOf(
        Dashboard.RecentActivity(
            type = Dashboard.ActivityType.NEW_LAW_FIRM,
            description = "Law Firm A"
        ),
        Dashboard.RecentActivity(
            type = Dashboard.ActivityType.NEW_USER,
            description = "User B"
        ),
        Dashboard.RecentActivity(
            type = Dashboard.ActivityType.RESEARCH_REQUEST,
            description = "Law Firm C"
        )
    ),
    val isLoading: Boolean = true,
    val error: String? = null
)
