package com.payamgr.qrcodemaker.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.payamgr.qrcodemaker.data.database.dao.MeCardContentDao
import com.payamgr.qrcodemaker.data.database.dao.PhoneCallContentDao
import com.payamgr.qrcodemaker.data.database.dao.TextContentDao
import com.payamgr.qrcodemaker.data.database.entity.MeCardContent
import com.payamgr.qrcodemaker.data.database.entity.PhoneCallContent
import com.payamgr.qrcodemaker.data.database.entity.TextContent

interface QrDatabase {
    fun textContentDao(): TextContentDao
    fun phoneCallContentDao(): PhoneCallContentDao
    fun meCardContentDao(): MeCardContentDao
}

@Database(
    entities = [
        MeCardContent::class,
        PhoneCallContent::class,
        TextContent::class,
    ], version = 1
)
abstract class QrDatabaseImpl : RoomDatabase(), QrDatabase
