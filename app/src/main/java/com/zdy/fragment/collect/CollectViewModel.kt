package com.zdy.fragment.collect

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.zdy.api.WanAndroidApi
import com.zdy.base.BaseViewModel
import com.zdy.fragment.collect.repository.CollectPageSource
import com.zdy.netWork.ApiCilent

/**
 * 创建日期：8/15/21 on 1:01 PM
 * 描述：
 * 作者：zhudongyong
 */
class CollectViewModel : BaseViewModel() {

    val wanAndroidApi = ApiCilent.instance.getAPICilent(true,WanAndroidApi::class.java)

    /**
     * 获取数据
     */
    fun getData() = Pager(PagingConfig(pageSize = 1)) {
        CollectPageSource(wanAndroidApi)
    }.flow

}