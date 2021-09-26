package com.zdy.view.swipeRefresh

import android.R
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.TypedArray
import android.os.Build
import android.os.Handler
import android.util.AttributeSet
import android.view.*
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.Transformation
import android.widget.RelativeLayout
import androidx.core.view.MotionEventCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

/**
 * 创建日期：5/21/20 on 11:33 PM
 * 描述：
 * 作者：zhudongyong
 */
class ZZSwipeRefreshView : ViewGroup {

    private val HEADER_VIEW_HEIGHT: Int = 50 // HeaderView height (dp)
    private val DECELERATE_INTERPOLATION_FACTOR = 2f //动画插值器
    private val INVALID_POINTER = -1 //初始化手指point
    private val DRAG_RATE = .5f

    private val DEFAULT_CIRCLE_TARGET = 64
    private val SCALE_DOWN_DURATION = 150
    private val ANIMATE_TO_START_DURATION = 200

    //动画插值器 开始快然后慢
    private var mDecelerateInterpolator: DecelerateInterpolator = DecelerateInterpolator(
        DECELERATE_INTERPOLATION_FACTOR
    )

    private var mTargetView: View? = null //当前ZZSwipeRefreshView内部view

    private var mRefreshing = false
    private var mLoadMore = false
    private var mTouchSlop = ViewConfiguration.get(context).scaledTouchSlop //最小触发滑动的距离
    private var mTotalDragDistance = -1f //拉取最大位置
    private var mCurrentTargetOffsetTop = 0 //子view偏移量
    private var mOriginalOffsetCalculated = false

    private var mInitialMotionY = 0f
    private var mInitialMotionX = 0f
    private var mIsBeingDragged = false
    private var mActivePointerId = INVALID_POINTER

    private var mReturningToStart = false
    private val LAYOUT_ATTRS = intArrayOf(R.attr.enabled)

    private var mHeaderViewContainer = RelativeLayout(context)
    private var mFooterViewContainer = RelativeLayout(context)
    private var mHeaderViewIndex = -1
    private var mFooterViewIndex = -1
    private val mScale = false

    private var mFrom = 0
    private var mOriginalOffsetTop = 0

    private var mScaleDownAnimation: Animation? = null

    private var mScaleDownToStartAnimation: Animation? = null

    // 最后停顿时的偏移量px，与DEFAULT_CIRCLE_TARGET正比
    private var mSpinnerFinalOffset = 0f

    private var mHeaderViewWidth = 0 // headerView的宽度
    private var mFooterViewWidth = 0
    private var mHeaderViewHeight = 0
    private var mFooterViewHeight = 0
    private val mUsingCustomStart = false
    private var targetScrollWithLayout: Boolean = true
    private var pushDistance = 0
    private var density = 1.0f

    private var pullRefreshListener: OnPullRefreshListener? = null // 下拉刷新
    private var pushLoadMoreListener: OnPushLoadMoreListener? = null // 上拉加载更多

    @JvmOverloads
    constructor(context: Context) : super(context, null)

    @JvmOverloads
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        setWillNotDraw(false) //对view进行重新绘制
        val a: TypedArray = context
            .obtainStyledAttributes(attributeSet, LAYOUT_ATTRS)
        isEnabled = a.getBoolean(0, true)
        a.recycle()

