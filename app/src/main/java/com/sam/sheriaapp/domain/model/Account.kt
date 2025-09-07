package com.sam.sheriaapp.domain.model

import java.time.LocalDateTime

sealed class AccountType {
    object LegalProfessional : AccountType()
    object LawFirmAdmin : AccountType()
    object IndividualUser : AccountType()
}

data class Account(
    val id: String,
    val type: AccountType,
    val name: String,
    val email: String,
    val phone: String,
    val address: String,
    val registrationDate: LocalDateTime,
    val profileImageUri: String? = null,
    val subscription: Subscription,
    val activities: List<Activity> = emptyList(),
    val financialData: FinancialData? = null
)

data class Subscription(
    val plan: String,
    val status: String,
    val lastPayment: LocalDateTime? = null
)

data class FinancialData(
    val totalSpent: Double,
    val avgSpendingPerUser: Double? = null,
    val spendingDistribution: SpendingDistribution? = null,
    val growthPercentage: Double? = null
)

data class SpendingDistribution(
    val totalAmount: Double,
    val period: String,
    val userBreakdown: Map<String, Double>
)

data class Activity(
    val description: String,
    val timestamp: LocalDateTime
)

// Sample data
object SampleAccounts {
    val lucasBennett = Account(
        id = "user_001",
        type = AccountType.LegalProfessional,
        name = "Lucas Bennett",
        email = "lucas.bennett@example.com",
        phone = "+1 (555) 123-4567",
        address = "123 Main Street, Anytown, USA",
        registrationDate = LocalDateTime.of(2023, 1, 15, 0, 0),
        subscription = Subscription("Premium", "Active"),
        activities = listOf(
            Activity("Document Accessed", LocalDateTime.of(2024, 7, 28, 10, 30)),
            Activity("Search Query Executed", LocalDateTime.of(2024, 7, 25, 15, 45)),
            Activity("Account Login", LocalDateTime.of(2024, 7, 24, 9, 15))
        )
    )

    val ethanCarter = Account(
        id = "user_002",
        type = AccountType.LawFirmAdmin,
        name = "Ethan Carter",
        email = "ethan.carter@example.com",
        phone = "+1 (555) 123-4567",
        address = "123 Main Street, Anytown, USA",
        registrationDate = LocalDateTime.of(2023, 1, 15, 0, 0),
        subscription = Subscription("Premium", "Active", LocalDateTime.of(2024, 7, 20, 0, 0)),
        financialData = FinancialData(
            totalSpent = 1250.0,
            avgSpendingPerUser = 416.67,
            spendingDistribution = SpendingDistribution(
                totalAmount = 1250.0,
                period = "Last 3 Months",
                userBreakdown = mapOf(
                    "User 1" to 450.0,
                    "User 2" to 400.0,
                    "User 3" to 400.0
                )
            ),
            growthPercentage = 15.0
        ),
        activities = listOf(
            Activity("Document Accessed", LocalDateTime.of(2024, 7, 26, 10, 30)),
            Activity("Search Query Executed", LocalDateTime.of(2024, 7, 25, 15, 45)),
            Activity("Account Login", LocalDateTime.of(2024, 7, 24, 9, 15))
        )
    )
}