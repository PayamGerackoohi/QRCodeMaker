package com.payamgr.qrcodemaker.data.di

import android.app.Activity
import com.payamgr.qrcodemaker.data.NativeQrCodeMaker
import com.payamgr.qrcodemaker.data.NativeQrCodeMakerImpl
import com.payamgr.qrcodemaker.data.QrCodeMaker
import com.payamgr.qrcodemaker.data.QrCodeMakerImpl
import com.payamgr.qrcodemaker.data.controller.SplashScreenController
import com.payamgr.qrcodemaker.data.controller.SplashScreenControllerImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {
    @Binds
    abstract fun bindQrCodeMaker(impl: QrCodeMakerImpl): QrCodeMaker

    @Binds
    abstract fun splashScreenController(impl: SplashScreenControllerImpl): SplashScreenController

    companion object {
        @Provides
        fun provideNativeQrCodeMaker(): NativeQrCodeMaker = NativeQrCodeMakerImpl
    }
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface AppModulesProvider {
    fun splashScreenController(): SplashScreenController
}

fun Activity.provideAppModule() = EntryPoints.get(application, AppModulesProvider::class.java)