        val wm = context
            .getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val metrics = resources.displayMetrics
        mHeaderViewWidth = display.width
        mFooterViewWidth = display.width
        mHeaderViewHeight = (HEADER_VIEW_HEIGHT * metrics.density).toInt()
        mFooterViewHeight = (HEADER_VIEW_HEIGHT * metrics.density).toInt()
        createHeaderViewContainer()
        createFooterViewContainer()
        ViewCompat.setChildrenDrawingOrderEnabled(this, true)
        mSpinnerFinalOffset = DEFAULT_CIRCLE_TARGET * metrics.density
        density = metrics.density
        mTotalDragDistance = mSpinnerFinalOffset
    }

    /**
     * 创建头布局的容器
     */
    private fun createHeaderViewContainer() {
        val layoutParams = RelativeLayout.LayoutParams(
            (mHeaderViewHeight * 0.8).toInt(),
            (mHeaderViewHeight * 0.8).toInt()
        )
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL)
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
        mHeaderViewContainer.visibility = GONE
        addView(mHeaderViewContainer)
    }

    /**
     * 添加底部布局
     */
    private fun createFooterViewContainer() {
        mFooterViewContainer.visibility = GONE
        addView(mFooterViewContainer)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (mTargetView == null) {
            ensureTarget()
        }
        mTargetView?.let {
            it.measure(
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
            mHeaderViewContainer.measure(
                MeasureSpec.makeMeasureSpec(
                    mHeaderViewWidth, MeasureSpec.EXACTLY
                ), MeasureSpec
                    .makeMeasureSpec(3 * mHeaderViewHeight, MeasureSpec.EXACTLY)
            )
            mFooterViewContainer.measure(
                MeasureSpec.makeMeasureSpec(
                    mFooterViewWidth, MeasureSpec.EXACTLY
                ), MeasureSpec
                    .makeMeasureSpec(mFooterViewHeight, MeasureSpec.EXACTLY)
            )
            if (!mUsingCustomStart && !mOriginalOffsetCalculated) {
                mOriginalOffsetCalculated = true
                mOriginalOffsetTop = -(mHeaderViewContainer.measuredHeight)
                mCurrentTargetOffsetTop = mOriginalOffsetTop
                updateListenerCallBack()
            }
            mHeaderViewIndex = -1
            for (index in 0 until childCount) {
                if (getChildAt(index) === mHeaderViewContainer) {
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
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val width = measuredWidth
        val height = measuredHeight

        if (mTargetView == null) {
            ensureTarget()
        }
        mTargetView?.let {
            var distance = mCurrentTargetOffsetTop + mHeaderViewContainer.measuredHeight
            if (!targetScrollWithLayout) {
                // 判断标志位，如果目标View不跟随手指的滑动而滑动，将下拉偏移量设置为0
                distance = 0
            }
            val childLeft = paddingLeft
            val childTop = paddingTop + distance - pushDistance // 根据偏移量distance更新

            val childWidth = width - paddingLeft - paddingRight
            val childHeight = height - paddingTop - paddingBottom
            it.layout(
                childLeft, childTop, childLeft + childWidth, childTop
                        + childHeight
            ) // 更新目标View的位置

            val headViewWidth = mHeaderViewContainer.measuredWidth
            val headViewHeight = mHeaderViewContainer.measuredHeight
            mHeaderViewContainer.layout(
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
    }

    /**
     * 确保mTarget不为空
     * mTarget一般是可滑动的ScrollView,ListView,RecyclerView等
     */
    private fun ensureTarget() {
        if (mTargetView == null) {
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                if (child != mHeaderViewContainer
                    && child != mFooterViewContainer
                ) {
                    mTargetView = child
                    break
                }
            }
        }
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
     * ====================================以上是布局相关=========================================================
     */

    /**
     * ====================================以下是滑动相关=========================================================
     */


    /**
     * 主要判断是否应该拦截子View的事件
     * 如果拦截，则交给自己的OnTouchEvent处理
     * 否者，交给子View处理
     */
    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        ensureTarget()
        val action = MotionEventCompat.getActionMasked(ev)
        if (mReturningToStart && action == MotionEvent.ACTION_DOWN) {
            mReturningToStart = false
        }
        if (!isEnabled || mReturningToStart || mRefreshing || mLoadMore
            || !isChildScrollToTop() && !isChildScrollToBottom()
        ) {
            // 如果子View可以滑动，不拦截事件，交给子View处理-下拉刷新
            // 或者子View没有滑动到底部不拦截事件-上拉加载更多
            return false
        }
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                // 恢复HeaderView的初始位置
                setTargetOffsetTopAndBottom(mOriginalOffsetTop - mHeaderViewContainer.top, true)
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0)
                mIsBeingDragged = false
                val initialMotionY = getMotionEventY(ev, mActivePointerId)
                if (initialMotionY == -1f) {
                    return false
                }
                mInitialMotionX = getMotionEventX(ev, mActivePointerId)
                mInitialMotionY = initialMotionY // 记录按下的位置
            }
            MotionEvent.ACTION_MOVE -> {
                if (mActivePointerId == INVALID_POINTER) {
                    return false
                }

                val y = getMotionEventY(ev, mActivePointerId)
                if (y == -1f) {
                    return false
                }
                var yDiff = 0f
                if (isChildScrollToBottom()) {
                    yDiff = mInitialMotionY - y //计算上拉的距离
                    if (yDiff > mTouchSlop && !mIsBeingDragged) { //判断是否触发最小滑动距离
                        mIsBeingDragged = true
                    }
                } else {
                    yDiff = y - mInitialMotionY //计算下拉的距离
                    if (yDiff > mTouchSlop && !mIsBeingDragged) {
                        mIsBeingDragged = true
                    }
                }

                //todo 处理横向滑动冲突
                val xDiff: Float = abs(getMotionEventX(ev, mActivePointerId) - mInitialMotionX)
                if (xDiff > yDiff) {
                    parent.requestDisallowInterceptTouchEvent(false)
                    return false
                } else {
                    parent.requestDisallowInterceptTouchEvent(true)
                }
            }
            MotionEventCompat.ACTION_POINTER_UP -> onSecondaryPointerUp(ev)
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                mIsBeingDragged = false
                mActivePointerId = INVALID_POINTER
            }
        }
        return mIsBeingDragged // 如果正在拖动，则拦截子View的事件
    }

    private fun getMotionEventX(ev: MotionEvent, activePointerId: Int): Float {
        val index = MotionEventCompat.findPointerIndex(
            ev,
            activePointerId
        )
        return if (index < 0) {
            (-1).toFloat()
        } else MotionEventCompat.getX(ev, index)
    }

    private fun getMotionEventY(ev: MotionEvent, activePointerId: Int): Float {
        val index = MotionEventCompat.findPointerIndex(
            ev,
            activePointerId
        )
        return if (index < 0) {
            (-1).toFloat()
        } else MotionEventCompat.getY(ev, index)
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        val action = MotionEventCompat.getActionMasked(ev)
        if (mReturningToStart && action == MotionEvent.ACTION_DOWN) {
            mReturningToStart = false
        }
        if (!isEnabled || mReturningToStart
            || !isChildScrollToTop() && !isChildScrollToBottom()
        ) {
            // 如果子View可以滑动，不拦截事件，交给子View处理
            return false
        }
        return if (isChildScrollToBottom()) { // 加载更多
            handlerPushTouchEvent(ev, action)
        } else { // 下拉刷新
            handlerPullTouchEvent(ev, action)
        }
    }

    /**
     * 处理加载更多的Touch事件
     *
     */
    private fun handlerPushTouchEvent(ev: MotionEvent, action: Int): Boolean {
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0)
                mIsBeingDragged = false
            }
            MotionEvent.ACTION_MOVE -> {
                val pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId)
                if (pointerIndex < 0) {
                    return false
                }
                val y = MotionEventCompat.getY(ev, pointerIndex)
                val overscrollBottom = (mInitialMotionY - y) * DRAG_RATE
                if (mIsBeingDragged) {
                    pushDistance = overscrollBottom.toInt()
                    updateFooterViewPosition()
                    pushLoadMoreListener?.let {
                        it.onPushEnable(pushDistance >= mFooterViewHeight)
                    }
                }
            }
            MotionEventCompat.ACTION_POINTER_DOWN -> {
                val index = MotionEventCompat.getActionIndex(ev)
                mActivePointerId = MotionEventCompat.getPointerId(ev, index)
            }
            MotionEventCompat.ACTION_POINTER_UP -> onSecondaryPointerUp(ev)
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (mActivePointerId == INVALID_POINTER) {
                    return false
                }
                val pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId)
                val y = MotionEventCompat.getY(ev, pointerIndex)
                val overscrollBottom = (mInitialMotionY - y) * DRAG_RATE // 松手是下拉的距离
                mIsBeingDragged = false
                mActivePointerId = INVALID_POINTER
                pushDistance = if (overscrollBottom < mFooterViewHeight) { // 直接取消
                    0
                } else { // 下拉到mFooterViewHeight
                    mFooterViewHeight
                }
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                    updateFooterViewPosition()
                    if (pushDistance == mFooterViewHeight) {
                        pushLoadMoreListener?.let {
                            it.onLoadMore()
                        }
                    }
                } else {
                    animatorFooterToBottom(overscrollBottom.toInt(), pushDistance)
                }
                return false
            }
        }
        return true
    }

    /**
     * 松手后，footer移动到最下面 其实也是回复footer的位置（最低层）
     */
    private fun animatorFooterToBottom(start: Int, end: Int) {
        val valueAnimator = ValueAnimator.ofInt(start, end)
        valueAnimator.duration = 150
        valueAnimator.addUpdateListener { valueAnimator -> // update
            pushDistance = valueAnimator.animatedValue as Int
            updateFooterViewPosition()
        }
        valueAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                if (end > 0 && pushLoadMoreListener != null) {
                    mLoadMore = true
                    pushLoadMoreListener?.onLoadMore()
                } else {
                    resetTargetLayout()
                    mLoadMore = false
                }
            }
        })
        valueAnimator.interpolator = mDecelerateInterpolator
        valueAnimator.start()
    }


    /**
     * 处理下拉刷新
     */
    private fun handlerPullTouchEvent(ev: MotionEvent, action: Int): Boolean {
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0)
                mIsBeingDragged = false
            }
            MotionEvent.ACTION_MOVE -> {
                val pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId)
                if (pointerIndex < 0) {
                    return false
                }
                val y = MotionEventCompat.getY(ev, pointerIndex)
                val overscrollTop = (y - mInitialMotionY) * DRAG_RATE
                if (mIsBeingDragged) {
                    val originalDragPercent = overscrollTop / mTotalDragDistance
                    if (originalDragPercent < 0) {
                        return false
                    }
                    val dragPercent = min(1f, abs(originalDragPercent))
                    val extraOS = abs(overscrollTop) - mTotalDragDistance
                    val slingshotDist = if (mUsingCustomStart) {
                        mSpinnerFinalOffset - mOriginalOffsetTop
                    } else {
                        mSpinnerFinalOffset
                    }
                    val tensionSlingshotPercent = max(
                        0f,
                        min(extraOS, slingshotDist * 2) / slingshotDist
                    )
                    val tensionPercent =
                        (tensionSlingshotPercent / 4 - (tensionSlingshotPercent / 4).toDouble()
                            .pow(2.0)).toFloat() * 2f
                    val extraMove = slingshotDist * tensionPercent * 2
                    val targetY = (mOriginalOffsetTop
                            + (slingshotDist * dragPercent + extraMove).toInt())
                    if (mHeaderViewContainer.visibility != VISIBLE) {
                        mHeaderViewContainer.visibility = VISIBLE
                    }
                    if (!mScale) {
                        ViewCompat.setScaleX(mHeaderViewContainer, 1f)
                        ViewCompat.setScaleY(mHeaderViewContainer, 1f)
                    }
                    if (overscrollTop < mTotalDragDistance) {
                        if (mScale) {
                            setAnimationProgress(overscrollTop / mTotalDragDistance)
                        }
                        pullRefreshListener?.onPullEnable(false)
                    } else {
                        pullRefreshListener?.onPullEnable(true)
                    }
                    setTargetOffsetTopAndBottom(
                        targetY - mCurrentTargetOffsetTop,
                        true
                    )
                }
            }
            MotionEventCompat.ACTION_POINTER_DOWN -> {
                val index = MotionEventCompat.getActionIndex(ev)
                mActivePointerId = MotionEventCompat.getPointerId(ev, index)
            }
            MotionEventCompat.ACTION_POINTER_UP -> onSecondaryPointerUp(ev)
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (mActivePointerId == INVALID_POINTER) {
                    if (action == MotionEvent.ACTION_UP) {
                    }
                    return false
                }
                val pointerIndex = MotionEventCompat.findPointerIndex(
                    ev,
                    mActivePointerId
                )
                val y = MotionEventCompat.getY(ev, pointerIndex)
                val overscrollTop = (y - mInitialMotionY) * DRAG_RATE
                mIsBeingDragged = false
                if (overscrollTop > mTotalDragDistance) { //大于最大位置，回复header
                    setRefreshing(refreshing = true)
                } else {
                    mRefreshing = false
                    var listener: Animation.AnimationListener? = null
                    if (!mScale) {
                        listener = object : Animation.AnimationListener {
                            override fun onAnimationStart(animation: Animation) {
                            }

                            override fun onAnimationEnd(animation: Animation) {
                                if (!mScale) {
                                    startScaleDownAnimation(null)
                                }
                            }

                            override fun onAnimationRepeat(animation: Animation) {}
                        }
                    }
                    animateOffsetToStartPosition(mCurrentTargetOffsetTop, listener)
                }
                mActivePointerId = INVALID_POINTER
                return false
            }
        }
        return true
    }

    /**
     * 判断目标View是否滑动到顶部-还能否继续滑动
     *
     * @return
     */
    private fun isChildScrollToTop(): Boolean {
        return if (Build.VERSION.SDK_INT < 14) {
            mTargetView!!.scrollY <= 0
        } else {
            !ViewCompat.canScrollVertically(mTargetView, -1)
        }
    }

    /**
     * 是否滑动到底部
     *
     * @return
     */
    private fun isChildScrollToBottom(): Boolean {
        if (isChildScrollToTop()) {
            return false
        }
        if (mTargetView is RecyclerView) {
            val recyclerView = mTargetView as RecyclerView
            val layoutManager = recyclerView.layoutManager
            val count = recyclerView.adapter!!.itemCount
            if (layoutManager is LinearLayoutManager && count > 0) {
                if (layoutManager.findLastCompletelyVisibleItemPosition() == count - 1) {
                    return true
                }
            } else if (layoutManager is StaggeredGridLayoutManager) {
                val lastItems = IntArray(2)
                layoutManager
                    .findLastCompletelyVisibleItemPositions(lastItems)
                val lastItem = max(lastItems[0], lastItems[1])
                if (lastItem == count - 1) {
                    return true
                }
            }
            return false
        }
        return false
    }

    private fun startScaleDownAnimation(listener: Animation.AnimationListener?) {
        mScaleDownAnimation = object : Animation() {
            override fun applyTransformation(
                interpolatedTime: Float,
                t: Transformation
            ) {
                setAnimationProgress(1 - interpolatedTime)
            }
        }
        mScaleDownAnimation?.duration = SCALE_DOWN_DURATION.toLong()
        mScaleDownAnimation?.setAnimationListener(listener)
        mHeaderViewContainer.clearAnimation()
        mHeaderViewContainer.startAnimation(mScaleDownAnimation)
    }

    private fun setAnimationProgress(progress: Float) {
        var progress = 1f
        ViewCompat.setScaleX(mHeaderViewContainer, progress)
        ViewCompat.setScaleY(mHeaderViewContainer, progress)
    }

    /**
     * header移动位置
     */
    private fun animateOffsetToStartPosition(
        from: Int,
        listener: Animation.AnimationListener?
    ) {
        if (mScale) {
            startScaleDownReturnToStartAnimation(from, listener)
        } else {
            mFrom = from
            mAnimateToStartPosition.reset()
            mAnimateToStartPosition.duration = ANIMATE_TO_START_DURATION.toLong()
            mAnimateToStartPosition.interpolator = mDecelerateInterpolator
            if (listener != null) {
                mAnimateToStartPosition.setAnimationListener(listener)
            }
            mHeaderViewContainer.clearAnimation()
            mHeaderViewContainer.startAnimation(mAnimateToStartPosition)
        }
        resetTargetLayoutDelay(ANIMATE_TO_START_DURATION)
    }

    private fun startScaleDownReturnToStartAnimation(
        from: Int,
        listener: Animation.AnimationListener?
    ) {
        mFrom = from
        mScaleDownToStartAnimation = object : Animation() {
            override fun applyTransformation(
                interpolatedTime: Float,
                t: Transformation
            ) {
                val targetScale: Float =
                    ViewCompat.getScaleX(mHeaderViewContainer) + (-ViewCompat.getScaleX(
                        mHeaderViewContainer
                    ) * interpolatedTime)
                setAnimationProgress(targetScale)
                moveToStart(interpolatedTime)
            }
        }
        mScaleDownToStartAnimation?.duration = SCALE_DOWN_DURATION.toLong()
        if (listener != null) {
            mScaleDownToStartAnimation?.setAnimationListener(listener)
        }
        mHeaderViewContainer.clearAnimation()
        mHeaderViewContainer.startAnimation(mScaleDownToStartAnimation)
    }

    /**
     * 重置Target位置
     *
     * @param delay
     */
    private fun resetTargetLayoutDelay(delay: Int) {
        Handler().postDelayed({ resetTargetLayout() }, delay.toLong())
    }

    fun setRefreshing(refreshing: Boolean) {
        if (mRefreshing != refreshing) {
            ensureTarget()
            mRefreshing = refreshing
            if (mRefreshing) {//拉的距离太大，先回到正确位置，再停顿
                animateOffsetToCorrectPosition(
                    mCurrentTargetOffsetTop,
                    mRefreshListener
                )
            } else {
                animateOffsetToStartPosition(mCurrentTargetOffsetTop, mRefreshListener)
            }
        }
    }

    private fun animateOffsetToCorrectPosition(
        from: Int,
        listener: Animation.AnimationListener?
    ) {
        mFrom = from
        mHeaderViewContainer.clearAnimation()
        mAnimateToCorrectPosition.reset()
        mAnimateToCorrectPosition.duration = 1000
        mAnimateToCorrectPosition.interpolator = mDecelerateInterpolator
        if (listener != null) {
            mAnimateToCorrectPosition.setAnimationListener(listener)
        }
        mHeaderViewContainer.startAnimation(mAnimateToCorrectPosition)
    }

    private val mAnimateToCorrectPosition: Animation = object : Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            var targetTop = 0
            var endTarget = 0
            endTarget = if (!mUsingCustomStart) {
                (mSpinnerFinalOffset - abs(mOriginalOffsetTop)).toInt()
            } else {
                mSpinnerFinalOffset.toInt()
            }
            targetTop = mFrom + ((endTarget - mFrom) * interpolatedTime).toInt()
            val offset = targetTop - mHeaderViewContainer.top
            setTargetOffsetTopAndBottom(offset, false /* requires update */)
        }

        override fun setAnimationListener(listener: AnimationListener) {
            super.setAnimationListener(listener)
        }
    }

    /**
     * 修改底部布局的位置-敏感pushDistance
     */
    private fun updateFooterViewPosition() {
        mFooterViewContainer.visibility = VISIBLE
        mFooterViewContainer.bringToFront()
        //针对4.4及之前版本的兼容
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            mFooterViewContainer.parent.requestLayout()
        }
        mFooterViewContainer.offsetTopAndBottom(-pushDistance)
        updatePushDistanceListener()
    }

    private fun updatePushDistanceListener() {
//        pushLoadMoreListener?.onPushDistance(pushDistance)
    }

    private fun onSecondaryPointerUp(ev: MotionEvent) {
        val pointerIndex = MotionEventCompat.getActionIndex(ev)
        val pointerId = MotionEventCompat.getPointerId(ev, pointerIndex)
        if (pointerId == mActivePointerId) {
            val newPointerIndex = if (pointerIndex == 0) 1 else 0
            mActivePointerId = MotionEventCompat.getPointerId(
                ev,
                newPointerIndex
            )
        }
    }

    private fun setTargetOffsetTopAndBottom(offset: Int, requiresUpdate: Boolean) {
        mHeaderViewContainer.bringToFront()
        mHeaderViewContainer.offsetTopAndBottom(offset)
        mCurrentTargetOffsetTop = mHeaderViewContainer.top
        if (requiresUpdate && Build.VERSION.SDK_INT < 11) {
            invalidate()
        }
        updateListenerCallBack()
    }


    /**
     * 自定义头部布局
     *
     * @param child
     */
    fun setHeaderView(child: View?) {
        if (child == null) {
            return
        }
        mHeaderViewContainer.removeAllViews()
        val layoutParams = RelativeLayout.LayoutParams(
            mHeaderViewWidth, mHeaderViewHeight
        )
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
        mHeaderViewContainer.addView(child, layoutParams)
    }
    
    
    /**
     * 自定义底部布局
     *
     * @param child
     */
    fun setFooterView(child: View?) {
        if (child == null) {
            return
        }
        mFooterViewContainer.removeAllViews()
        val layoutParams = RelativeLayout.LayoutParams(
            mFooterViewWidth, mFooterViewHeight
        )
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
        mFooterViewContainer.addView(child, layoutParams)
    }


    /**
     * 更新回调
     */
    private fun updateListenerCallBack() {
        val distance = mCurrentTargetOffsetTop + mHeaderViewContainer.height
//        pullRefreshListener?.onPullDistance(distance)
    }


    /**
     * 下拉时，超过距离之后，弹回来的动画监听器
     */
    private val mRefreshListener: Animation.AnimationListener =
        object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}

            override fun onAnimationRepeat(animation: Animation) {}

            override fun onAnimationEnd(animation: Animation) {
//                    Handler().postDelayed({
//                        setR
//                    }, 2000)
                if (mRefreshing) {
                    pullRefreshListener?.onRefresh()
                } else {
                    mHeaderViewContainer.visibility = GONE
                    mFooterViewContainer.visibility = GONE
                    setTargetOffsetTopAndBottom(
                        mOriginalOffsetTop
                                - mCurrentTargetOffsetTop, true
                    )
                }
                mCurrentTargetOffsetTop = mHeaderViewContainer.top
                updateListenerCallBack()
            }

        }

    /**
     * 重置Target的位置
     */
    private fun resetTargetLayout() {
        val width = measuredWidth
        val height = measuredHeight
        val child = mTargetView!!
        val childLeft = paddingLeft
        val childTop = paddingTop
        val childWidth = (child.width - paddingLeft
                - paddingRight)
        val childHeight = (child.height - paddingTop
                - paddingBottom)
        child.layout(
            childLeft, childTop, childLeft + childWidth, childTop
                    + childHeight
        )
        val headViewWidth = mHeaderViewContainer.measuredWidth
        val headViewHeight = mHeaderViewContainer.measuredHeight
        mHeaderViewContainer.layout(
            width / 2 - headViewWidth / 2,
            -headViewHeight, width / 2 + headViewWidth / 2, 0
        ) // 更新头布局的位置
        val footViewWidth = mFooterViewContainer.measuredWidth
        val footViewHeight = mFooterViewContainer.measuredHeight
        mFooterViewContainer.layout(
            width / 2 - footViewWidth / 2, height,
            width / 2 + footViewWidth / 2, height + footViewHeight
        )
    }

    private val mAnimateToStartPosition: Animation = object : Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            moveToStart(interpolatedTime)
        }
    }


    private fun moveToStart(interpolatedTime: Float) {
        var targetTop = 0
        targetTop = mFrom + ((mOriginalOffsetTop - mFrom) * interpolatedTime).toInt()
        val offset = targetTop - mHeaderViewContainer.top
        setTargetOffsetTopAndBottom(offset, false /* requires update */)
    }

    fun setHeaderViewBackgroundColor(color: Int) {
        mHeaderViewContainer.setBackgroundColor(color)
    }

    /**
     * 设置子View是否跟谁手指的滑动而滑动
     *
     * @param targetScrollWithLayout
     */
    fun setTargetScrollWithLayout(targetScrollWithLayout: Boolean) {
        this.targetScrollWithLayout = targetScrollWithLayout
    }

    fun setOnPullRefreshListener(listener: OnPullRefreshListener) {
        this.pullRefreshListener = listener
    }

    fun setOnPushLoadMoreListener(listener: OnPushLoadMoreListener) {
        this.pushLoadMoreListener = listener
    }
}