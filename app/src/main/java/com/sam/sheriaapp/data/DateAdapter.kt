package com.sam.sheriaapp.data

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.util.Date

class DateAdapter {
    @ToJson
    fun toJson(date: Date): Long {
        return date.time
    }

    @FromJson
    fun fromJson(timestamp: Long): Date {
        return Date(timestamp)
    }
}