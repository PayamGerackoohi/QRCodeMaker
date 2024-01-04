package com.payamgr.qrcodemaker.data.model

import androidx.annotation.StringRes
import androidx.compose.ui.text.input.KeyboardType
import com.payamgr.qrcodemaker.R

sealed class QrCodeType(@StringRes val titleId: Int, @StringRes val descriptionId: Int, val inputs: List<Input>) {
    class Text(text: String = "") : QrCodeType(
        R.string.text,
        R.string.qrcode_type_description_text,
        listOf(Input.Single(InputId.Text, R.string.text, text, KeyboardType.Text, singleLine = false)),
    )

    class PhoneCall(phone: String = "") : QrCodeType(
        R.string.phone_call,
        R.string.qrcode_type_description_phone_call,
        listOf(Input.Single(InputId.Phone, R.string.phone, phone, KeyboardType.Phone)),
    )

    class MeCard(
        firstName: String = "",
        lastName: String = "",
        phone: String = "",
    ) : QrCodeType(
        R.string.me_card,
        R.string.qrcode_type_description_me_card,
        listOf(
            Input.Group(
                R.string.name,
                listOf(
                    Input.Single(InputId.FirstName, R.string.name_first_name, firstName, KeyboardType.Text),
                    Input.Single(InputId.LastName, R.string.name_last_name, lastName, KeyboardType.Text),
                ),
            ),
            Input.Single(InputId.Phone, R.string.phone, phone, KeyboardType.Phone),
        ),
    )
}
// Input.Single(InputId.NamePrefix, R.string.name_prefix, KeyboardType.Text, true),
// Input.Single(InputId.MiddleName, R.string.name_middle_name, KeyboardType.Text, true),
// Input.Single(InputId.NamePostfix, R.string.name_postfix, KeyboardType.Text, true),
