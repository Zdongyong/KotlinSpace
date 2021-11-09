package com.zdy.paging

import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.ScaleAnimation
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.zdy.mykotlin.R
import com.zdy.view.BookFlipPageTransformer
import com.zdy.view.CardTransformer
import com.zdy.view.MyPagerHelper
import com.zdy.view.ViewPager2SlowScrollHelper
import com.zdy.view.verticalTab.TabTitle
import com.zdy.view.verticalTab.VerticalTabView
import kotlin.math.abs


/**
 * 创建日期：8/27/21 on 10:20 PM
 * 描述：
 * 作者：zhudongyong
 */
class RecommendActivity : AppCompatActivity(),VerticalTabView.OnTabSelectedListener {

    private var mViewPager: ViewPager2? = null
    private var tablayout: VerticalTabView? = null

    private val tabTitles = mutableListOf<TabTitle>()
    private lateinit var viewPager2SlowScrollHelper: ViewPager2SlowScrollHelper
    private lateinit var myPagerHelper: MyPagerHelper
//    private var bookFlipTransformer = BookFlipPageTransformer(11)
    private var cardTransformer = CardTransformer()
    private var currentPosition = 0

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
//        mViewPager?.isUserInputEnabled = false
//        mViewPager?.orientation = ViewPager2.ORIENTATION_VERTICAL
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
        mViewPager?.setPageTransformer(cardTransformer)

        tabTitles.add(TabTitle("热门", R.mipmap.labour_icon))
        tabTitles.add(TabTitle("专辑", R.mipmap.insurance_icon))
        tabTitles.add(TabTitle("单曲", R.mipmap.technology_icon))
        tabTitles.add(TabTitle("独家", R.mipmap.cost_exchange))
        tabTitles.add(TabTitle("MV", R.mipmap.league_icon))
        tablayout?.setTab(tabTitles)
        mViewPager?.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                tablayout?.setTabSelected(position)
            }
        })
        mViewPager?.isUserInputEnabled = false
        myPagerHelper = mViewPager?.let { MyPagerHelper(it) }!!
        viewPager2SlowScrollHelper = mViewPager?.let { ViewPager2SlowScrollHelper(it,10) }!!
    }

    private fun setPageTransformer(view: View){
        var animator =  ObjectAnimator.ofFloat(view, "rotationY", 0f, 90f)
        animator.duration = 1000
        animator =  ObjectAnimator.ofFloat(view, "rotationY", -90f, 0f)
        animator.duration = 1000
        animator.interpolator = DecelerateInterpolator()
        animator.start()
    }

    private fun setScaleIn(view: View){
        val scaleAnimationIn =  ScaleAnimation(0f, 1.0f, 0f, 1.0f, view.pivotX, view.pivotY)
        scaleAnimationIn.duration = 1000
        scaleAnimationIn.fillAfter = true
        view.startAnimation(scaleAnimationIn)
    }

    private fun setScaleOut(view: View){
        val scaleAnimationOut =  ScaleAnimation(1.0f, 0f, 1.0f, 0f, view.pivotX, view.pivotY)
        scaleAnimationOut.duration = 1000
        scaleAnimationOut.fillAfter = true
        scaleAnimationOut.start()
        view.startAnimation(scaleAnimationOut)
    }



    override fun onTabSelected(position: Int) {
        if (currentPosition == position) return
        if (abs(currentPosition - position) > 1){
            val beforePosition = if (position>currentPosition) position - 1 else position + 1
            Log.d("123123","==$position=======$beforePosition==========")
//            mViewPager?.setCurrentItem(beforePosition, false)
            mViewPager?.let { myPagerHelper.setCurrentItem(beforePosition) }
            Handler().postDelayed(Runnable {
                mViewPager?.let { myPagerHelper.setCurrentItemWithAnimator(tablayout!!,cardTransformer, position) }
            },100)
        }else{
            mViewPager?.let { myPagerHelper.setCurrentItemWithAnimator(tablayout!!,cardTransformer, position) }
        }
        currentPosition = position

    }


}

