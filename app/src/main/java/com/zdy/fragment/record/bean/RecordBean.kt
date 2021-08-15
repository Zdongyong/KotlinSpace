package com.zdy.fragment.record.bean

import com.zdy.paging.DifferData

/**
 * 创建日期：8/15/21 on 11:23 PM
 * 描述：
 * 作者：zhudongyong
 */
class RecordBean(
    var date: String? = "",
    var stories: List<StoriesBean> = emptyList()
) {
    data class StoriesBean(
        var type: Int = 0,
        var id: Int = 0,
        var ga_prefix: String? = null,
        var title: String? = null,
        var url: String? = null,
        var images: List<String>? = null
    ) : DifferData {
        override fun areItemsTheSame(d: DifferData): Boolean {
            return (d as? StoriesBean)?.id == id
        }

        override fun areContentsTheSame(d: DifferData): Boolean {
            return (d as? StoriesBean)?.title == title
        }
    }

}