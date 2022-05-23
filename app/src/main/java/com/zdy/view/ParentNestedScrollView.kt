package com.example.meterialproject.view.nested

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import androidx.core.view.NestedScrollingParent3
import androidx.core.view.NestedScrollingParentHelper
import androidx.core.view.ViewCompat
import androidx.core.view.children

/**
 *
 * @ClassName: ParentNestedScrollView
 * @Author: android 超级兵
 * @CreateDate: 4/7/22$ 3:41 PM$
 * TODO
 */
class ParentNestedScrollView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr), NestedScrollingParent3 {

    private val TAG:String = "ParentNestedScrollView"

    private val parentHelper by lazy {
        NestedScrollingParentHelper(this)
    }

    // 第一个View
    private val firstView by lazy {
        children.first()
    }

    private var mChildHeight = 0

    @SuppressLint("LongLogTag")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var tempHeightMeasureSpec = heightMeasureSpec
        mChildHeight = 0
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(tempHeightMeasureSpec)

        children.forEach {
            tempHeightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.UNSPECIFIED)

            // 测量子view
            measureChild(it, widthMeasureSpec, tempHeightMeasureSpec)
            Log.i(
                "szjParentHeight",
                "name:${it::class.java.simpleName}\theight:${it.measuredHeight}"
            )
            mChildHeight += it.measuredHeight
        }


        setMeasuredDimension(widthSize, heightSize)
    }

    /*
     * 作者:android 超级兵
     * 创建时间: 4/7/22 4:51 PM
     * TODO  子view调用 startNestedScroll()时候执行
     */
    override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean =
        let {
            Log.i(
                TAG,
                "parent onStartNestedScroll child:" +
                        child::class.java.simpleName +
                        "\ttarget:${target::class.java.simpleName}" +
                        "\taxes:${axes == ViewCompat.SCROLL_AXIS_VERTICAL}" +
                        "\ttype:${type}"
            )
            true
        }

    /*
     * 作者:android 超级兵
     * 创建时间: 4/7/22 4:52 PM
     * TODO 如果onStartNestedScroll()返回true的话,就会紧接着调用该方法
     *  常用来做一些初始化工作
     */
    override fun onNestedScrollAccepted(child: View, target: View, axes: Int, type: Int) {
        Log.i(
            TAG,
            "parent onNestedScrollAccepted child:${child::class.java.simpleName}" +
                    "\ttarget:${target::class.java.simpleName}" +
                    "taxes:${axes}\ttype:${type}"
        )
        parentHelper.onNestedScrollAccepted(child, target, axes, type)
    }

    /*
     * 作者:android 超级兵
     * 创建时间: 4/7/22 4:55 PM
     * TODO 当子view调用 stopNestedScroll() 时候调用
     */
    override fun onStopNestedScroll(target: View, type: Int) {
        Log.i(
            TAG,
            "parent onStopNestedScroll target:${target::class.java.simpleName}\ttype:${type}"
        )
        parentHelper.onStopNestedScroll(target, type)
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int,
        consumed: IntArray
    ) {
        Log.i(
            TAG, "parent onNestedScroll1 target:${target::class.java.simpleName}" +
                    "\tdxConsumed:${dxConsumed}" +
                    "\tdyConsumed:${dyConsumed}" +
                    "\tdxUnconsumed:${dxUnconsumed}" +
                    "\tdyUnconsumed:${dyUnconsumed}" +
                    "\ttype:${type}" +
                    "consumed:${consumed}"
        )
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int
    ) {

        Log.i(
            TAG, "parent onNestedScroll2 target:${target::class.java.simpleName}" +
                    "\tdxConsumed:${dxConsumed}" +
                    "\tdyConsumed:${dyConsumed}" +
                    "\tdxUnconsumed:${dxUnconsumed}" +
                    "\tdyUnconsumed:${dyUnconsumed}" +
                    "\ttype:${type}"
        )
    }


    /*
     * 作者:android 超级兵
     * 创建时间: 4/7/22 4:45 PM
     * TODO 当子view调用 dispatchNestedPreScroll() 时候调用
     *   tips:在childNestedScrollView.onTouchEvent#ACTION_MOVE:中
     */
    override fun onNestedPreScroll(target: View, dx: Int, dy: Int,
                                   consumed: IntArray, type: Int) {
        Log.e(
            "parentView是否消费2", "target:${target::class.java.simpleName}" +
                    "\tdx:${dx}" +
                    "\tdy:${dy}" +
                    "\tconsumedX:${consumed[0]}" +
                    "\tconsumedY:${consumed[1]}" +
                    "\ttype:${type}" +
                    "\tfirstViewHeight:${firstView.height}" +
                    "\tscrollY:${scrollY}"
        )
        /**
         * dy > 0 表示向⬆️滑动
         * dy < 0 表示向⬇️滑动
         * scrollY > 0 向上滑动 表示还有滑动空间
         * scrollY < 0 向下滑动
         */

        // (dy > 0 &&  scrollY < firstView.height) 如果 向上滑动 并且 当前滑动的距离 < 第一个View的高 说明还有滑动空间
        // (dy < 0 && scrollY > 0) 如果当前向下滑动 并且还有滑动空间
        if ((dy > 0 && scrollY < firstView.height) || (dy < 0 && scrollY > 0)) {
            // 父容器消费了多少通知子view
            consumed[1] = dy // 关键代码!!parentView正在消费事件,并且通知 childView
            scrollBy(0, dy)
        }
    }



    override fun scrollTo(x: Int, y: Int) {
        var tempY = y

        if (tempY < 0) tempY = 0

        Log.i(TAG, "parent tempY:${tempY}")
        super.scrollTo(x, tempY)
    }
}