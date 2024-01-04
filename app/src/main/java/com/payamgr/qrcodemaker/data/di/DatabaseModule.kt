package com.payamgr.qrcodemaker.data.di

import android.content.Context
import androidx.room.Room
import com.payamgr.qrcodemaker.data.database.QrDatabase
import com.payamgr.qrcodemaker.data.database.QrDatabaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): QrDatabase = Room
        .databaseBuilder(context, QrDatabaseImpl::class.java, "qr-code-maker.db")
        .build()
}
