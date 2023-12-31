package com.payamgr.qrcodemaker.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.payamgr.qrcodemaker.data.database.entity.TextContent

@Dao
interface TextContentDao : BaseDao<TextContent> {
    @Query("SELECT * FROM text_content")
    suspend fun getAll(): List<TextContent>
}
