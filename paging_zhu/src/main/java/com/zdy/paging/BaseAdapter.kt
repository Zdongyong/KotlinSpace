package com.zdy.paging

import androidx.lifecycle.Lifecycle
import androidx.paging.PagingData
import com.zdy.paging.iterm.ExtPagingDataAdapter
import com.zdy.paging.iterm.ItemHelper

/**
 * 创建日期：8/15/21 on 10:50 PM
 * 描述：
 * 作者：zhudongyong
 */
class BaseAdapter(
    vararg viewHolder: BaseViewHolder<*> //设置为可变参数 表示可以配置多个viewHolder
) : ExtPagingDataAdapter<DifferData>(
    itemCallback(
        areItemsTheSame = { old, new ->
            old.areItemsTheSame(new)
        },
        areContentsTheSame = { old, new ->
            old.areContentsTheSame(new)
        },
        getChangePayload = { old, new ->
            old.getChangePayload(new)
        }
    )
) {

    private val holderList = mutableListOf<BaseViewHolder<*>>()

    //每次初始化一个viewHolder就添加一个
    init {
        holderList += viewHolder
    }


    override fun bindData(helper: ItemHelper, data: DifferData?, payloads: MutableList<Any>?) {
        val holder = getHolder(data) ?: return
        helper.setItemHolder(holder)
    }

    override fun getItemLayout(position: Int): Int {
        //没有对应数据类型的holder
        val holder = getHolder(super.getData(position))
            ?: throw RuntimeException("BaseAdapter : no match holder")
        return holder.getLayoutRes()
    }

    /**
     * 设置数据
     */
    fun <T : DifferData> setData(lifecycle: Lifecycle, pagingData: PagingData<T>) {
        super.setPagingData(lifecycle, pagingData as PagingData<DifferData>)
    }

    fun getHolder(data: DifferData?): BaseViewHolder<DifferData>? {
        val differData = data ?: return null
        return holderList.firstOrNull {
            differData::class.java.name == it::class.java.getSuperClassGenericType<BaseViewHolder<*>>().name
        } as? BaseViewHolder<DifferData>
    }


}