package com.zdy.paging

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.zdy.mykotlin.R
import q.rorbin.verticaltablayout.VerticalTabLayout


/**
 * 创建日期：8/27/21 on 10:20 PM
 * 描述：
 * 作者：zhudongyong
 */
class RecommendActivity : AppCompatActivity() {

    private var mViewPager: ViewPager2? = null
    private var tablayout: VerticalTabLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tab_recommend)
        mViewPager = findViewById(R.id.view_pager)
        tablayout = findViewById(R.id.tv_tab_title)
        initViewPager()
    }

    private fun initViewPager() {
//        tablayout?.setupWithViewPager(mViewPager);//绑定
//        tablayout?.setTabAdapter(MyTabAdapter())
        mViewPager?.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount() = 4

            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> SongFragment()
                    1 -> SongFragment()
                    2 -> SongFragment()
                    else -> SongFragment()
                }
            }
        }

    }


}

