package com.zdy.base

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.zdy.modules.login.inter.LoginPresenter

/**
 * 创建日期：8/1/21 on 11:44 PM
 * 描述：
 * 作者：zhudongyong
 */
// public final class
// P extends LoginPresenter                     vs     P: LoginPresenter
// P extends LoginPresenter & Serializable      vs     where P : IBasePresenter,  P: Serializable
abstract class BaseActivity<P> : AppCompatActivity() where P : IBasePresenter {

    lateinit var presenter: P

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        initView()
//        initData()
        presenter = createPresenter()
    }
//    abstract fun setContentView()

//    abstract fun initView()
//
//    abstract fun initData()

    abstract fun createPresenter(): P

    override fun onDestroy() {
        super.onDestroy()
    }

}