package com.zdy.fragment.collect.bean


/**
 * 创建日期：8/10/21 on 12:13 AM
 * 描述：
 * 作者：zhudongyong
 */
class BasePagingResponse<T>(
    val data: T,
    val errorCode: Int,
    val errorMsg: String ?
) {
}