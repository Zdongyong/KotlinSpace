package com.zdy.paging

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.zdy.mykotlin.R
import kotlin.math.abs

/**
 * 创建日期：8/27/21 on 10:20 PM
 * 描述：
 * 作者：zhudongyong
 */
class LinkageActivity : AppCompatActivity() {

    private val mTitles = arrayOf(
        "文章", "视频"
    )

    private var mViewPager: ViewPager2? = null
    private var tablayout: TabLayout? = null
    private var appBar: AppBarLayout? = null
    private var mHeadImage: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tab_linkage)
        mViewPager = findViewById(R.id.view_pager)
        tablayout = findViewById(R.id.tablayout)
        appBar = findViewById(R.id.app_bar)
        mHeadImage = findViewById(R.id.mHeadImage)
        initViewPager()
        initAnim()
    }

    private fun initAnim(){
        val mSelfHeight = 0
        val mHeadImgScale = 0
        val toolbarHeight = resources.getDimension(R.dimen.toolbar_height);
        val initHeight = resources.getDimension(R.dimen.subscription_head);
        appBar?.addOnOffsetChangedListener(OnOffsetChangedListener { appBarLayout, verticalOffset ->
            Log.i("123123", "======${appBarLayout.totalScrollRange}========$verticalOffset======")
            if (mSelfHeight === 0) {
//                mSelfHeight = mSubscriptionTitle.getHeight()
//                val distanceTitle: Float =
//                    mSubscriptionTitle.getTop() + (mSelfHeight - toolbarHeight) / 2.0f
//                val distanceSubscribe: Float =
//                    mSubscribe.getY() + (mSubscribe.getHeight() - toolbarHeight) / 2.0f
//                val distanceHeadImg: Float =
//                    mHeadImage.getY() + (mHeadImage.getHeight() - toolbarHeight) / 2.0f
//                val distanceSubscribeX: Float =
//                    screenW / 2.0f - (mSubscribe.getWidth() / 2.0f + resources.getDimension(R.dimen.normal_space))
//                mTitleScale = distanceTitle / (initHeight - toolbarHeight)
//                mSubScribeScale = distanceSubscribe / (initHeight - toolbarHeight)
//                mHeadImgScale = distanceHeadImg / (initHeight - toolbarHeight)
//                mSubScribeScaleX = distanceSubscribeX / (initHeight - toolbarHeight)
            }
            val size = -verticalOffset.toFloat()
            val total = appBarLayout.totalScrollRange.toFloat()
            val percentage = (1.0f - size / total).toDouble()
//            val scale: Float = 1.0f - -verticalOffset / (initHeight - toolbarHeight)
            mHeadImage?.scaleX = percentage.toFloat()
            mHeadImage?.scaleY = percentage.toFloat()
//            mHeadImage?.alpha = percentage
//            mHeadImage?.translationY = mHeadImgScale * verticalOffset
//            mSubscriptionTitle.setTranslationY(mTitleScale * verticalOffset)
//            mSubscribe.setTranslationY(mSubScribeScale * verticalOffset)
//            mSubscribe.setTranslationX(-mSubScribeScaleX * verticalOffset)
        })
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


    override fun onDestroy() {
        super.onDestroy()
    }


}