package com.zdy.fragment.collect.bean


data class CollectBean(

    val curPage: Int,
    val offset: Int,
    val over: Boolean,
    val pageCount: Int,
    val size: Int,
    val total: Int,
    val datas: List<DataBean>


)