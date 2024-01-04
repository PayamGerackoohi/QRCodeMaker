package com.payamgr.qrcodemaker.test_util

import com.payamgr.qrcodemaker.data.NativeQrCodeMaker
import com.payamgr.qrcodemaker.data.database.QrDatabase
import com.payamgr.qrcodemaker.data.database.dao.MeCardContentDao
import com.payamgr.qrcodemaker.data.database.dao.PhoneCallContentDao
import com.payamgr.qrcodemaker.data.database.dao.TextContentDao
import com.payamgr.qrcodemaker.data.database.entity.MeCardContent
import com.payamgr.qrcodemaker.data.database.entity.PhoneCallContent
import com.payamgr.qrcodemaker.data.database.entity.TextContent
import com.payamgr.qrcodemaker.data.model.Content
import com.payamgr.qrcodemaker.data.model.QrCode
import com.payamgr.qrcodemaker.data.model.QrCodeType
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk

object Fake {
    object Data {
        const val TITLE = "Title"
        const val TITLE2 = "Title2"
        const val FIRST_NAME = "FirstName"
        const val LAST_NAME = "LastName"
        const val PHONE = "Phone"
        const val TEXT = "Text"
    }

    object Content {
        val text get() = Data.run { TextContent(TITLE, TEXT) }
        val phoneCall get() = Data.run { PhoneCallContent(TITLE, PHONE) }
        val meCard get() = Data.run { MeCardContent(TITLE, FIRST_NAME, LAST_NAME, PHONE) }
        val text2 get() = Data.run { TextContent(TITLE2, TEXT) }
        val phoneCall2 get() = Data.run { PhoneCallContent(TITLE2, PHONE) }
        val meCard2 get() = Data.run { MeCardContent(TITLE2, FIRST_NAME, LAST_NAME, PHONE) }
    }

    object Type {
        val text get() = QrCodeType.Text(Data.TEXT)
        val phoneCall get() = QrCodeType.PhoneCall(Data.PHONE)
        val meCard get() = Data.run { QrCodeType.MeCard(FIRST_NAME, LAST_NAME, PHONE) }
    }

    val mockDatabase: QrDatabase
        get() = mockk<QrDatabase>().also { db ->
            every { db.textContentDao() } returns
                    spyk(object : TextContentDao, DbHelper<TextContent>() {})
            every { db.phoneCallContentDao() } returns
                    spyk(object : PhoneCallContentDao, DbHelper<PhoneCallContent>() {})
            every { db.meCardContentDao() } returns
                    spyk(object : MeCardContentDao, DbHelper<MeCardContent>() {})
        }

    object QR {
        val text = QrCode(1, booleanArrayOf(true))
        val phoneCall = QrCode(
            2, booleanArrayOf(
                true, false,
                false, true,
            )
        )
        val meCard = QrCode(
            3, booleanArrayOf(
                true, false, false,
                false, true, false,
                false, false, true,
            )
        )
    }

    val nativeQrCodeMaker = object : NativeQrCodeMaker {
        override fun text(text: String, ecc: Int): QrCode = QR.text
        override fun phoneCall(phone: String, ecc: Int): QrCode = QR.phoneCall
        override fun meCard(meCard: MeCardContent, ecc: Int): QrCode = QR.meCard
    }
}

class NewContent : Content() {
    override var id: Int = 0
    override val title: String = Fake.Data.TITLE
    override val qrCodeType: QrCodeType = Fake.Type.text
    override fun string(): String = ""
}
