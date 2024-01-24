package com.payamgr.qrcodemaker.test_util

import androidx.compose.runtime.Composable
import com.payamgr.qrcodemaker.data.NativeQrCodeMakerImpl
import com.payamgr.qrcodemaker.data.database.entity.MeCardContent
import com.payamgr.qrcodemaker.data.database.entity.PhoneCallContent
import com.payamgr.qrcodemaker.data.database.entity.TextContent
import com.payamgr.qrcodemaker.data.model.ErrorCorrectionCodeLevel
import com.payamgr.qrcodemaker.data.model.QrCode
import com.payamgr.qrcodemaker.data.model.QrCodeType
import com.payamgr.qrcodemaker.data.model.state.ContentFormState
import com.payamgr.qrcodemaker.data.model.state.ContentTypeState
import com.payamgr.qrcodemaker.data.model.state.HomeState
import com.payamgr.qrcodemaker.data.model.state.ShowQrCodeState
import com.payamgr.qrcodemaker.view.page.content_form.ContentFormVMImpl
import com.payamgr.qrcodemaker.view.page.content_type.ContentTypeVMImpl
import com.payamgr.qrcodemaker.view.page.home.HomeVMImpl
import com.payamgr.qrcodemaker.view.page.show_qrcode.ShowQrCodeVMImpl
import kotlinx.coroutines.flow.MutableStateFlow

typealias C = com.payamgr.qrcodemaker.data.model.Content
typealias CR = com.payamgr.qrcodemaker.data.repository.ContentRepository
typealias ECC = ErrorCorrectionCodeLevel
typealias VMB = com.payamgr.qrcodemaker.view.ViewModelBuilder
typealias QCM = com.payamgr.qrcodemaker.data.QrCodeMaker

object Fake {
    object Data {
        const val TITLE = "Title"
        const val TITLE2 = "Title2"
        const val BODY = "Body"
        const val BODY2 = "Body2"
        const val LABEL = "Label"
        const val OK = "OK"
        const val YES = "Yes"
        const val NO = "No"
        const val TEXT = "Text-txeT"
        const val TEXT2 = "Text2"
        const val LONG_TEXT = "aaa\nbbb\nccc"
        const val FIRST_NAME = "Payam"
        const val LAST_NAME = "Gerackoohi"
        const val PHONE = "+98 905 738 0865"
        val QR_CODE_2x2 = QrCode(2, identityBooleanMatrixOf(2))
        val QR_CODE_REAL: QrCode = NativeQrCodeMakerImpl.text(LONG_TEXT, ErrorCorrectionCodeLevel.High.ordinal)

        @Suppress("SameParameterValue")
        private fun identityBooleanMatrixOf(size: Int): BooleanArray = buildList {
            repeat(size) { i -> repeat(size) { j -> add(i == j) } }
        }.toBooleanArray()
    }

    object Content {
        val text = TextContent("Sample Text", Data.TEXT)
        val realisticText = TextContent(Data.TITLE, Data.LONG_TEXT)
        val phone = PhoneCallContent("Sample Phone Call", Data.PHONE)
        val meCard = MeCardContent("Sample Me-Card", Data.FIRST_NAME, Data.LAST_NAME, Data.PHONE)
    }

    open class ContentRepository : CR {
        override val currentContent = MutableStateFlow<C?>(null)
        override val currentQrCodeType = MutableStateFlow<QrCodeType?>(null)
        override suspend fun loadContents() = MutableStateFlow<List<C>>(listOf())
        override suspend fun push(content: C) {}
        override suspend fun push(type: QrCodeType) {}
        override suspend fun add(content: C) {}
        override suspend fun update(content: C) {}
        override suspend fun removeCurrentContent() {}
    }

    object QrCodeMaker : QCM {
        override suspend fun encode(content: C, errorCorrectionCodeLevel: ECC) = Data.QR_CODE_2x2
    }

    class ViewModelBuilder(
        repository: CR = ContentRepository(),
        homeState: HomeState = HomeState(),
        contentTypeState: ContentTypeState = ContentTypeState(),
        contentFormState: ContentFormState = ContentFormState(),
        showQrCodeState: ShowQrCodeState = ShowQrCodeState(),
    ) : VMB {
        override val home = @Composable { HomeVMImpl(homeState, repository) }
        override val contentType = @Composable { ContentTypeVMImpl(contentTypeState, repository) }
        override val contentForm = @Composable { ContentFormVMImpl(contentFormState, repository) }
        override val showQrCode = @Composable { ShowQrCodeVMImpl(showQrCodeState, repository, QrCodeMaker) }
    }
}
