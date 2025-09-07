package com.sam.sheriaapp.domain.repository

import com.sam.sheriaapp.data.local.dao.DashboardDao
import com.sam.sheriaapp.data.remote.SheriaApiService
import com.sam.sheriaapp.domain.model.Dashboard
import javax.inject.Inject

interface DashboardRepository{
    suspend fun getDashboardData(): Dashboard
}

class DashboardRepositoryImpl @Inject constructor(
    private val api: SheriaApiService,
    private val dao: DashboardDao
): DashboardRepository {
    override suspend fun getDashboardData(): Dashboard {
        return try {
        val remoteData = api.getDashboardData().toDomain()
        dao.insertDashboardData(remoteData.toEntity())
        remoteData
        } catch (e: Exception){
            dao.getDashboardData()?.toDomain() ?: Dashboard()
//            dao.getDashboardData()?.toDomain() ?: throw e
        }
    }
}