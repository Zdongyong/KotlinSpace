package com.zdy.modules.login

import android.content.Context
import android.util.Log
import com.zdy.api.APIResponse
import com.zdy.api.WanAndroidApi
import com.zdy.entity.LoginResponse
import com.zdy.modules.login.inter.LoginModule
import com.zdy.modules.login.inter.LoginPresenter
import com.zdy.netWork.ApiCilent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * 创建日期：8/1/21 on 11:14 PM
 * 描述：
 * 作者：zhudongyong
 */
class LoginModuleImpl : LoginModule {

    private val TAG: String = "login"

    override fun cancleLogin() {
        TODO("Not yet implemented")
    }

    override fun LoginAction(
        context: Context,
        username: String,
        password: String,
        loginListener: LoginPresenter.LoginListener
    ) {
        ApiCilent.instance.getAPICilent(WanAndroidApi::class.java)
            .login(username, password)
            .subscribeOn(Schedulers.io())//切换线程
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : APIResponse<LoginResponse>(context) {
                override fun success(data: LoginResponse?) {
                    Log.i(TAG, "success: ${data.toString()}")
                    loginListener.loginSuccess(data)
                }

                override fun fail(errorCode: String, errorMsg: String?) {
                    Log.i(TAG, "fail: $errorMsg")
                    loginListener.loginFail(errorCode, errorMsg)
                }

            })

    }


}