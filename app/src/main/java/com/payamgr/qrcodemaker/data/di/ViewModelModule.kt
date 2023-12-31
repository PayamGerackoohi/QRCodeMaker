package com.payamgr.qrcodemaker.data.di

import com.airbnb.mvrx.hilt.AssistedViewModelFactory
import com.airbnb.mvrx.hilt.MavericksViewModelComponent
import com.airbnb.mvrx.hilt.ViewModelKey
import com.payamgr.qrcodemaker.view.page.content_form.ContentFormVM
import com.payamgr.qrcodemaker.view.page.content_form.ContentFormVMImpl
import com.payamgr.qrcodemaker.view.page.content_type.ContentTypeVM
import com.payamgr.qrcodemaker.view.page.content_type.ContentTypeVMImpl
import com.payamgr.qrcodemaker.view.page.home.HomeVM
import com.payamgr.qrcodemaker.view.page.home.HomeVMImpl
import com.payamgr.qrcodemaker.view.page.show_qrcode.ShowQrCodeVM
import com.payamgr.qrcodemaker.view.page.show_qrcode.ShowQrCodeVMImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.multibindings.IntoMap

@Module
@InstallIn(MavericksViewModelComponent::class)
interface ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(HomeVM::class)
    fun bindHomeVM(factory: HomeVMImpl.Factory): AssistedViewModelFactory<*, *>

    @Binds
    @IntoMap
    @ViewModelKey(ContentFormVM::class)
    fun bindContentFormVM(factory: ContentFormVMImpl.Factory): AssistedViewModelFactory<*, *>

    @Binds
    @IntoMap
    @ViewModelKey(ContentTypeVM::class)
    fun bindContentTypeVM(factory: ContentTypeVMImpl.Factory): AssistedViewModelFactory<*, *>

    @Binds
    @IntoMap
    @ViewModelKey(ShowQrCodeVM::class)
    fun bindShowQrCodeVM(factory: ShowQrCodeVMImpl.Factory): AssistedViewModelFactory<*, *>
}
