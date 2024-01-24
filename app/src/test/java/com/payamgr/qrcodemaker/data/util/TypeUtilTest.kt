package com.payamgr.qrcodemaker.data.util

import androidx.test.filters.SmallTest
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test

@SmallTest
class TypeUtilTest {
    @Test
    fun `qrCodeTypes test`() {
        assertThat(TypeUtil.qrCodeTypes.map { it.javaClass.canonicalName })
            .isEqualTo(
                listOf(
                    "com.payamgr.qrcodemaker.data.model.QrCodeType.MeCard",
                    "com.payamgr.qrcodemaker.data.model.QrCodeType.PhoneCall",
                    "com.payamgr.qrcodemaker.data.model.QrCodeType.Text",
                )
            )
    }
}
