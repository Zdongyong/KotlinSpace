package com.zdy.fragment.collect.bean

import com.google.gson.annotations.SerializedName

/**
 * 创建日期：8/10/21 on 12:13 AM
 * 描述：
 * 作者：zhudongyong
 */
class RepoResponse {

    @SerializedName("items") val items: List<ArticleBean> = emptyList()

}