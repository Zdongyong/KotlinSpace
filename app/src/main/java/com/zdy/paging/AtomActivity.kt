package com.zdy.paging

import android.os.Bundle
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.zdy.mykotlin.R

/**
 * 创建日期：8/27/21 on 10:20 PM
 * 描述：
 * 作者：zhudongyong
 */
class AtomActivity : AppCompatActivity() {

    private val mTitles = arrayOf(
        "作者主页", "我的订阅"
    )

    private var mViewPager: ViewPager2? = null
    private var tablayout: TabLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "古力-Paging"
        setContentView(R.layout.activity_tab_atom)
        mViewPager = findViewById(R.id.view_pager)
        tablayout = findViewById(R.id.tablayout)
        initViewPager()
    }

    private fun initViewPager() {
        mViewPager?.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount() = 2

            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> AtomFragment("作者主页")
                    else -> AtomFragment("我的订阅")
                }
            }
        }

        TabLayoutMediator(
            tablayout!!,
            mViewPager!!
        ) { tab, position ->
            when (position) {
                0 -> tab.text = mTitles[0]
                else -> tab.text = mTitles[1]
            }
        }.attach()
    }


}