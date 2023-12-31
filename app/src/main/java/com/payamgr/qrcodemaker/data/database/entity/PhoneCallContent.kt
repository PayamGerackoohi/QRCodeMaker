package com.payamgr.qrcodemaker.data.database.entity

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.payamgr.qrcodemaker.data.QrCodeMaker
import com.payamgr.qrcodemaker.data.model.Content
import com.payamgr.qrcodemaker.data.model.ErrorCorrectionCodeLevel
import com.payamgr.qrcodemaker.data.model.InputData
import com.payamgr.qrcodemaker.data.model.InputId
import com.payamgr.qrcodemaker.data.model.QrCodeType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Entity(tableName = "phone_call_content")
class PhoneCallContent(
    override val title: String,
    val phone: String,
) : Content() {
    @PrimaryKey(autoGenerate = true)
    override var id: Int = 0

    override val qrCodeType get() = QrCodeType.PhoneCall(phone)

    override suspend fun qrCode(ecc: ErrorCorrectionCodeLevel) = withContext(Dispatchers.Default) {
        QrCodeMaker.phoneCall(phone, ecc.ordinal)
    }

    override fun print() = buildString {
        appendLine("Phone:")
        append(phone)
    }

    companion object {
        fun digest(input: Map<InputId, InputData>) = input.run {
            PhoneCallContent(
                title = unwrap(InputId.Title),
                phone = unwrap(InputId.Phone),
            )
        }
    }
}
