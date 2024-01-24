package com.payamgr.qrcodemaker.data.database.entity

import androidx.test.filters.SmallTest
import com.payamgr.qrcodemaker.data.model.InputData
import com.payamgr.qrcodemaker.data.model.InputId
import com.payamgr.qrcodemaker.data.util.string
import com.payamgr.qrcodemaker.test_util.Fake
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@SmallTest
class TextContentTest {
    private lateinit var content: TextContent

    @BeforeEach
    fun setup() {
        content = Fake.Data.run { TextContent(TITLE, TEXT) }
    }

    @Test
    fun `qrCodeType test`() {
        assertThat(content.qrCodeType.inputs.map { it.string() }.toString())
            .isEqualTo("[Single(id: Text, initialValue: Text)]")
    }

    @Test
    fun `print test`() {
        assertThat(content.string()).isEqualTo("Text:\nText")
    }

    @Nested
    inner class DigestTest {
        @Test
        fun `empty input data`() {
            assertThat(TextContent.digest(mapOf()))
                .isEqualTo(TextContent("", ""))
        }

        @Test
        fun `normal data`() {
            val input = Fake.Data.run {
                mapOf(
                    InputId.Title to InputData(TITLE),
                    InputId.Text to InputData(TEXT),
                )
            }
            assertThat(TextContent.digest(input)).isEqualTo(content)
        }
    }
}
