package com.zdy.modules.login.inter

import com.zdy.entity.LoginResponse

/**
 * 创建日期：8/1/21 on 11:03 PM
 * 描述：
 * 作者：zhudongyong
 */
interface LoginView {

    fun loginSuccess(loginBean: LoginResponse?);

    fun loginFail(errorCode: String, errorMsg: String?)

}