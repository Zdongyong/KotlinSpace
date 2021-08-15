package com.zdy.fragment.collect

import androidx.paging.PagingSource
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zdy.fragment.collect.bean.DataBean

/**
 * 创建日期：8/15/21 on 5:28 PM
 * 描述：
 * 作者：zhudongyong
 */
interface CollectDao {

    /**
     * 插入数据
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<DataBean>)

    /**
     * 查询作者名为 query的数据
     */
    @Query("SELECT * FROM collect WHERE chapterName LIKE :query")
    fun pagingSource(query: String): PagingSource<Int, DataBean>

    /**
     * 清除全部
     */
    @Query("DELETE FROM collect")
    suspend fun clearAll()

}