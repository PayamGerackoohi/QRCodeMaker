package com.payamgr.qrcodemaker.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier

@Module
@InstallIn(SingletonComponent::class)
object CoroutineModule {
    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class Default

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class Main

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class IO

    @Provides
    @Default
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    @Provides
    @Main
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

    @Provides
    @IO
    fun provideIODispatcher(): CoroutineDispatcher = Dispatchers.IO
}
