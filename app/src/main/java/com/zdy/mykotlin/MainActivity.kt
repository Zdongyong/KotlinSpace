package com.zdy.mykotlin

import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.zdy.api.WanAndroidApi
import com.zdy.netWork.ApiCilent

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //WanAndroidApi.class = WanAndroidApi::class.java
        ApiCilent.instance.getAPICilent(WanAndroidApi::class.java)
    }

}