package com.payamgr.qrcodemaker.view.module

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.payamgr.qrcodemaker.data.model.ErrorCorrectionCodeLevel
import com.payamgr.qrcodemaker.data.model.ErrorCorrectionCodeLevel.*
import com.payamgr.qrcodemaker.data.model.action.ReactiveAction
import com.payamgr.qrcodemaker.view.theme.QRCodeMakerTheme

@Preview
@Composable
fun ErrorCorrectionModule_Preview() {
    QRCodeMakerTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Column {
                var level by remember { mutableStateOf(Medium) }
                ErrorCorrectionModule(
                    eccAction = ReactiveAction(
                        data = level,
                        onDataChanged = { level = it },
                    ),
                )
            }
        }
    }
}

private val levels = ErrorCorrectionCodeLevel.values()

@Composable
fun ErrorCorrectionModule(eccAction: ReactiveAction<ErrorCorrectionCodeLevel>) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth()
    ) {
        levels.forEach { level ->
            Item(
                label = level.name,
                isSelected = level == eccAction.data,
                shape = when (level) {
                    Low -> RoundedCornerShape(bottomStart = 16.dp, topStart = 16.dp)
                    Medium -> RectangleShape
                    High -> RoundedCornerShape(bottomEnd = 16.dp, topEnd = 16.dp)
                },
                onClick = { eccAction.onDataChanged(level) },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RowScope.Item(
    label: String,
    isSelected: Boolean,
    shape: Shape,
    onClick: () -> Unit,
) {
    ElevatedFilterChip(
        selected = isSelected,
        onClick = onClick,
        label = {
            Text(
                text = label,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
        },
        shape = shape,
        colors = FilterChipDefaults.elevatedFilterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.errorContainer
        ),
        modifier = Modifier.weight(1f)
    )
}
