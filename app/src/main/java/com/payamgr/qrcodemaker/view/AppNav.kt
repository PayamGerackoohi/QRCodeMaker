package com.payamgr.qrcodemaker.view

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.payamgr.qrcodemaker.view.page.content_type.ContentType.contentTypePage
import com.payamgr.qrcodemaker.view.page.content_type.ContentType.navigateToContentType
import com.payamgr.qrcodemaker.view.page.home.Home
import com.payamgr.qrcodemaker.view.page.home.Home.homePage
import com.payamgr.qrcodemaker.view.page.content_form.ContentForm.contentFormPage
import com.payamgr.qrcodemaker.view.page.content_form.ContentForm.navigateToContentForm
import com.payamgr.qrcodemaker.view.page.show_qrcode.ShowQrCode.navigateToShowQrCode
import com.payamgr.qrcodemaker.view.page.show_qrcode.ShowQrCode.showQrCodePage

object AppNav {
    @Composable
    fun Host(navController: NavHostController, viewModelBuilder: ViewModelBuilder = ViewModelBuilderImpl) {
        navController.apply {
            NavHost(navController = this, startDestination = Home.Route) {
                homePage(
                    viewModelBuilder = viewModelBuilder.home,
                    navigateToContentType = { navigateToContentType() },
                    navigateToShowQrCode = { navigateToShowQrCode() },
                )
                contentTypePage(
                    viewModelBuilder = viewModelBuilder.contentType,
                    navigateToContentForm = { navigateToContentForm(false) },
                )
                contentFormPage(
                    viewModelBuilder = viewModelBuilder.contentForm,
                    onClose = { isEditMode ->
                        if (isEditMode)
                            popBackStack()
                        else
                            popBackStack(Home.Route, false)
                    }
                )
                showQrCodePage(
                    viewModelBuilder = viewModelBuilder.showQrCode,
                    onClose = { popBackStack() },
                    navigateToContentForm = { navigateToContentForm(true) },
                )
            }
        }
    }
}
