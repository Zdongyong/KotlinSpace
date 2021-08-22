package com.zdy.view

import android.R
import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.RelativeLayout
import androidx.core.view.ViewCompat

/**
 * 创建日期：8/18/21 on 10:21 PM
 * 描述：
 * 作者：zhudongyong
 */
class ZhuSwipeRefreshLayout: ViewGroup {

    companion object {
        val TAG = "ZhuSwipeRefreshLayout"
        private val LAYOUT_ATTRS = intArrayOf(R.attr.enabled)
        private const val HEADER_VIEW_HEIGHT = 50 // HeaderView height (dp)
        private const val SCALE_DOWN_DURATION = 150
        private const val ANIMATE_TO_TRIGGER_DURATION = 200
        private const val ANIMATE_TO_START_DURATION = 200
        private const val DEFAULT_CIRCLE_TARGET = 64

    }

    private var mTarget: View? = null
    private var mHeaderViewWidth = 0 // headerView的宽度
    private var mHeaderViewHeight = 0
    private var mHeadViewContainer = HeadViewContainer(context)
    private var mFooterViewContainer = RelativeLayout(context)
    private var mCurrentTargetOffsetTop = 0
    private val pushDistance = 0
    private var targetScrollWithLayout = true
    private val mUsingCustomStart = false
    private var mOriginalOffsetCalculated = false
    protected var mOriginalOffsetTop = 0
    private var mHeaderViewIndex = -1
    private var mFooterViewIndex = -1

    // 最后停顿时的偏移量px，与DEFAULT_CIRCLE_TARGET正比
    private var mSpinnerFinalOffset = 1.0f
    private var mTotalDragDistance = -1f
    private var density = 1.0f

    @JvmOverloads
    constructor(context: Context):super(context,null)

    constructor(context: Context,attributeSet: AttributeSet) : super(context,attributeSet) {
        val a: TypedArray = context.obtainStyledAttributes(
            attributeSet,
            ZhuSwipeRefreshLayout.LAYOUT_ATTRS
        )
        isEnabled = a.getBoolean(0, true)
        a.recycle()

        val layoutParams = RelativeLayout.LayoutParams(
            (mHeaderViewHeight * 0.8).toInt(),
            (mHeaderViewHeight * 0.8).toInt()
        )

        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL)
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
        mHeadViewContainer.visibility = GONE
        addView(mHeadViewContainer)
        addView(mFooterViewContainer)

        val wm = context
            .getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val metrics = resources.displayMetrics

