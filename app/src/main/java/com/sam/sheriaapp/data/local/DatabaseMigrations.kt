package com.sam.sheriaapp.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Create accounts table
        database.execSQL("""
            CREATE TABLE accounts (
                id TEXT PRIMARY KEY NOT NULL,
                type TEXT NOT NULL,
                name TEXT NOT NULL,
                email TEXT NOT NULL,
                phone TEXT NOT NULL,
                address TEXT NOT NULL,
                registrationDate TEXT NOT NULL,
                profileImageUri TEXT,
                subscriptionPlan TEXT NOT NULL,
                subscriptionStatus TEXT NOT NULL,
                lastPayment TEXT,
                totalSpent REAL NOT NULL DEFAULT 0,
                avgSpendingPerUser REAL NOT NULL DEFAULT 0
            )
        """)
    }
}