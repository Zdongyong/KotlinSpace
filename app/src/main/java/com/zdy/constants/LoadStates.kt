package com.zdy.constants

import androidx.paging.LoadState

/**
 * 创建日期：8/15/21 on 3:50 PM
 * 描述：
 * 作者：zhudongyong
 */
data class LoadStates(
    /** [LoadState] corresponding to [LoadType.REFRESH] loads. */
    val refresh: LoadState,
    /** [LoadState] corresponding to [LoadType.PREPEND] loads. */
    val prepend: LoadState,
    /** [LoadState] corresponding to [LoadType.APPEND] loads. */
    val append: LoadState
)
