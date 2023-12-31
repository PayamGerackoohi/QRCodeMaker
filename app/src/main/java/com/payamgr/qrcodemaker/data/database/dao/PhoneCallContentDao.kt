package com.payamgr.qrcodemaker.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.payamgr.qrcodemaker.data.database.entity.PhoneCallContent

@Dao
interface PhoneCallContentDao : BaseDao<PhoneCallContent> {
    @Query("SELECT * FROM phone_call_content")
    suspend fun getAll(): List<PhoneCallContent>
}
