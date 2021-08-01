package com.zdy.modules.login.inter

import android.content.Context

/**
 * 创建日期：8/1/21 on 11:03 PM
 * 描述：
 * 作者：zhudongyong
 */
interface LoginModule {

    fun cancleLogin()

    fun LoginAction(
        context: Context,
        username: String,
        password: String,
        loginListener: LoginPresenter.LoginListener
    )
}