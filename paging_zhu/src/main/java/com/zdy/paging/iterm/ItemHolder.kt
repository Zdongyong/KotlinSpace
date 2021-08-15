package com.zdy.paging.iterm

/**
 * 创建日期：8/15/21 on 8:52 PM
 * 描述：
 * 作者：zhudongyong
 */
abstract class ItemHolder<T> {

    abstract fun bindData(helper: ItemHelper, data: T?, payloads: MutableList<Any>? = null)

    /**
     * 初始化view，只在view第一次创建调用
     *
     * @param helper 帮助类
     * @param item   数据
     */
    open fun initView(helper: ItemHelper, data: T?) {}

}