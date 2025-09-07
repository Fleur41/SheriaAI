package com.sam.sheriaapp.data.mapper

import com.sam.sheriaapp.data.local.entity.BillingEntity
import com.sam.sheriaapp.data.remote.model.BillingDto
import com.sam.sheriaapp.domain.model.Billing


fun BillingDto.toBilling(): Billing {
    return Billing(
        id = id,
        planName = planName,
        planStatus = planStatus,
        amount = amount,
        currency = currency,
        //billingDate = convertGoogleDateToUtilDate(this.billingDate),
        billingDate = billingDate,
        paymentMethod = paymentMethod,
        cardLastFour = cardLastFour,
        expiryDate = expiryDate
    )
}

fun BillingEntity.toBilling(): Billing {
    return Billing(
        id = id,
        planName = planName,
        planStatus = planStatus,
        amount = amount,
        currency = currency,
        billingDate = billingDate,
        paymentMethod = paymentMethod,
        cardLastFour = cardLastFour,
        expiryDate = expiryDate
    )
}

fun Billing.toBillingEntity(): BillingEntity {
    return BillingEntity(
        id = id,
        planName = planName,
        planStatus = planStatus,
        amount = amount,
        currency = currency,
        billingDate = billingDate,
        paymentMethod = paymentMethod,
        cardLastFour = cardLastFour,
        expiryDate = expiryDate
    )
}
//fun BillingDto.toBilling(): Billing {
//    return Billing(
//        id = id,
//        planName = planName,
//        planStatus = planStatus,
//        amount = amount,
//        currency = currency,
//        billingDate = billingDate,
//        paymentMethod = paymentMethod,
//        cardLastFour = cardLastFour,
//        expiryDate = expiryDate
//    )
//}
//
//fun BillingEntity.toBilling(): Billing {
//    return Billing(
//        id = id,
//        planName = planName,
//        planStatus = planStatus,
//        amount = amount,
//        currency = currency,
//        billingDate = billingDate,
//        paymentMethod = paymentMethod,
//        cardLastFour = cardLastFour,
//        expiryDate = expiryDate
//    )
//}
//
//fun Billing.toBillingEntity(): BillingEntity {
//    return BillingEntity(
//        id = id,
//        planName = planName,
//        planStatus = planStatus,
//        amount = amount,
//        currency = currency,
//        billingDate = billingDate,
//        paymentMethod = paymentMethod,
//        cardLastFour = cardLastFour,
//        expiryDate = expiryDate
//    )
//}