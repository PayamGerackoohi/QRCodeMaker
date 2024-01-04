package com.payamgr.qrcodemaker.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.payamgr.qrcodemaker.data.model.Content
import com.payamgr.qrcodemaker.data.model.InputData
import com.payamgr.qrcodemaker.data.model.InputId
import com.payamgr.qrcodemaker.data.model.QrCodeType

@Entity(tableName = "me_card_content")
data class MeCardContent(
    override val title: String,

    @ColumnInfo("first_name")
    val firstName: String,

    @ColumnInfo("last_name")
    val lastName: String,

    val phone: String,
) : Content() {
    @PrimaryKey(autoGenerate = true)
    override var id: Int = 0

    override val qrCodeType get() = QrCodeType.MeCard(firstName, lastName, phone)

    override fun string() = buildString {
        appendLine("First Name:")
        appendLine(firstName)
        appendLine()
        appendLine("Last Name:")
        appendLine(lastName)
        appendLine()
        appendLine("Phone:")
        append(phone)
    }

    companion object {
        fun digest(input: Map<InputId, InputData>) = input.run {
            MeCardContent(
                title = unwrap(InputId.Title),
                firstName = unwrap(InputId.FirstName),
                lastName = unwrap(InputId.LastName),
                phone = unwrap(InputId.Phone),
            )
        }
    }
}
