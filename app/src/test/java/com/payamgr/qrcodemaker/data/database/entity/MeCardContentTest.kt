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
class MeCardContentTest {
    private lateinit var card: MeCardContent

    @BeforeEach
    fun setup() {
        card = Fake.Data.run { MeCardContent(TITLE, FIRST_NAME, LAST_NAME, PHONE) }
    }

    @Test
    fun `constructor test`() {
        card.apply {
            Fake.Data.apply {
                assertThat(title).isEqualTo(TITLE)
                assertThat(firstName).isEqualTo(FIRST_NAME)
                assertThat(lastName).isEqualTo(LAST_NAME)
                assertThat(phone).isEqualTo(PHONE)
            }
        }
    }

    @Test
    fun `qrCodeType test`() {
        assertThat(card.qrCodeType.inputs.map { it.string() }.toString())
            .isEqualTo(
                "[Group(inputs: [" +
                        "Single(id: FirstName, initialValue: FirstName), " +
                        "Single(id: LastName, initialValue: LastName)" +
                        "]), " +
                        "Single(id: Phone, initialValue: Phone)" +
                        "]"
            )
    }

    @Test
    fun `print test`() {
        assertThat(card.string())
            .isEqualTo("First Name:\nFirstName\n\nLast Name:\nLastName\n\nPhone:\nPhone")
    }

    @Nested
    inner class DigestTest {
        @Test
        fun `empty input data`() {
            assertThat(MeCardContent.digest(mapOf()))
                .isEqualTo(MeCardContent("", "", "", ""))
        }

        @Test
        fun `normal data`() {
            val input = Fake.Data.run {
                mapOf(
                    InputId.Title to InputData(TITLE),
                    InputId.FirstName to InputData(FIRST_NAME),
                    InputId.LastName to InputData(LAST_NAME),
                    InputId.Phone to InputData(PHONE),
                )
            }
            assertThat(MeCardContent.digest(input)).isEqualTo(card)
        }
    }
}
