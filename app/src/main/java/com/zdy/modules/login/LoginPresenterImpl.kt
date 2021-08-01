package com.zdy.modules.login

import android.content.Context
import com.zdy.entity.LoginResponse
import com.zdy.modules.login.inter.LoginPresenter

/**
 * 创建日期：8/1/21 on 11:20 PM
 * 描述：
 * 作者：zhudongyong
 */
class LoginPresenterImpl(val loginView: LoginActivity) : LoginPresenter,
    LoginPresenter.LoginListener {

    private val loginModuleImpl: LoginModuleImpl = LoginModuleImpl()

    override fun loginAction(context: Context, username: String, password: String) {
        //验证username和password
        loginModuleImpl.LoginAction(context, username, password, this)
    }

    override fun loginSuccess(loginBean: LoginResponse?) {
        loginView.loginSuccess(loginBean)
    }

    override fun loginFail(errorCode: String, errorMsg: String?) {
        loginView.loginFail(errorCode, errorMsg)
    }


}