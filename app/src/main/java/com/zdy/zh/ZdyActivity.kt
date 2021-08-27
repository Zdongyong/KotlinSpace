package com.zdy.zh

import android.os.Bundle
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.flyco.tablayout.SlidingTabLayout
import com.zdy.mykotlin.R
import java.lang.reflect.Field
import java.util.*

/**
 * 创建日期：8/27/21 on 10:20 PM
 * 描述：
 * 作者：zhudongyong
 */
class ZdyActivity : AppCompatActivity() {

    private val mTitles = arrayOf(
        "热门", "专辑"
    )
    val mFragments = ArrayList<Fragment>()

    private lateinit var tabLayout: SlidingTabLayout
    private var mViewPager: ViewPager? = null
    private lateinit var mFragmentAdapter: MyFragmentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zdy)
        mViewPager = findViewById(R.id.vp)
        tabLayout = findViewById(R.id.stl)
        initViewPager()
    }

    fun initViewPager() {
        mFragments.add(SongFragment())
        mFragments.add(SongFragment())
        mFragmentAdapter = MyFragmentAdapter(
            supportFragmentManager
        )
        mViewPager?.adapter = mFragmentAdapter

        try {
            val mTouchSlop: Field = ViewPager::class.java.getDeclaredField("mTouchSlop")
            mTouchSlop.isAccessible = true
            mTouchSlop.setInt(mViewPager, dp2px(50f))
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
        mViewPager!!.offscreenPageLimit = mFragments.size
        tabLayout.setViewPager(mViewPager, mTitles)
    }


    inner class MyFragmentAdapter constructor(
        private val fm: FragmentManager?
    ) : FragmentPagerAdapter(fm!!) {

        override fun getCount(): Int {
            return mFragments.size
        }

        override fun getItem(position: Int): Fragment {
            return mFragments.get(position)
        }
    }

    fun dp2px(dpVal: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dpVal, resources.displayMetrics
        ).toInt()
    }

}