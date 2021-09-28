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
class PagingActivity : AppCompatActivity() {

    private val mTitles = arrayOf(
        "热门", "专辑", "单曲", "MV"
    )

    private var mViewPager: ViewPager2? = null
    private var tablayout: TabLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "古力-Paging"
        setContentView(R.layout.activity_tab_paging)
        mViewPager = findViewById(R.id.view_pager)
        tablayout = findViewById(R.id.tablayout)
        initViewPager()
    }

    private fun initViewPager() {
        mViewPager?.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount() = 4

            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> SongFragment("热门")
                    1 -> SongFragment("专辑")
                    2 -> SongFragment("单曲")
                    else -> SongFragment("MV")
                }
            }
        }

        TabLayoutMediator(
            tablayout!!,
            mViewPager!!,
            object : TabLayoutMediator.TabConfigurationStrategy {
                override fun onConfigureTab(tab: TabLayout.Tab, position: Int) {
                    when (position) {
                        0 -> tab.text = mTitles[0]
                        1 -> tab.text = mTitles[1]
                        2 -> tab.text = mTitles[2]
                        else -> tab.text = mTitles[3]
                    }
                }
            }).attach()
    }


}