package com.zdy.fragment

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.flyco.tablayout.SlidingTabLayout
import com.zdy.mykotlin.R
import com.zdy.paging.SongFragment
import java.lang.reflect.Field
import java.util.ArrayList

/**
 * 创建日期：8/15/21 on 7:20 PM
 * 描述：
 * 作者：zhudongyong
 */
class RecordFragment: Fragment() {

    private val mTitles = arrayOf(
        "热门", "专辑"
    )
    val mFragments = ArrayList<Fragment>()

    private lateinit var tabLayout: SlidingTabLayout
    private var mViewPager: ViewPager? = null
    private lateinit var mFragmentAdapter: MyFragmentAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_record, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewPager = view.findViewById(R.id.vp)
        tabLayout = view.findViewById(R.id.stl)
        initViewPager()
    }


    fun initViewPager() {
        mFragments.add(SongFragment())
        mFragments.add(SongFragment())
        mFragmentAdapter = MyFragmentAdapter(
            activity?.supportFragmentManager
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