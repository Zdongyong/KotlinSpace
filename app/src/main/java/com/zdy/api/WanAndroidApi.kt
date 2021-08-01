package com.zdy.api

import com.zdy.entity.BaseResponse
import com.zdy.entity.LoginResponse
import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * 创建日期：8/1/21 on 2:51 PM
 * 描述：
 * 作者：zhudongyong
 */
interface WanAndroidApi<T> {


    /**
     * 登录
     */
    @POST("/user/login")
    @FormUrlEncoded
    fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): Observable<BaseResponse<LoginResponse>>



}