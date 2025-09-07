package com.sam.sheriaapp.di

import android.content.Context
import androidx.room.Room
import com.sam.sheriaapp.data.local.MIGRATION_1_2
import com.sam.sheriaapp.data.local.dao.DashboardDao
import com.sam.sheriaapp.data.local.SheriaDatabase
import com.sam.sheriaapp.data.local.dao.AccountDao
import com.sam.sheriaapp.data.remote.SheriaApiService
import com.sam.sheriaapp.data.remote.adapters.DateAdapter
import com.sam.sheriaapp.domain.repository.AccountRepository
import com.sam.sheriaapp.domain.repository.AccountRepositoryImpl
import com.sam.sheriaapp.domain.repository.BillingRepository
import com.sam.sheriaapp.domain.repository.BillingRepositoryImpl
import com.sam.sheriaapp.domain.repository.DashboardRepository
import com.sam.sheriaapp.domain.repository.DashboardRepositoryImpl
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    @Provides
    @Singleton
    fun provideSheriaDatabase(@ApplicationContext context: Context): SheriaDatabase {
        return Room.databaseBuilder(context, SheriaDatabase::class.java, "sheria_db")
            .addMigrations(MIGRATION_1_2)
            .fallbackToDestructiveMigration(false)
            .build()
    }
    @Provides
    @Singleton
    fun provideDashboardDao(database: SheriaDatabase) = database.dashboardDao()

    @Provides
    @Singleton
    fun  provideMoshi(): Moshi{
        return Moshi.Builder()
            .add(DateAdapter())
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    fun provideSheriaApiService(moshi: Moshi): SheriaApiService {
        val baseUrl = "https://api.sheria.ai/"
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(SheriaApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideDashBoardRepository(
        api: SheriaApiService,
        dao: DashboardDao
    ): DashboardRepository {
        return DashboardRepositoryImpl(api, dao)
    }

    @Provides
    @Singleton
    fun provideAccountDao(database: SheriaDatabase) : AccountDao  {
        return database.accountDao()
    }

    @Provides
    @Singleton
    fun provideAccountRepository(
        api: SheriaApiService,
        dao: AccountDao
    ): AccountRepository {
        return AccountRepositoryImpl(api, dao)
    }

    @Provides
    @Singleton
    fun provideBillingRepository(
        api: SheriaApiService,
        database: SheriaDatabase
    ): BillingRepository {
        return BillingRepositoryImpl(api, database)
    }

    @Provides
    @Singleton
    @Suppress("Unused")
    fun provideDatabaseInitializer(database: SheriaDatabase): DatabaseInitializer {
        return DatabaseInitializer(database)
    }
}

class DatabaseInitializer(private val database: SheriaDatabase) {
    init {
        // This will be called when the DatabaseInitializer is created
        // You could add sample data initialization here if needed
    }

}