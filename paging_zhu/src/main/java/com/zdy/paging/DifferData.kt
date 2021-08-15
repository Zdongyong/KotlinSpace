package com.zdy.paging

import androidx.annotation.NonNull

/**
 * 创建日期：8/15/21 on 8:05 PM
 * 描述：
 * 作者：zhudongyong
 */
interface DifferData {

    /**
     * 检测两个字段是否具有相同数据
     */
    fun areItemsTheSame(data: DifferData): Boolean {
        return this == data
    }

    /**
     * 检查两个对象是否表示同一项
     */
    fun areContentsTheSame(data: DifferData): Boolean {
        return this == data
    }

    /**
     * <可选> 局部刷新
     */
    fun getChangePayload(d: DifferData): Any? {
        return null
    }

}