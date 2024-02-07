package com.payamgr.qrcodemaker.view.module

import androidx.annotation.DrawableRes
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.payamgr.qrcodemaker.R

@Preview
@Composable
fun AnimatedVector_Preview() {
    var atEnd by remember { mutableStateOf(false) }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.background(color = Color.White)
    ) {
        Button(onClick = { atEnd = atEnd.not() }) {
            Text(text = "Toggle")
        }
        AnimatedVector.Module(
            resourceId = R.drawable.splash_screen_icon,
            atEnd = atEnd,
            label = "",
            modifier = Modifier.size(150.dp)
        )
    }
}

object AnimatedVector {
    @OptIn(ExperimentalAnimationGraphicsApi::class)
    @Composable
    fun Module(
        @DrawableRes resourceId: Int,
        atEnd: Boolean,
        label: String,
        modifier: Modifier,
    ) {
        val state = stringResource(if (atEnd) R.string.end else R.string.beginning)
        Image(
            painter = rememberAnimatedVectorPainter(
                animatedImageVector = AnimatedImageVector.animatedVectorResource(id = resourceId),
                atEnd = atEnd
            ),
            contentDescription = null,
            modifier = modifier.semantics {
                contentDescription = "Animated Vector: $label"
                stateDescription = state
            }
        )
    }
}
