package com.payamgr.qrcodemaker.data.repository

import com.payamgr.qrcodemaker.data.database.QrDatabase
import com.payamgr.qrcodemaker.data.database.entity.MeCardContent
import com.payamgr.qrcodemaker.data.database.entity.PhoneCallContent
import com.payamgr.qrcodemaker.data.database.entity.TextContent
import com.payamgr.qrcodemaker.data.di.CoroutineModule
import com.payamgr.qrcodemaker.data.model.Content
import com.payamgr.qrcodemaker.data.model.QrCodeType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContentRepositoryImpl @Inject constructor(
    private val db: QrDatabase,
    @CoroutineModule.IO private val dispatcher: CoroutineDispatcher,
) : ContentRepository {
    private var needsNewContents = true
    private val contents = MutableStateFlow<List<Content>>(listOf())
    override val currentContent = MutableStateFlow<Content?>(null)
    override val currentQrCodeType = MutableStateFlow<QrCodeType?>(null)

    override suspend fun loadContents() = withContext(dispatcher) {
        if (needsNewContents) {
            internalLoadContents()
            needsNewContents = false
        }
        contents
    }

    private suspend fun internalLoadContents() {
        contents.value = mutableListOf<Content>().apply {
            addAll(db.textContentDao().getAll())
            addAll(db.phoneCallContentDao().getAll())
            addAll(db.meCardContentDao().getAll())
        }
    }

    override suspend fun push(content: Content) {
        currentContent.value = content
    }

    override suspend fun push(type: QrCodeType) {
        currentQrCodeType.value = type
    }

    override suspend fun add(content: Content) = withContext(dispatcher) {
        when (content) {
            is TextContent -> db.textContentDao().insert(content)
            is PhoneCallContent -> db.phoneCallContentDao().insert(content)
            is MeCardContent -> db.meCardContentDao().insert(content)
            else -> throw Error("Unknown content: $content")
        }
        internalLoadContents()
    }

    override suspend fun update(content: Content) = withContext(dispatcher) {
        when (content) {
            is TextContent -> db.textContentDao().update(content)
            is PhoneCallContent -> db.phoneCallContentDao().update(content)
            is MeCardContent -> db.meCardContentDao().update(content)
            else -> throw Error("Unknown content: $content")
        }
        push(content)
        internalLoadContents()
    }

    override suspend fun removeCurrentContent() {
        withContext(dispatcher) {
            currentContent.value?.let { content ->
                when (content) {
                    is TextContent -> db.textContentDao().delete(content)
                    is PhoneCallContent -> db.phoneCallContentDao().delete(content)
                    is MeCardContent -> db.meCardContentDao().delete(content)
                    else -> throw Error("Unknown content: $content")
                }
                internalLoadContents()
                currentContent.value = null
            }
        }
    }
}
