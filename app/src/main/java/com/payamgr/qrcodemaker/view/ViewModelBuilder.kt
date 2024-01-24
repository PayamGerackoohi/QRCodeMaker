package com.payamgr.qrcodemaker.view

import androidx.compose.runtime.Composable
import com.payamgr.qrcodemaker.view.page.content_form.ContentFormVM
import com.payamgr.qrcodemaker.view.page.content_type.ContentTypeVM
import com.payamgr.qrcodemaker.view.page.home.HomeVM
import com.payamgr.qrcodemaker.view.page.show_qrcode.ShowQrCodeVM

interface ViewModelBuilder {
    val home: @Composable () -> HomeVM
    val contentType: @Composable () -> ContentTypeVM
    val contentForm: @Composable () -> ContentFormVM
    val showQrCode: @Composable () -> ShowQrCodeVM
}
