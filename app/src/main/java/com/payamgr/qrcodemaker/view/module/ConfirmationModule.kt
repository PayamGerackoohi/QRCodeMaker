package com.payamgr.qrcodemaker.view.module

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.payamgr.qrcodemaker.view.theme.QRCodeMakerTheme
import kotlinx.coroutines.launch

@Preview
@Composable
fun ConfirmationModule_Preview() {
    QRCodeMakerTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Column {
                var showConfirmation by remember { mutableStateOf(false) }
                var status by remember { mutableStateOf("") }
                Column {
                    Button(onClick = { showConfirmation = true }) {
                        Text(text = "Show")
                    }
                    Text(text = status)
                    Confirmation.Module(
                        show = showConfirmation,
                        onDismiss = { showConfirmation = false },
                        title = "Title",
                        body = "Body",
                        actions = arrayOf(
//                            ActionType(
//                                0,
//                                "OK",
//                                Confirmation.defaultButtonColors(container = Color.Red, text = Color.White),
//                            )
                            ActionType(
                                0,
                                "Yes",
                                Confirmation.defaultButtonColors(container = Color.Blue),
                            ),
                            ActionType(
                                1,
                                "No",
                                Confirmation.defaultButtonColors(container = Color.Red),
                            ),
                        ),
                        onAction = { status = "$it" },
                    )
                }
            }
        }
    }
}

data class ActionType(
    val id: Int,
    val label: String,
    val colors: Confirmation.ButtonColors? = null,
)

object Confirmation {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Module(
        show: Boolean,
        onDismiss: () -> Unit,
        actions: Array<ActionType>,
        onAction: (id: Int) -> Unit,
        title: String? = null,
        body: String? = null,
    ) {
        val sheetState = rememberModalBottomSheetState()
        val scope = rememberCoroutineScope()
        val hideSheet: () -> Unit = {
            scope.launch { sheetState.hide() }.invokeOnCompletion {
                if (!sheetState.isVisible) onDismiss()
            }
        }
        if (show)
            ModalBottomSheet(
                sheetState = sheetState,
                onDismissRequest = { onDismiss() },
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    val textModifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                    if (title != null)
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center,
                            modifier = textModifier
                        )
                    if (body != null)
                        Text(
                            text = body,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = textModifier
                        )
                    Row(
                        horizontalArrangement = Arrangement.SpaceAround,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        actions.forEachIndexed { index, action ->
                            if (index != 0)
                                Divider(modifier = Modifier.width(1.dp))
                            ModuleButton(
                                onClick = {
                                    onAction(action.id)
                                    hideSheet()
                                },
                                label = action.label,
                                buttonColors = action.colors,
                            )
                        }
                    }
                }
            }
    }

    @Composable
    fun RowScope.ModuleButton(
        onClick: () -> Unit,
        label: String,
        buttonColors: ButtonColors?,
    ) {
        val colors = buttonColors ?: defaultButtonColors()
        Button(
            onClick = onClick,
            shape = RectangleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.container,
                contentColor = colors.text,
            ),
            modifier = Modifier.weight(1f)
        ) {
            Text(text = label)
        }
    }

    @Composable
    fun defaultButtonColors(
        container: Color = MaterialTheme.colorScheme.primaryContainer,
        text: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    ): ButtonColors = ButtonColors(container, text)

    @Immutable
    class ButtonColors(
        val container: Color,
        val text: Color,
    )
}
