package com.zdy.paging

import androidx.annotation.LayoutRes
import com.zdy.paging.iterm.ItemHolder

/**
 * 创建日期：8/15/21 on 10:12 PM
 * 描述：
 * 作者：zhudongyong
 */
abstract class BaseViewHolder<T : DifferData> : ItemHolder<T>() {

    @LayoutRes
    abstract fun getLayoutRes(): Int

}