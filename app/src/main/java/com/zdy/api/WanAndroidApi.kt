package com.zdy.api

import com.zdy.entity.BaseResponse
import com.zdy.entity.LoginResponse
import com.zdy.fragment.collect.bean.BasePagingResponse
import com.zdy.fragment.collect.bean.CollectBean
import com.zdy.fragment.record.bean.RecordBean
import io.reactivex.Observable
import kotlinx.coroutines.Deferred
import retrofit2.http.*

/**
 * 创建日期：8/1/21 on 2:51 PM
 * 描述：
 * 作者：zhudongyong
 */
interface WanAndroidApi {


    /**
     * 登录
     */
    @POST("/user/login")
    @FormUrlEncoded
    fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): Observable<BaseResponse<LoginResponse>>

    /**
     * 获取数据
     */
    @GET("wenda/list/{pageId}/json")
    suspend fun getData(@Path("pageId") pageId: Int): BasePagingResponse<CollectBean>

    /**
     * 知乎日报历史记录
     */
    @GET("before/{time}")
    fun getNews(@Path("time") time: Long): Deferred<RecordBean>


}