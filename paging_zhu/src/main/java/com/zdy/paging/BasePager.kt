package com.zdy.paging

import androidx.paging.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

/**
 * 创建日期：8/15/21 on 10:22 PM
 * 描述：
 * 作者：zhudongyong
 */
abstract class BasePager<K : Any, V : Any>(
    private val pagingConfig: PagingConfig,
    private val initialKey: K? = null
) {

    abstract suspend fun loadData(params: PagingSource.LoadParams<K>):
            PagingSource.LoadResult<K, V>

    fun getData(scope: CoroutineScope): Flow<PagingData<V>> {
        return Pager(pagingConfig, initialKey = initialKey) {
            object : PagingSource<K, V>() {
                override suspend fun load(params: LoadParams<K>): LoadResult<K, V> {
                    return loadData(params)
                }
            }
        }.flow.cachedIn(scope)
    }
}