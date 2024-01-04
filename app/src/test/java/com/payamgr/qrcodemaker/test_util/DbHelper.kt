package com.payamgr.qrcodemaker.test_util

import com.payamgr.qrcodemaker.data.database.dao.MeCardContentDao
import com.payamgr.qrcodemaker.data.database.dao.PhoneCallContentDao
import com.payamgr.qrcodemaker.data.database.dao.TextContentDao
import com.payamgr.qrcodemaker.data.database.entity.MeCardContent
import com.payamgr.qrcodemaker.data.database.entity.PhoneCallContent
import com.payamgr.qrcodemaker.data.database.entity.TextContent
import com.payamgr.qrcodemaker.data.model.Content

@Suppress("RedundantSuspendModifier", "MemberVisibilityCanBePrivate", "UNUSED_PARAMETER")
open class DbHelper<T : Content> {
    private val list = mutableListOf<T>()

    fun getAllCall() {}
    fun insertCall(vararg data: T) {}
    fun updateCall(data: T) {}
    fun deleteCall(data: T) {}

    suspend fun getAll(): List<T> {
        getAllCall()
        return list
    }

    suspend fun insert(vararg data: T): List<Long> {
        insertCall(*data)
        list.addAll(data)
        return listOf()
    }

    suspend fun update(data: T): Int {
        updateCall(data)
        val index = list.indexOfFirst { it.id == data.id }
        list[index] = data
        return 0
    }

    suspend fun delete(data: T): Int {
        deleteCall(data)
        val index = list.indexOfFirst { it.id == data.id }
        list.removeAt(index)
        return 0
    }
}

@Suppress("UNCHECKED_CAST")
object DbHelperCastExtensions {
    fun TextContentDao.asDbHelper() = this as DbHelper<TextContent>
    fun PhoneCallContentDao.asDbHelper() = this as DbHelper<PhoneCallContent>
    fun MeCardContentDao.asDbHelper() = this as DbHelper<MeCardContent>
}