        mHeaderViewWidth = display.width
        mHeaderViewHeight = (ZhuSwipeRefreshLayout.HEADER_VIEW_HEIGHT * metrics.density).toInt()
        ViewCompat.setChildrenDrawingOrderEnabled(this, true)
        mSpinnerFinalOffset =
            (ZhuSwipeRefreshLayout.DEFAULT_CIRCLE_TARGET * metrics.density).toFloat()
        density = metrics.density
        mTotalDragDistance = mSpinnerFinalOffset
    }


    fun setHeaderView(child: Int?) {
        if (child == null) {
            return
        }
        mHeadViewContainer.removeAllViews()
        val view = LayoutInflater.from(context).inflate(child, null)
        val layoutParams = RelativeLayout.LayoutParams(
            mHeaderViewWidth, mHeaderViewHeight
        )
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
        mHeadViewContainer.addView(view, layoutParams)
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (mTarget == null) {
            ensureTarget()
        }
        if (mTarget == null) {
            return
        }
        mTarget!!.measure(
            MeasureSpec.makeMeasureSpec(
                measuredWidth
                        - paddingLeft - paddingRight, MeasureSpec.EXACTLY
            ),
            MeasureSpec.makeMeasureSpec(
                (measuredHeight
                        - paddingTop - paddingBottom),
                MeasureSpec.EXACTLY
            )
        )
        mHeadViewContainer.measure(
            MeasureSpec.makeMeasureSpec(
                mHeaderViewWidth, MeasureSpec.EXACTLY
            ), MeasureSpec
                .makeMeasureSpec(3 * mHeaderViewHeight, MeasureSpec.EXACTLY)
        )
        if (!mUsingCustomStart && !mOriginalOffsetCalculated) {
            mOriginalOffsetCalculated = true
            mOriginalOffsetTop = -mHeadViewContainer
                .measuredHeight
            mCurrentTargetOffsetTop = mOriginalOffsetTop
//            updateListenerCallBack()
        }
        mHeaderViewIndex = -1
        for (index in 0 until childCount) {
            if (getChildAt(index) === mHeadViewContainer) {
                mHeaderViewIndex = index
                break
            }
        }

        mFooterViewIndex = -1
        for (index in 0 until childCount) {
            if (getChildAt(index) === mFooterViewContainer) {
                mFooterViewIndex = index
                break
            }
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val width = measuredWidth
        val height = measuredHeight
        if (childCount == 0) {
            return
        }
        if (mTarget == null) {
            ensureTarget()
        }
        if (mTarget == null) {
            return
        }
        var distance: Int = mCurrentTargetOffsetTop + mHeadViewContainer.measuredHeight
        if (!targetScrollWithLayout) {
            // 判断标志位，如果目标View不跟随手指的滑动而滑动，将下拉偏移量设置为0
            distance = 0
        }
        val child = mTarget
        val childLeft = paddingLeft
        val childTop: Int = paddingTop + distance - pushDistance // 根据偏移量distance更新

        val childWidth = width - paddingLeft - paddingRight
        val childHeight = height - paddingTop - paddingBottom
        child?.layout(
            childLeft, childTop, childLeft + childWidth, childTop
                    + childHeight
        ) // 更新目标View的位置

        val headViewWidth = mHeadViewContainer.measuredWidth
        val headViewHeight = mHeadViewContainer.measuredHeight
        mHeadViewContainer.layout(
            width / 2 - headViewWidth / 2,
            mCurrentTargetOffsetTop, width / 2 + headViewWidth / 2,
            mCurrentTargetOffsetTop + headViewHeight
        ) // 更新头布局的位置
        val footViewWidth = mFooterViewContainer.measuredWidth
        val footViewHeight = mFooterViewContainer.measuredHeight
        mFooterViewContainer.layout(
            width / 2 - footViewWidth / 2, height
                    - pushDistance, width / 2 + footViewWidth / 2, height
                    + footViewHeight - pushDistance
        )
    }

    /**
     * 孩子节点绘制的顺序
     *
     * @param childCount
     * @param i
     * @return
     */
    override fun getChildDrawingOrder(childCount: Int, i: Int): Int {
        // 将新添加的View,放到最后绘制
        if (mHeaderViewIndex < 0 && mFooterViewIndex < 0) {
            return i
        }
        if (i == childCount - 2) {
            return mHeaderViewIndex
        }
        if (i == childCount - 1) {
            return mFooterViewIndex
        }
        val bigIndex =
            if (mFooterViewIndex > mHeaderViewIndex) mFooterViewIndex else mHeaderViewIndex
        val smallIndex =
            if (mFooterViewIndex < mHeaderViewIndex) mFooterViewIndex else mHeaderViewIndex
        if (i >= smallIndex && i < bigIndex - 1) {
            return i + 1
        }
        return if (i >= bigIndex || i == bigIndex - 1) {
            i + 2
        } else i
    }


    /**
     * 确保mTarget不为空<br></br>
     * mTarget一般是可滑动的ScrollView,ListView,RecyclerView等
     */
    private fun ensureTarget() {
        if (mTarget == null) {
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                if (child != mHeadViewContainer) {
                    mTarget = child
                    break
                }
            }
        }
    }

    inner class HeadViewContainer(context: Context) : RelativeLayout(context) {

        init {

        }
    }
}