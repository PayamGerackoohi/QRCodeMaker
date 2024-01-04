package com.payamgr.qrcodemaker.data.di

import com.payamgr.qrcodemaker.data.NativeQrCodeMaker
import com.payamgr.qrcodemaker.data.NativeQrCodeMakerImpl
import com.payamgr.qrcodemaker.data.QrCodeMaker
import com.payamgr.qrcodemaker.data.QrCodeMakerImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface AppModuleBinding {
    @Binds
    fun bindQrCodeMaker(impl: QrCodeMakerImpl): QrCodeMaker
}

@Module
@InstallIn(SingletonComponent::class)
object AppModuleProvision {
    @Provides
    fun provideNativeQrCodeMaker(): NativeQrCodeMaker = NativeQrCodeMakerImpl
}
