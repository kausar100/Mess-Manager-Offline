package com.misl.messmanager.di

import android.content.Context
import androidx.room.Room
import com.misl.messmanager.data.local.MessDatabase
import com.misl.messmanager.data.local.dao.MessDao
import com.misl.messmanager.data.repository.MessRepositoryImpl
import com.misl.messmanager.domain.repository.MessRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideMessDatabase(@ApplicationContext context: Context): MessDatabase {
        return Room.databaseBuilder(
            context, MessDatabase::class.java, "mess_manager.db"
        ).fallbackToDestructiveMigration() // Destroys and rebuilds the database on version change
            .build()
    }

    @Provides
    @Singleton
    fun provideMessDao(database: MessDatabase): MessDao {
        return database.messDao()
    }

    @Provides
    @Singleton
    fun provideMessRepository(dao: MessDao): MessRepository {
        return MessRepositoryImpl(dao)
    }
}