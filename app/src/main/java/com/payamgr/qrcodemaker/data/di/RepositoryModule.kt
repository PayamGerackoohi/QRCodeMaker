package com.payamgr.qrcodemaker.data.di

import com.payamgr.qrcodemaker.data.repository.ContentRepository
import com.payamgr.qrcodemaker.data.repository.ContentRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {
    @Binds
    fun bindContentRepository(repository: ContentRepositoryImpl): ContentRepository
}
