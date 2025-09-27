package com.sam.sheriaapp.domain.repository

import com.sam.sheriaapp.data.local.dao.AccountDao
import com.sam.sheriaapp.data.local.entity.toEntity
import com.sam.sheriaapp.data.remote.SheriaApiService
import com.sam.sheriaapp.data.remote.model.UpdateProfileImageRequest
import com.sam.sheriaapp.domain.model.Account
import javax.inject.Inject

interface AccountRepository {
    suspend fun getAllAccounts(): List<Account>
    suspend fun getAccountById(accountId: String): Account?
    suspend fun updateProfileImage(accountId: String, imageData: String)  //imageUri: String
    suspend fun updateAccount(account: Account)
    suspend fun deleteAccount(accountId: String)
}
class AccountRepositoryImpl @Inject constructor(
    private val api: SheriaApiService,
    private val dao: AccountDao
) : AccountRepository {

    override suspend fun getAllAccounts(): List<Account> {
        return try {
            val remoteAccounts = api.getAllAccounts().map { it.toDomain() }
            // Cache the accounts
            remoteAccounts.forEach { account ->
                dao.insertAccount(account.toEntity())
            }
            remoteAccounts
        } catch (e: Exception) {
            // Fall back to cached data
            dao.getAllAccounts().map { it.toDomain() }
        }
    }

    override suspend fun getAccountById(accountId: String): Account? {
        return try {
            api.getAccountById(accountId).toDomain()
        } catch (e: Exception) {
            dao.getAccountById(accountId)?.toDomain()
        }
    }

    override suspend fun updateProfileImage(accountId: String, imageData: String) {
        try {
            api.updateProfileImage(accountId, UpdateProfileImageRequest(imageData))
            // Update local cache
            dao.updateProfileImage(accountId, imageData)
            println("DEBUG: ✅ Local update successful (API temporarily disabled)")
        } catch (e: Exception) {
            // Update local cache if network fails
            dao.updateProfileImage(accountId, imageData)
            println("DEBUG: ⚠️ API failed but local update succeeded: ${e.message}")
            //throw e // Re-throw to handle in ViewModel
        }
    }

    override suspend fun updateAccount(account: Account) {
        try {
            // For simplicity, we'll just update local cache
            // In real app, you'd have API endpoint for this
            dao.insertAccount(account.toEntity())
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun deleteAccount(accountId: String) {
        try {
            // Would call API endpoint if available
            dao.deleteAccount(accountId)
        } catch (e: Exception) {
            throw e
        }
    }
}