package com.zdy.fragment.collect.repository

import android.util.Log
import androidx.paging.PagingSource
import com.zdy.api.WanAndroidApi
import com.zdy.fragment.collect.bean.DataBean
import java.io.IOException

/**
 * 创建日期：8/10/21 on 12:15 AM
 * 描述：
 * 作者：zhudongyong
 */
class CollectPageSource constructor(
    var wanAndroidApi: WanAndroidApi
): PagingSource<Int, DataBean>(){

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DataBean> {

        return try {

            //页码未定义置为1
            var currentPage = params.key ?: 1
            //仓库层请求数据
            Log.d("MainActivity", "请求第${currentPage}页")
            var demoReqData = wanAndroidApi.getData(currentPage)
            //当前页码 小于 总页码 页面加1
            var nextPage = if (currentPage < demoReqData.data?.pageCount ?: 0) {
                currentPage + 1
            } else {
                //没有更多数据
                null
            }

            LoadResult.Page(
                data = demoReqData.data.datas,
                prevKey = null,
                nextKey = nextPage
            )


        } catch (e: Exception) {
            if (e is IOException) {
                Log.d("测试错误数据", "-------连接失败")
            }
            Log.d("测试错误数据", "-------${e.message}")
            LoadResult.Error(throwable = e)
        }

    }


}