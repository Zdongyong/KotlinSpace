package com.zdy.modules.login.inter

import android.content.Context
import com.zdy.base.IBasePresenter
import com.zdy.entity.LoginResponse

/**
 * 创建日期：8/1/21 on 11:04 PM
 * 描述：
 * 作者：zhudongyong
 */
interface LoginPresenter : IBasePresenter {

    fun loginAction(context: Context, username: String, password: String);

    interface LoginListener {
        fun loginSuccess(loginBean: LoginResponse?);
        fun loginFail(errorCode: String, errorMsg: String?)
    }
}