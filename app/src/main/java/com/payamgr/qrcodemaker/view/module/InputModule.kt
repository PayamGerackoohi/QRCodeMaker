package com.payamgr.qrcodemaker.view.module

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import com.payamgr.qrcodemaker.data.model.action.ReactiveAction

object InputModule {
    @Composable
    fun TextType(
        valueAction: ReactiveAction<String>,
        label: String,
        keyboardType: KeyboardType,
        isMandatory: Boolean,
        isError: Boolean,
        modifier: Modifier = Modifier,
        singleLine: Boolean = true,
    ) {
        TextField(
            value = valueAction.data,
            onValueChange = valueAction.onDataChanged,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            label = {
                Text(text = buildAnnotatedString {
                    if (isMandatory)
                        withStyle(SpanStyle(color = MaterialTheme.colorScheme.error)) { append("* ") }
                    append(label)
                })
            },
            singleLine = singleLine,
            isError = isError,
            modifier = modifier.testTag("InputModule.TextType.TextField"),
        )
    }
}
