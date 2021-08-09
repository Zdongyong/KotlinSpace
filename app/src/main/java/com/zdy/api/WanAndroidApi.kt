package com.zdy.api

import com.zdy.fragment.collect.bean.ArticleBean
import com.zdy.entity.BaseResponse
import com.zdy.entity.LoginResponse
import com.zdy.fragment.collect.bean.RepoResponse
import io.reactivex.Observable
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
     * 获取项目列表
     */
    @GET("search/repositories?sort=stars&q=Android")
    suspend fun searchRepos(@Query("page") page: Int, @Query("per_page") perPage: Int): RepoResponse


}