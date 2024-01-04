package com.payamgr.qrcodemaker.data

import com.payamgr.qrcodemaker.data.database.entity.MeCardContent
import com.payamgr.qrcodemaker.data.database.entity.PhoneCallContent
import com.payamgr.qrcodemaker.data.database.entity.TextContent
import com.payamgr.qrcodemaker.data.di.CoroutineModule
import com.payamgr.qrcodemaker.data.model.Content
import com.payamgr.qrcodemaker.data.model.ErrorCorrectionCodeLevel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QrCodeMakerImpl @Inject constructor(
    @CoroutineModule.Default private val dispatcher: CoroutineDispatcher,
    private val native: NativeQrCodeMaker,
) : QrCodeMaker {
    override suspend fun encode(content: Content, errorCorrectionCodeLevel: ErrorCorrectionCodeLevel) =
        withContext(dispatcher) {
            val ecc = errorCorrectionCodeLevel.ordinal
            native.run {
                when (content) {
                    is TextContent -> text(content.text, ecc)
                    is PhoneCallContent -> phoneCall(content.phone, ecc)
                    is MeCardContent -> meCard(content, ecc)
                    else -> throw IllegalArgumentException(content::class.qualifiedName!!)
                }
            }
        }
}
