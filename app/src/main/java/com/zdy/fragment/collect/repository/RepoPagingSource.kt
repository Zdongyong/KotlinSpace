package com.zdy.fragment.collect.repository

import androidx.paging.PagingSource
import com.zdy.api.WanAndroidApi
import com.zdy.fragment.collect.bean.ArticleBean

/**
 * 创建日期：8/10/21 on 12:15 AM
 * 描述：
 * 作者：zhudongyong
 */
class RepoPagingSource(val wanAndroidApi: WanAndroidApi) : PagingSource<Int, ArticleBean>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ArticleBean> {

        return try {
            val page = params.key ?: 1 // set page 1 as default
            val pageSize = params.loadSize
            val repoResponse = wanAndroidApi.searchRepos(page, pageSize)
            val repoItems = repoResponse.items
            val prevKey = if (page > 1) page - 1 else null
            val nextKey = if (repoItems.isNotEmpty()) page + 1 else null
            LoadResult.Page(repoItems, prevKey, nextKey)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}