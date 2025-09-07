package com.sam.sheriaapp.data.remote.model

import com.sam.sheriaapp.domain.model.Account
import com.sam.sheriaapp.domain.model.AccountType
import com.sam.sheriaapp.domain.model.Activity
import com.sam.sheriaapp.domain.model.FinancialData
import com.sam.sheriaapp.domain.model.Subscription
import java.time.LocalDateTime

data class AccountDto(
    val id: String,
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
    val avgSpendingPerUser: Double = 0.0,
    val activities: List<ActivityDto> = emptyList()
) {
    fun toDomain(): Account {
        return Account(
            id = id,
            type = when (type) {
                "LEGAL_PROFESSIONAL" -> AccountType.LegalProfessional
                "LAW_FIRM_ADMIN" -> AccountType.LawFirmAdmin
                else -> AccountType.IndividualUser
            },
            name = name,
            email = email,
            phone = phone,
            address = address,
            registrationDate = LocalDateTime.parse(registrationDate),
            profileImageUri = profileImageUri,
            subscription = Subscription(
                plan = subscriptionPlan,
                status = subscriptionStatus,
                lastPayment = lastPayment?.let { LocalDateTime.parse(it) }
            ),
            financialData = if (totalSpent > 0) FinancialData(
                totalSpent = totalSpent,
                avgSpendingPerUser = if (avgSpendingPerUser > 0) avgSpendingPerUser else null,
                spendingDistribution = null,
                growthPercentage = null
            ) else null,
            activities = activities.map { it.toDomain() }
        )
    }
}

data class ActivityDto(
    val description: String,
    val timestamp: String
) {
    fun toDomain(): Activity {
        return Activity(
            description = description,
            timestamp = LocalDateTime.parse(timestamp)
        )
    }
}

data class UpdateProfileImageRequest(val imageUri: String)


//package com.sam.sheriaapp.data.remote.model
//
//import com.sam.sheriaapp.domain.model.Account
//import com.sam.sheriaapp.domain.model.AccountType
//import java.time.LocalDateTime
//
//data class AccountDto(
//    val id: String,
//    val type: String,
//    val name: String,
//    val email: String,
//    val phone: String,
//    val address: String,
//    val registrationDate: String,
//    val profileImageUri: String? = null,
//    val subscriptionPlan: String,
//    val subscriptionStatus: String,
//    val lastPayment: String? = null,
//    val totalSpent: Double? = null,
//    val avgSpendingPerUser: Double? = null,
//    val activities: List<ActivityDto> = emptyList()
//) {
//    fun toDomain() = Account(
//        id = id,
//        type = when (type) {
//            "LEGAL_PROFESSIONAL" -> AccountType.LegalProfessional
//            "LAW_FIRM_ADMIN" -> AccountType.LawFirmAdmin
//            else -> AccountType.IndividualUser
//        },
//        name = name,
//        email = email,
//        phone = phone,
//        address = address,
//        registrationDate = LocalDateTime.parse(registrationDate),
//        profileImageUri = profileImageUri,
//        subscription = com.sam.sheriaapp.domain.model.Subscription(
//            plan = subscriptionPlan,
//            status = subscriptionStatus,
//            lastPayment = lastPayment?.let { LocalDateTime.parse(it) }
//        ),
//        financialData = if (totalSpent != null) com.sam.sheriaapp.domain.model.FinancialData(
//            totalSpent = totalSpent,
//            avgSpendingPerUser = avgSpendingPerUser
//        ) else null,
//        activities = activities.map { it.toDomain() }
//    )
//}
//
//data class ActivityDto(
//    val description: String,
//    val timestamp: String
//) {
//    fun toDomain() = com.sam.sheriaapp.domain.model.Activity(
//        description = description,
//        timestamp = LocalDateTime.parse(timestamp)
//    )
//}