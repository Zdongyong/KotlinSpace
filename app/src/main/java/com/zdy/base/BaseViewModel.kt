package com.zdy.base

import android.util.Log
import androidx.lifecycle.ViewModel

/**
 * 创建日期：8/15/21 on 3:01 PM
 * 描述：
 * 作者：zhudongyong
 */
open class BaseViewModel : ViewModel() {

    companion object {
        val TAG: String = "BaseViewModel"
    }

    override fun onCleared() {
        super.onCleared()
        Log.e(TAG, "onCleared")
    }

}