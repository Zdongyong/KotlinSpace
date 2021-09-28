package com.zdy.paging

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.zdy.mykotlin.R
import com.zdy.view.verticalTab.TabTitle
import com.zdy.view.verticalTab.VerticalTabView


/**
 * 创建日期：8/27/21 on 10:20 PM
 * 描述：
 * 作者：zhudongyong
 */
class RecommendActivity : AppCompatActivity(),VerticalTabView.OnTabSelectedListener {

    private var mViewPager: ViewPager2? = null
    private var tablayout: VerticalTabView? = null

    private val tabTitles = mutableListOf<TabTitle>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "古力-推荐"
        setContentView(R.layout.activity_tab_recommend)
        mViewPager = findViewById(R.id.view_pager)
        tablayout = findViewById(R.id.tv_tab_title)
        initViewPager()
    }

    private fun initViewPager() {
        tablayout?.setOnTabSelectedListener(this)
        mViewPager?.isUserInputEnabled = false
        mViewPager?.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount() = 5

            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> SongFragment("热门")
                    1 -> SongFragment("专辑")
                    2 -> SongFragment("单曲")
                    3 -> SongFragment("独家")
                    else -> SongFragment("MV")
                }
            }

        }

        tabTitles.add(TabTitle("热门",R.mipmap.labour_icon))
        tabTitles.add(TabTitle("专辑",R.mipmap.insurance_icon))
        tabTitles.add(TabTitle("单曲",R.mipmap.technology_icon))
        tabTitles.add(TabTitle("独家",R.mipmap.cost_exchange))
        tabTitles.add(TabTitle("MV",R.mipmap.league_icon))
        tablayout?.setTab(tabTitles)
    }

    override fun onTabSelected(position: Int) {
        mViewPager?.currentItem = position
    }


}

