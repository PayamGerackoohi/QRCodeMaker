package com.payamgr.qrcodemaker.data.database.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update

interface BaseDao<T> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg data: T): List<Long>

    @Update
    suspend fun update(data: T): Int

    @Delete
    suspend fun delete(data: T): Int
}
