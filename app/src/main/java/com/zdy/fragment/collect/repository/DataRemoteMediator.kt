package com.zdy.fragment.collect.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.zdy.fragment.collect.bean.DataBean

/**
 * 创建日期：8/15/21 on 4:23 PM
 * 描述：
 * 作者：zhudongyong
 */
@ExperimentalPagingApi
class DataRemoteMediator: RemoteMediator<Int, DataBean>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, DataBean>
    ): MediatorResult {
        TODO("Not yet implemented")
    }


}