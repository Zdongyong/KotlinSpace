package com.zdy.api

import android.content.Context
import com.zdy.entity.BaseResponse
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import java.util.*

/**
 * 创建日期：8/1/21 on 4:44 PM
 * 描述：
 * 作者：zhudongyong
 */
abstract class APIResponse<T>(val context: Context) : Observer<BaseResponse<T>> {

    private var isShow: Boolean = false;

    constructor(context: Context, show: Boolean) : this(context) {
        this.isShow = show;
    }

    abstract fun success(data: T?)

    abstract fun fail(errorCode: String, errorMsg: String?)


    override fun onSubscribe(d: Disposable) {
// 弹出 加载框
        if (isShow) {
//            LoadingDialog.show(context)
        }
    }

    override fun onNext(t: BaseResponse<T>) {
        if (null == t.data) {
            fail(t.errorCode, t.errorMsg)
        } else {
            success(t.data)
        }
    }

    override fun onError(e: Throwable) {
        fail("-1", e.message)
    }

    override fun onComplete() {
        // 取消加载
//        LoadingDialog.cancel()
    }
}