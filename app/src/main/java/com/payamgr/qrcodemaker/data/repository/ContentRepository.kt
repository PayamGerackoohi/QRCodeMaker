package com.payamgr.qrcodemaker.data.repository

import com.payamgr.qrcodemaker.data.model.Content
import com.payamgr.qrcodemaker.data.model.QrCodeType
import kotlinx.coroutines.flow.StateFlow

interface ContentRepository {
    val currentContent: StateFlow<Content?>
    val currentQrCodeType: StateFlow<QrCodeType?>
    suspend fun loadContents(): StateFlow<List<Content>>
    suspend fun push(content: Content)
    suspend fun push(type: QrCodeType)
    suspend fun add(content: Content)
    suspend fun update(content: Content)
    suspend fun removeCurrentContent()
}
