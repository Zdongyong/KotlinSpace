package com.zdy.netWork

import com.zdy.Constants.Config
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * 创建日期：8/1/21 on 3:26 PM
 * 描述：
 * 作者：zhudongyong
 */
class ApiCilent {

    //全局单例
    private object Holder {

        val INSTANCE = ApiCilent()

    }

    companion object {

        val instance = Holder.INSTANCE;

    }

    //初始化网络
    fun <T> getAPICilent(apiInterface: Class<T>): T {

        //创建okhttpCilent
        val okHttpClient = OkHttpClient().newBuilder()
            // 添加读取超时时间
            .readTimeout(10000, TimeUnit.SECONDS)

            // 添加连接超时时间
            .connectTimeout(10000, TimeUnit.SECONDS)

            // 添加写出超时时间
            .writeTimeout(10000, TimeUnit.SECONDS)
            .build()

        //创建Retrofit
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(Config.baseUrl)
            .client(okHttpClient)//请求
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())//交给RxJava来处理
            .addConverterFactory(GsonConverterFactory.create())//返回值交给Gson
            .build()

        return retrofit.create(apiInterface);
    }




}