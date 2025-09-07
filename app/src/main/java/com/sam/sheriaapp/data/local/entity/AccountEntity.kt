package com.sam.sheriaapp.data.local.entity

import android.R.attr.name
import android.R.attr.type


import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey val id: String,
    val type: String,
    val name: String,
    val email: String,
    val phone: String,
    val address: String,
    val registrationDate: String,
    val profileImageUri: String? = null,
    val subscriptionPlan: String,
    val subscriptionStatus: String,
    val lastPayment: String? = null,
    val totalSpent: Double = 0.0,
    val avgSpendingPerUser: Double = 0.0
) {
    fun toDomain(): com.sam.sheriaapp.domain.model.Account {
        return com.sam.sheriaapp.domain.model.Account(
            id = id,
            type = when (type) {
                "LEGAL_PROFESSIONAL" -> com.sam.sheriaapp.domain.model.AccountType.LegalProfessional
                "LAW_FIRM_ADMIN" -> com.sam.sheriaapp.domain.model.AccountType.LawFirmAdmin
                else -> com.sam.sheriaapp.domain.model.AccountType.IndividualUser
            },
            name = name,
            email = email,
            phone = phone,
            address = address,
            registrationDate = LocalDateTime.parse(registrationDate),
            profileImageUri = profileImageUri,
            subscription = com.sam.sheriaapp.domain.model.Subscription(
                plan = subscriptionPlan,
                status = subscriptionStatus,
                lastPayment = lastPayment?.let { LocalDateTime.parse(it) }
            ),
            financialData = if (totalSpent > 0) com.sam.sheriaapp.domain.model.FinancialData(
                totalSpent = totalSpent,
                avgSpendingPerUser = avgSpendingPerUser,
                spendingDistribution = null,
                growthPercentage = null
            ) else null,
            activities = emptyList()
        )
    }
}

fun com.sam.sheriaapp.domain.model.Account.toEntity(): AccountEntity {
    return AccountEntity(
        id = id,
        type = when (type) {
            com.sam.sheriaapp.domain.model.AccountType.LegalProfessional -> "LEGAL_PROFESSIONAL"
            com.sam.sheriaapp.domain.model.AccountType.LawFirmAdmin -> "LAW_FIRM_ADMIN"
            com.sam.sheriaapp.domain.model.AccountType.IndividualUser -> "INDIVIDUAL_USER"
        },
        name = name,
        email = email,
        phone = phone,
        address = address,
        registrationDate = registrationDate.toString(),
        profileImageUri = profileImageUri,
        subscriptionPlan = subscription.plan,
        subscriptionStatus = subscription.status,
        lastPayment = subscription.lastPayment?.toString(),
        totalSpent = financialData?.totalSpent ?: 0.0,
        avgSpendingPerUser = financialData?.avgSpendingPerUser ?: 0.0
    )
}