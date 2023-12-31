package com.payamgr.qrcodemaker.view.page.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.airbnb.mvrx.Mavericks
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import com.payamgr.qrcodemaker.data.model.Content
import com.payamgr.qrcodemaker.data.model.event.HomeEvent
import com.payamgr.qrcodemaker.view.theme.QRCodeMakerTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

@Preview
@Composable
fun HomePage_Preview() {
    QRCodeMakerTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Mavericks.initialize(LocalContext.current)
            Home.Page({}, {})
        }
    }
}

object Home {
    const val Route = "home"

    fun NavGraphBuilder.homePage(navigateToContentType: () -> Unit, navigateToShowQrCode: (Content) -> Unit) {
        composable(Route) {
            Page(navigateToContentType, navigateToShowQrCode)
        }
    }

    @Composable
    fun Page(
        navigateToContentType: () -> Unit,
        navigateToShowQrCode: (Content) -> Unit,
        viewModel: HomeVM = mavericksViewModel(),
    ) {
        HandleEvents(viewModel.eventFlow, navigateToShowQrCode, navigateToContentType)
        val state by viewModel.collectAsState()
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(16.dp),
        ) {
            item { AddContent(showContentTypePage = viewModel::showContentTypePage) }
            items(state.contents) {
                QrCodeContent(it.title) { viewModel.showQrCode(it) }
            }
        }
    }

    @Composable
    fun HandleEvents(
        event: Flow<HomeEvent>,
        navigateToShowQrCode: (Content) -> Unit,
        navigateToContentType: () -> Unit,
    ) {
        LaunchedEffect(key1 = Unit) {
            event.collectLatest {
                when (it) {
                    is HomeEvent.NavigateToShowQrCode -> navigateToShowQrCode(it.content)
                    HomeEvent.NavigateToContentType -> navigateToContentType()
                }
            }
        }
    }

    @Composable
    fun AddContent(showContentTypePage: () -> Unit) {
        Button(onClick = showContentTypePage) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Default.Add, "add content")
                Text(text = "Add Content")
            }
        }
    }

    @Composable
    fun QrCodeContent(title: String, onClick: () -> Unit) {
        Button(onClick = onClick) {
            Text(text = title)
        }
    }
}
