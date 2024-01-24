package com.payamgr.qrcodemaker.data

import androidx.test.filters.SmallTest
import com.payamgr.qrcodemaker.data.model.ErrorCorrectionCodeLevel
import com.payamgr.qrcodemaker.test_util.Fake
import com.payamgr.qrcodemaker.test_util.NewContent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@SmallTest
@OptIn(ExperimentalCoroutinesApi::class)
class QrCodeMakerImplTest {
    private val utd = UnconfinedTestDispatcher()
    private lateinit var qrCodeMaker: QrCodeMakerImpl

    @BeforeEach
    fun setup() {
        qrCodeMaker = QrCodeMakerImpl(utd, Fake.nativeQrCodeMaker)
        Dispatchers.setMain(utd)
    }

    @AfterEach
    fun cleanup() {
        Dispatchers.resetMain()
    }

    @Nested
    @DisplayName("Encode Text")
    inner class EncodeTest {
        @Test
        fun `text test`() = runTest {
            val qrCode = qrCodeMaker.encode(Fake.Content.text, ErrorCorrectionCodeLevel.Low)
            assertThat(qrCode).isEqualTo(Fake.QR.text)
        }

        @Test
        fun `phone call test`() = runTest {
            val qrCode = qrCodeMaker.encode(Fake.Content.phoneCall, ErrorCorrectionCodeLevel.Medium)
            assertThat(qrCode).isEqualTo(Fake.QR.phoneCall)
        }

        @Test
        fun `me-card test`() = runTest {
            val qrCode = qrCodeMaker.encode(Fake.Content.meCard, ErrorCorrectionCodeLevel.High)
            assertThat(qrCode).isEqualTo(Fake.QR.meCard)
        }

        @Test
        fun `unknown content test`() = runTest {
            assertThrows<IllegalArgumentException> {
                qrCodeMaker.encode(NewContent(), ErrorCorrectionCodeLevel.High)
            }.let {
                assertThat(it.message).isEqualTo("com.payamgr.qrcodemaker.test_util.NewContent")
            }
        }
    }
}
