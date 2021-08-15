package com.zdy.fragment.record.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Config
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import cn.leo.retrofit_ktx.http.OkHttp3Creator
import cn.leo.retrofit_ktx.http.ServiceCreator
import com.zdy.api.WanAndroidApi
import com.zdy.fragment.record.bean.RecordBean
import com.zdy.netWork.ApiCilent
import com.zdy.paging.BaseAdapter
import com.zdy.paging.BasePager
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

/**
 * 创建日期：8/15/21 on 11:55 PM
 * 描述：
 * 作者：zhudongyong
 */
class RecordViewModel : ViewModel() {

    val wanAndroidApi = ApiCilent.instance.getAPICilent(true, WanAndroidApi::class.java)

    companion object {
        val api by lazy {
            ServiceCreator.create(WanAndroidApi::class.java) {
                baseUrl = com.zdy.constants.Config.baseUrlZhiHu
                httpClient = OkHttp3Creator.build {
                }
            }
        }
    }

    private val mDate = Calendar.getInstance().apply {
        add(Calendar.DATE, 1)
    }

    private val initialKey = SimpleDateFormat("yyyyMMdd", Locale.CHINA)
        .format(mDate.time)
        .toLong()

    private val pager =
        object : BasePager<Long, RecordBean.StoriesBean>(
            PagingConfig(15, initialLoadSize = 15),
            initialKey
        ) {
            override suspend fun loadData(params: PagingSource.LoadParams<Long>):
                    PagingSource.LoadResult<Long, RecordBean.StoriesBean> {
                val date =
                    params.key ?: return PagingSource.LoadResult.Page(emptyList(), null, null)
                return try {
                    val data = api.getNews(date).await()
                    PagingSource.LoadResult.Page(data.stories, null, data.date?.toLongOrNull())
                } catch (e: Exception) {
                    PagingSource.LoadResult.Error(e)
                }
            }
        }

    val data = pager.getData(viewModelScope)

}