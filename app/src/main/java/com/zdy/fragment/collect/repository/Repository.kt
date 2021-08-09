package com.zdy.fragment.collect.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.zdy.api.WanAndroidApi
import com.zdy.fragment.collect.bean.ArticleBean
import com.zdy.netWork.ApiCilent
import kotlinx.coroutines.flow.Flow

/**
 * 创建日期：8/10/21 on 12:18 AM
 * 描述：
 * 作者：zhudongyong
 */
object Repository {

    private const val PAGE_SIZE = 50

    val wanAndroidApi = ApiCilent.instance.getAPICilent(WanAndroidApi::class.java);

    fun getPagingData(): Flow<PagingData<ArticleBean>> {
        return Pager(
            config = PagingConfig(PAGE_SIZE),
            pagingSourceFactory = { RepoPagingSource(wanAndroidApi) }
        ).flow
    }

}