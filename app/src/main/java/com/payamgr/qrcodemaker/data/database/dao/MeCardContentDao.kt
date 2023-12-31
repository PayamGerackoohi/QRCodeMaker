package com.payamgr.qrcodemaker.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.payamgr.qrcodemaker.data.database.entity.MeCardContent

@Dao
interface MeCardContentDao : BaseDao<MeCardContent> {
    @Query("SELECT * FROM me_card_content")
    suspend fun getAll(): List<MeCardContent>
}
