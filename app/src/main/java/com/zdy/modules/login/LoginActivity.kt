package com.zdy.modules.login

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.zdy.api.WanAndroidApi
import com.zdy.base.BaseActivity
import com.zdy.entity.LoginResponse
import com.zdy.modules.login.inter.LoginPresenter
import com.zdy.modules.login.inter.LoginView
import com.zdy.mykotlin.R
import kotlinx.android.synthetic.main.activity_user_login.*

class LoginActivity : BaseActivity<LoginPresenter>(), LoginView {

    private val TAG: String = "login"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_login)
        //WanAndroidApi.class = WanAndroidApi::class.java
//        ApiCilent.instance.getAPICilent(WanAndroidApi::class.java)
        user_login_bt.setOnClickListener(onClickListener)
    }

    private val onClickListener = View.OnClickListener { view ->

        when (view.id) {
            R.id.user_login_bt -> {
                val username: String = user_phone_et.text.toString()
                val password: String = user_password_et.text.toString()
                Log.i(TAG, "username: $username , password: $password")
                presenter.loginAction(this@LoginActivity, username, password)
            }
        }

    }

    override fun loginSuccess(loginBean: LoginResponse?) {
        Log.d(TAG, "loginSuccess: ")
    }

    override fun loginFail(errorCode: String, errorMsg: String?) {
        Log.d(TAG, "loginFail: ")
    }

    override fun createPresenter(): LoginPresenter = LoginPresenterImpl(this)
}