package com.payamgr.qrcodemaker.view

import androidx.compose.runtime.Composable
import com.airbnb.mvrx.compose.mavericksViewModel
import com.payamgr.qrcodemaker.view.page.content_form.ContentFormVM
import com.payamgr.qrcodemaker.view.page.content_type.ContentTypeVM
import com.payamgr.qrcodemaker.view.page.home.HomeVM
import com.payamgr.qrcodemaker.view.page.show_qrcode.ShowQrCodeVM

object ViewModelBuilderImpl : ViewModelBuilder {
    override val home: @Composable () -> HomeVM = { mavericksViewModel() }
    override val contentType: @Composable () -> ContentTypeVM = { mavericksViewModel() }
    override val contentForm: @Composable () -> ContentFormVM = { mavericksViewModel() }
    override val showQrCode: @Composable () -> ShowQrCodeVM = { mavericksViewModel() }
}
