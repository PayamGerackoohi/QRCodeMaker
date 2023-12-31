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

@Entity(tableName = "text_content")
data class TextContent(
    override val title: String,
    val text: String,
) : Content() {
    @PrimaryKey(autoGenerate = true)
    override var id: Int = 0

    override val qrCodeType get() = QrCodeType.Text(text)

    override suspend fun qrCode(ecc: ErrorCorrectionCodeLevel) = withContext(Dispatchers.Default) {
        QrCodeMaker.text(text, ecc.ordinal)
    }

    override fun print() = buildString {
        appendLine("Text:")
        append(text)
    }

    companion object {
        fun digest(input: Map<InputId, InputData>) = input.run {
            TextContent(
                title = unwrap(InputId.Title),
                text = unwrap(InputId.Text),
            )
        }
    }
}
