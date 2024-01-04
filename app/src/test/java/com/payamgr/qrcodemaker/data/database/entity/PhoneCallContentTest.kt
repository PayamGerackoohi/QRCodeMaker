package com.payamgr.qrcodemaker.data.database.entity

import com.payamgr.qrcodemaker.data.model.InputData
import com.payamgr.qrcodemaker.data.model.InputId
import com.payamgr.qrcodemaker.data.util.string
import com.payamgr.qrcodemaker.test_util.Fake
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class PhoneCallContentTest {
    private lateinit var content: PhoneCallContent

    @BeforeEach
    fun setup() {
        content = Fake.Data.run { PhoneCallContent(TITLE, PHONE) }
    }

    @Test
    fun `qrCodeType test`() {
        assertThat(content.qrCodeType.inputs.map { it.string() }.toString())
            .isEqualTo("[Single(id: Phone, initialValue: Phone)]")
    }

    @Test
    fun `print test`() {
        assertThat(content.string()).isEqualTo("Phone:\nPhone")
    }

    @Nested
    inner class DigestTest {
        @Test
        fun `empty input data`() {
            assertThat(PhoneCallContent.digest(mapOf()))
                .isEqualTo(PhoneCallContent("", ""))
        }

        @Test
        fun `normal data`() {
            val input = Fake.Data.run {
                mapOf(
                    InputId.Title to InputData(TITLE),
                    InputId.Phone to InputData(PHONE),
                )
            }
            assertThat(PhoneCallContent.digest(input)).isEqualTo(content)
        }
    }
}
