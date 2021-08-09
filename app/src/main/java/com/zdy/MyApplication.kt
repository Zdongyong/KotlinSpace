package com.zdy

import android.app.Application
import com.zdy.dataBase.StudentDataBase

/**
 * 创建日期：8/3/21 on 12:51 AM
 * 描述：
 * 作者：zhudongyong
 */
class MyApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        StudentDataBase.getDatabase(this)
    }
}