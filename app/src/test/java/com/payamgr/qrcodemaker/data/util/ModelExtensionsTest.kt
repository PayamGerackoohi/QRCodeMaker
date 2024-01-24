package com.payamgr.qrcodemaker.data.util

import androidx.compose.ui.text.input.KeyboardType
import androidx.test.filters.SmallTest
import com.payamgr.qrcodemaker.data.model.Input
import com.payamgr.qrcodemaker.data.model.InputData
import com.payamgr.qrcodemaker.data.model.InputId
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@SmallTest
class ModelExtensionsTest {
    private val singleInput = Input.Single(InputId.Title, 1, "aaa", KeyboardType.Text)

    @Nested
    @DisplayName("Input::string() Test")
    inner class InputStringTest {
        @Test
        fun `Single test`() {
            assertThat(singleInput.string()).isEqualTo("Single(id: Title, initialValue: aaa)")
        }

        @Test
        fun `Group test`() {
            val input = Input.Group(2, listOf(singleInput))
            assertThat(input.string()).isEqualTo("Group(inputs: [Single(id: Title, initialValue: aaa)])")
        }
    }

    @Nested
    @DisplayName("InputData::isValid Test")
    inner class InputDataIsValidTest {
        @Test
        fun `optional test`() {
            listOf(
                "" to true,
                "  " to true,
                "a" to true,
            ).forEachIndexed { index, (input, output) ->
                assertThat(InputData(input, true).isValid)
                    .`as`("index: $index, input: $input, output: $output")
                    .isEqualTo(output)
            }
        }

        @Test
        fun `mandatory test`() {
            listOf(
                "" to false,
                "  " to false,
                "a" to true,
            ).forEachIndexed { index, (input, output) ->
                assertThat(InputData(input, false).isValid)
                    .`as`("index: $index, input: $input, output: $output")
                    .isEqualTo(output)
            }
        }
    }
}
