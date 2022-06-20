package com.example.meterialproject.view.nested

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.core.view.NestedScrollingChild3
import androidx.core.view.NestedScrollingChildHelper
import androidx.core.view.ViewCompat
import androidx.core.view.children


/**
 *
 * @ClassName: ChildNestedScrollView
 * @Author: android 超级兵
 * @CreateDate: 4/7/22$ 3:42 PM$
 * TODO
 */
class ChildNestedScrollView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr), NestedScrollingChild3 {


    private val TAG:String = "ChildNestedScrollView"


    private val childHelper by lazy {
        NestedScrollingChildHelper(this).apply { isNestedScrollingEnabled = true }
    }

    // 滚动消耗
    private val mScrollConsumed = IntArray(2)

    // 偏移量
    private val mScrollOffset = IntArray(2)

    private var lastTouchY = 0


    @SuppressLint("ClickableViewAccessibility", "Range")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val touchX = event.x.toInt()
        val touchY = event.y.toInt()

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                Log.i(TAG, "down touchX:$touchX \t touchY:$touchY")
                lastTouchY = touchY
                startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL, ViewCompat.TYPE_TOUCH)
            }
            MotionEvent.ACTION_MOVE -> {
                var tempY = lastTouchY - touchY
                // 分发事件给parent 询问parent是否执行
                // true 表示父view消费了事件
                if (dispatchNestedPreScroll(
                        0,
                        tempY,
                        mScrollConsumed,
                        mScrollOffset,
                        ViewCompat.TYPE_TOUCH
                    )
                ) { // 父亲消费
                    Log.i(
                        TAG,
                        "mScrollConsumed:${mScrollConsumed[0]}\t${mScrollConsumed[1]} mScrollOffsetX:${mScrollOffset[0]}\tmScrollOffsetY:${mScrollOffset[1]}"
                    )
                    tempY -= mScrollConsumed[1]
                    if (tempY == 0) return true
                } else {
                    Log.i(
                        TAG,
                        "move tempY:${tempY}\tscrollY:$scrollY"
                    )// 自己消费
                    scrollBy(0, tempY)
                }
                lastTouchY = touchY
                // true 支持嵌套滚动
               if( dispatchNestedScroll(0,
                    tempY,
                    0,
                    scrollY - measuredHeight,
                    mScrollOffset,
                    ViewCompat.TYPE_TOUCH)){
                   Log.i(TAG,"dispatchNestedScroll\t lastTouchY:${lastTouchY}")
               }

            }
            // 抬起/取消
            MotionEvent.ACTION_CANCEL,
            MotionEvent.ACTION_UP -> {
                stopNestedScroll(ViewCompat.TYPE_TOUCH)
                Log.i(TAG, "up touchX:$touchX \t touchY:$touchY")
            }
        }
        return true
    }

    override fun startNestedScroll(axes: Int, type: Int): Boolean = let {
        Log.i(TAG, "child startNestedScroll axes:$axes type:$type ")
        childHelper.startNestedScroll(axes)
    }

    override fun stopNestedScroll(type: Int) {
        Log.i(TAG, "child stopNestedScroll $type")
        childHelper.stopNestedScroll(type)
    }

    override fun hasNestedScrollingParent(type: Int): Boolean = let {
        Log.i(TAG, "child hasNestedScrollingParent type:$type")
        childHelper.hasNestedScrollingParent(type)
    }


    /*
     * 作者:android 超级兵
     * 创建时间: 4/7/22 4:19 PM
     * TODO
     *  dxConsumed: 当前view消费的水平方向距离
        dyConsumed: 当前view消费的垂直方向距离
        dxUnconsumed: 没有消费的水平方向距离
        dyUnconsumed: 没有消费的垂直方向距离
        offsetInWindow: 【0】水平方向 【1】垂直方向
        type: Int,
        consumed: IntArray
     */
    override fun dispatchNestedScroll(
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        offsetInWindow: IntArray?,
        type: Int,
        consumed: IntArray
    ) {
        Log.i(
            TAG,
            "child dispatchNestedScroll1 dxConsumed:$dxConsumed\tdyConsumed:$dyConsumed\tdxUnconsumed:$dxUnconsumed" +
                    "\tdyUnconsumed:$dyUnconsumed\toffsetInWindow:$offsetInWindow\ttype:$type"
        )
        childHelper.dispatchNestedScroll(
            dxConsumed,
            dyConsumed,
            dxUnconsumed,
            dyUnconsumed,
            offsetInWindow,
            type,
            consumed
        )
    }

    // NestedScrollingChild2
    override fun dispatchNestedScroll(
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        offsetInWindow: IntArray?,
        type: Int
    ): Boolean = let {
        Log.i(
            TAG,
            "child dispatchNestedScroll2 dxConsumed:$dxConsumed\tdyConsumed:$dyConsumed\tdxUnconsumed:$dxUnconsumed" +
                    "\tdyUnconsumed:$dyUnconsumed\toffsetInWindow:$offsetInWindow\ttype:$type"
        )
        childHelper.dispatchNestedScroll(
            dxConsumed,
            dyConsumed,
            dxUnconsumed,
            dyUnconsumed,
            offsetInWindow,
            type
        )
    }

    override fun dispatchNestedPreScroll(
        dx: Int,
        dy: Int,
        consumed: IntArray?,
        offsetInWindow: IntArray?,
        type: Int
    ): Boolean = let {
        Log.i(
            TAG,
            "child dispatchNestedPreScroll dx:$dx" +
                    "\tdy:$dy" +
                    "\tconsumed:${consumed?.get(0)}" +
                    "\tconsumed:${consumed?.get(1)}" +
                    "\toffsetInWindow:${offsetInWindow?.get(0)}" +
                    "\toffsetInWindow:${offsetInWindow?.get(1)}" +
                    "\ttype:$type"
        )
        childHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type)
    }

    /*
     * 作者:android 超级兵
     * 创建时间: 4/9/22 3:47 PM
     * TODO  最终xml会调用到这里..添加
     */
    override fun addView(child: View, params: ViewGroup.LayoutParams?) {
        Log.i("szjCurrentViewSize1",
            "childView:${child::class.java.simpleName}\t" +
                "childCount:$childCount")

        super.addView(child, params)

        Log.e(TAG,
            "childView:${child::class.java.simpleName}\t" +
                    "childCount:$childCount")
    }

    @SuppressLint("LongLogTag")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var tempHeightMeasureSpec = heightMeasureSpec

        val widthSize = MeasureSpec.getSize(widthMeasureSpec)

        // 遍历所有的view 用来测量高度
        children.forEach {
            tempHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
                MeasureSpec.getSize(tempHeightMeasureSpec),
                MeasureSpec.UNSPECIFIED
            )

            // 测量子view
            measureChild(it, widthMeasureSpec, tempHeightMeasureSpec)
        }
        setMeasuredDimension(widthSize, children.first().measuredHeight)
    }


    /*
     * 作者:android 超级兵
     * 创建时间: 4/7/22 5:38 PM
     * TODO 未了防止滑过头
     */
    override fun scrollTo(x: Int, y: Int) {
        var tempY = y

        if (tempY < 0) tempY = 0

        Log.i(TAG, "scrollY:$scrollY")

        super.scrollTo(x, tempY)
    }
}