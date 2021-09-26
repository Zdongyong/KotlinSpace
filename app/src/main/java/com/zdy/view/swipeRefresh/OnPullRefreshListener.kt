package com.zdy.view.swipeRefresh

/**
 * 创建日期：8/18/21 on 10:26 PM
 * 描述：
 * 作者：zhudongyong
 */
interface OnPullRefreshListener {

    fun onRefresh()

//    fun onPullDistance(distance: Int)

    fun onPullEnable(enable: Boolean)

}