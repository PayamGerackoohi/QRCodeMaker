package com.payamgr.qrcodemaker.data.repository

import androidx.test.filters.SmallTest
import com.payamgr.qrcodemaker.R
import com.payamgr.qrcodemaker.data.database.QrDatabase
import com.payamgr.qrcodemaker.test_util.DbHelperCastExtensions.asDbHelper
import com.payamgr.qrcodemaker.test_util.Fake
import com.payamgr.qrcodemaker.test_util.NewContent
import io.mockk.verify
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
import kotlin.IllegalArgumentException

@SmallTest
@OptIn(ExperimentalCoroutinesApi::class)
class ContentRepositoryImplTest {
    private val utd = UnconfinedTestDispatcher()
    private lateinit var repository: ContentRepository
    private lateinit var database: QrDatabase

    @BeforeEach
    fun setup() {
        database = Fake.mockDatabase
        repository = ContentRepositoryImpl(database, utd)
        Dispatchers.setMain(utd)
    }

    @AfterEach
    fun cleanup() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state check`() = runTest {
        assertThat(repository.currentContent.value).isEqualTo(null)
        assertThat(repository.currentQrCodeType.value).isEqualTo(null)
    }

    @Test
    fun `load contents test`() = runTest {
        val card = Fake.Content.meCard

        // Verify initial state
        val contents = repository.loadContents()
        assertThat(contents.value).isEmpty()

        // Verify the database has called getAll
        database.textContentDao().asDbHelper().apply { verify(exactly = 1) { getAllCall() } }
        database.phoneCallContentDao().asDbHelper().apply { verify(exactly = 1) { getAllCall() } }
        database.meCardContentDao().asDbHelper().apply { verify(exactly = 1) { getAllCall() } }

        // Add me-card content
        repository.add(card)

        // Verify the database has called getAll again
        database.meCardContentDao().asDbHelper().apply { verify(exactly = 2) { getAllCall() } }

        // Verify the contents-flow is updated
        contents.value.let {
            assertThat(it).hasSize(1)
            assertThat(it).first().isEqualTo(card)
        }

        // Test Calling loadContents again
        // - Verify the contents-flow has the latest data
        repository.loadContents().value.let {
            assertThat(it).hasSize(1)
            assertThat(it).first().isEqualTo(card)
        }

        // - Verify the database has not called getAll again
        database.meCardContentDao().asDbHelper().apply { verify(exactly = 2) { getAllCall() } }

        // Add other content types
        repository.add(Fake.Content.text)
        repository.add(Fake.Content.phoneCall)

        // Verify the contents-flow has been updated
        repository.loadContents().value.let {
            assertThat(it).hasSize(3)
            assertThat(it[0]).isEqualTo(Fake.Content.text)
            assertThat(it[1]).isEqualTo(Fake.Content.phoneCall)
            assertThat(it[2]).isEqualTo(card)
        }
    }

    @Test
    fun `push content test`() = runTest {
        repository.push(Fake.Content.text)
        assertThat(repository.currentContent.value).isEqualTo(Fake.Content.text)
    }

    @Test
    fun `push qr-code type test`() = runTest {
        repository.push(Fake.Type.phoneCall)
        assertThat(repository.currentQrCodeType.value?.inputs?.toString()).isEqualTo("[Single(id=Phone, labelId=${R.string.phone}, initialValue=Phone, keyboardType=Phone, isOptional=false, singleLine=true)]")
    }

    @Nested
    @DisplayName("Add Content Test")
    inner class AddContentTest {
        @Test
        fun `add text`() = runTest {
            val contents = repository.loadContents()

            // Verify the initial state
            assertThat(contents.value).isEmpty()

            // Add text content
            repository.add(Fake.Content.text)

            // Verify the database has inserted the data
            database.textContentDao().asDbHelper().apply {
                verify { insertCall(Fake.Content.text) }
            }

            // Verify the contents state-flow is updated
            assertThat(contents.value).hasSize(1)
            assertThat(contents.value).first().isEqualTo(Fake.Content.text)
        }

        @Test
        fun `add phone call`() = runTest {
            val contents = repository.loadContents()

            // Verify the initial state
            assertThat(contents.value).isEmpty()

            // Add phone call content
            repository.add(Fake.Content.phoneCall)

            // Verify the database has inserted the data
            database.phoneCallContentDao().asDbHelper().apply {
                verify { insertCall(Fake.Content.phoneCall) }
            }

            // Verify the contents state-flow is updated
            assertThat(contents.value).hasSize(1)
            assertThat(contents.value).first().isEqualTo(Fake.Content.phoneCall)
        }

        @Test
        fun `add me-card`() = runTest {
            val contents = repository.loadContents()

            // Verify the initial state
            assertThat(contents.value).isEmpty()

            // Add me-card content
            repository.add(Fake.Content.meCard)

            // Verify the database has inserted the data
            database.meCardContentDao().asDbHelper().apply {
                verify { insertCall(Fake.Content.meCard) }
            }

            // Verify the contents state-flow is updated
            assertThat(contents.value).hasSize(1)
            assertThat(contents.value).first().isEqualTo(Fake.Content.meCard)
        }

        @Test
        fun `add unknown content`() = runTest {
            val contents = repository.loadContents()

            // Verify the initial state
            assertThat(contents.value).isEmpty()

            // Add unknown content and except an exception
            assertThrows<IllegalArgumentException> {
                repository.add(NewContent())
            }.let {
                assertThat(it.message).isEqualTo("Unknown content type: com.payamgr.qrcodemaker.test_util.NewContent")
            }
        }
    }

    @Nested
    @DisplayName("Update Content Test")
    inner class UpdateContentTest {
        @Test
        fun `update text`() = runTest {
            val contents = repository.loadContents()

            // Add text content
            repository.add(Fake.Content.text)

            // Verify addition
            assertThat(contents.value).hasSize(1)
            assertThat(contents.value).first().isEqualTo(Fake.Content.text)

            // Update the content
            repository.update(Fake.Content.text2)

            // Verify the database has updated the data
            database.textContentDao().asDbHelper().apply {
                verify { updateCall(Fake.Content.text2) }
            }

            // Verify the contents state-flow is updated
            assertThat(contents.value).hasSize(1)
            assertThat(contents.value).first().isEqualTo(Fake.Content.text2)
        }

        @Test
        fun `update phone call`() = runTest {
            val contents = repository.loadContents()

            // Add phone call content
            repository.add(Fake.Content.phoneCall)

            // Verify addition
            assertThat(contents.value).hasSize(1)
            assertThat(contents.value).first().isEqualTo(Fake.Content.phoneCall)

            // Update the phone call content
            repository.update(Fake.Content.phoneCall2)

            // Verify the database has updated the data
            database.phoneCallContentDao().asDbHelper().apply {
                verify { updateCall(Fake.Content.phoneCall2) }
            }

            // Verify the contents state-flow is updated
            assertThat(contents.value).hasSize(1)
            assertThat(contents.value).first().isEqualTo(Fake.Content.phoneCall2)
        }

        @Test
        fun `update me-card`() = runTest {
            val contents = repository.loadContents()

            // Add me-card content
            repository.add(Fake.Content.meCard)

            // Verify addition
            assertThat(contents.value).hasSize(1)
            assertThat(contents.value).first().isEqualTo(Fake.Content.meCard)

            // Update the me-card content
            repository.update(Fake.Content.meCard2)

            // Verify the database has updated the data
            database.meCardContentDao().asDbHelper().apply {
                verify { updateCall(Fake.Content.meCard2) }
            }

            // Verify the contents state-flow is updated
            assertThat(contents.value).hasSize(1)
            assertThat(contents.value).first().isEqualTo(Fake.Content.meCard2)
        }

        @Test
        fun `update unknown content`() = runTest {
            // Add text content
            repository.add(Fake.Content.text)

            // Update unknown content and except the exception
            assertThrows<IllegalArgumentException> {
                repository.update(NewContent())
            }.let {
                assertThat(it.message).isEqualTo("Unknown content type: com.payamgr.qrcodemaker.test_util.NewContent")
            }
        }
    }

    @Nested
    @DisplayName("Remove Current Content Test")
    inner class RemoveCurrentContentTest {
        @Test
        fun `remove text`() = runTest {
            val contents = repository.loadContents()

            // Add and push text content
            val content = Fake.Content.text
            repository.add(content)
            repository.push(content)

            // Verify addition and push
            assertThat(contents.value).hasSize(1)
            assertThat(contents.value).first().isEqualTo(content)
            assertThat(repository.currentContent.value).isEqualTo(content)

            // Remove the text content
            repository.removeCurrentContent()

            // Verify the database has removed the data
            database.textContentDao().asDbHelper().apply {
                verify { deleteCall(Fake.Content.text) }
            }

            // Verify the contents and currentContent state-flow are updated
            assertThat(contents.value).isEmpty()
            assertThat(repository.currentContent.value).isEqualTo(null)
        }

        @Test
        fun `remove phone call`() = runTest {
            val contents = repository.loadContents()

            // Add and push phone call content
            val content = Fake.Content.phoneCall
            repository.add(content)
            repository.push(content)

            // Verify addition and push
            assertThat(contents.value).hasSize(1)
            assertThat(contents.value).first().isEqualTo(content)
            assertThat(repository.currentContent.value).isEqualTo(content)

            // Remove the phone call content
            repository.removeCurrentContent()

            // Verify the database has removed the data
            database.phoneCallContentDao().asDbHelper().apply {
                verify { deleteCall(Fake.Content.phoneCall) }
            }

            // Verify the contents and currentContent state-flow are updated
            assertThat(contents.value).isEmpty()
            assertThat(repository.currentContent.value).isEqualTo(null)
        }

        @Test
        fun `remove me-card`() = runTest {
            val contents = repository.loadContents()

            // Add and push me-card content
            val content = Fake.Content.meCard
            repository.add(content)
            repository.push(content)

            // Verify addition and push
            assertThat(contents.value).hasSize(1)
            assertThat(contents.value).first().isEqualTo(content)
            assertThat(repository.currentContent.value).isEqualTo(content)

            // Update the me-card content
            repository.removeCurrentContent()

            // Verify the database has removed the data
            database.meCardContentDao().asDbHelper().apply {
                verify { deleteCall(Fake.Content.meCard) }
            }

            // Verify the contents and currentContent state-flow are updated
            assertThat(contents.value).isEmpty()
            assertThat(repository.currentContent.value).isEqualTo(null)
        }

        @Test
        fun `remove while repository has no content`() = runTest {
            val contents = repository.loadContents()

            // Verify initial state
            assertThat(contents.value).isEmpty()
            assertThat(repository.currentContent.value).isEqualTo(null)

            // Remove current content
            repository.removeCurrentContent()

            // Verify the contents and currentContent state-flow are still the same
            assertThat(contents.value).isEmpty()
            assertThat(repository.currentContent.value).isEqualTo(null)
        }

        @Test
        fun `remove unknown content`() = runTest {
            // push unknown content
            repository.push(NewContent())

            // verify removing the content throws
            assertThrows<IllegalArgumentException> {
                repository.removeCurrentContent()
            }.let {
                assertThat(it.message).isEqualTo("Unknown content type: com.payamgr.qrcodemaker.test_util.NewContent")
            }
        }
    }
}
