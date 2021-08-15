package com.zdy.fragment.record.viewHolder

import android.widget.ImageView
import com.zdy.fragment.record.bean.RecordBean
import com.zdy.mykotlin.R
import com.zdy.paging.BaseViewHolder
import com.zdy.paging.iterm.ItemHelper

/**
 * 创建日期：8/15/21 on 11:21 PM
 * 描述：
 * 作者：zhudongyong
 */
class RecordViewHolder: BaseViewHolder<RecordBean.StoriesBean>() {

    override fun getLayoutRes(): Int {
        return R.layout.item_record
    }

    override fun bindData(
        helper: ItemHelper,
        data: RecordBean.StoriesBean?,
        payloads: MutableList<Any>?
    ) {
        data.let {
            helper.setText(R.id.tv_title,data?.title)
        }
    }


}