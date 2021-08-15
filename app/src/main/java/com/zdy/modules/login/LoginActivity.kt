package com.zdy.modules.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.zdy.MainActivity
import com.zdy.api.WanAndroidApi
import com.zdy.base.BaseActivity
import com.zdy.entity.LoginResponse
import com.zdy.modules.login.inter.LoginPresenter
import com.zdy.modules.login.inter.LoginView
import com.zdy.mykotlin.R
import com.zdy.utils.LOGIN_STATE
import com.zdy.utils.getValue
import com.zdy.utils.putValue
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

    override fun onResume() {
        super.onResume()
        if (getValue(this, LOGIN_STATE,false)){
            val intent = Intent(this@LoginActivity, MainActivity::class .java)
            startActivity(intent)
        }
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
        Toast.makeText(this@LoginActivity, "登录成功 ~ 欧耶", Toast.LENGTH_SHORT).show()
        putValue(this,LOGIN_STATE,true)
        val intent = Intent(this@LoginActivity, MainActivity::class .java)
        startActivity(intent)
    }

    override fun loginFail(errorCode: String, errorMsg: String?) {
        Log.d(TAG, "loginFail: ")
        Toast.makeText(this@LoginActivity, "登录失败 ~ 呜呜呜", Toast.LENGTH_SHORT).show()
    }

    override fun createPresenter(): LoginPresenter = LoginPresenterImpl(this)
}