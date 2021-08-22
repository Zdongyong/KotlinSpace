package com.zdy.view

import android.R
import android.content.Context
import android.content.res.TypedArray
import android.os.Build
import android.os.Handler
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.RotateAnimation
import android.view.animation.Transformation
import android.widget.RelativeLayout
import androidx.core.view.MotionEventCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlinx.android.synthetic.main.item_header.view.*
import kotlin.math.max

/**
 * 创建日期：8/21/21 on 11:33 PM
 * 描述：
 * 作者：zhudongyong
 */
class ZZSwipeRefreshView : ViewGroup {

    private val LOG_TAG = "CustomeSwipeRefreshLayout"
    private val HEADER_VIEW_HEIGHT = 50 // HeaderView height (dp)

    private val DECELERATE_INTERPOLATION_FACTOR = 2f
    private val INVALID_POINTER = -1
    private val DRAG_RATE = .5f

    private val DEFAULT_CIRCLE_TARGET = 64
    private val SCALE_DOWN_DURATION = 150
    private val ANIMATE_TO_START_DURATION = 200
    private val mMediumAnimationDuration = resources.getInteger(
        R.integer.config_mediumAnimTime
    )

    private var mTarget: View? = null
    private var mTargetChild: View? = null

    private val mListener: OnPullRefreshListener? = null // 下拉刷新listener

    private var mRefreshing = false
    private var mLoadMore = false
    private var mTouchSlop = 0
    private var mTotalDragDistance = -1f
    private var mCurrentTargetOffsetTop = 0
    private var mOriginalOffsetCalculated = false

    private var mInitialMotionY = 0f
    private var mIsBeingDragged = false
    private var mActivePointerId = INVALID_POINTER

    private var mReturningToStart = false
    private var mDecelerateInterpolator: DecelerateInterpolator? = null //开始快然后慢
    private val LAYOUT_ATTRS = intArrayOf(R.attr.enabled)

    private var mHeadViewContainer: HeadViewContainer? = null
    private var mFooterViewContainer = RelativeLayout(context)
    private var mHeaderViewIndex = -1
    private var mFooterViewIndex = -1
    private val mScale = false

    protected var mFrom = 0

    protected var mOriginalOffsetTop = 0

    private var mScaleAnimation: Animation? = null

    private var mScaleDownAnimation: Animation? = null

    private var mScaleDownToStartAnimation: Animation? = null

    private var rotateAnimation: RotateAnimation? = null

    // 最后停顿时的偏移量px，与DEFAULT_CIRCLE_TARGET正比
    private var mSpinnerFinalOffset = 0f

    private var mNotify = false

    private var mHeaderViewWidth // headerView的宽度
            = 0

    private var mFooterViewWidth = 0

    private var mHeaderViewHeight = 0

    private var mFooterViewHeight = 0

    private val mUsingCustomStart = false

    private val targetScrollWithLayout = true

    private var pushDistance = 0

    private var density = 1.0f

    @JvmOverloads
    constructor(context: Context) : super(context, null)

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        mTouchSlop = ViewConfiguration.get(context).scaledTouchSlop

        setWillNotDraw(false)
        mDecelerateInterpolator = DecelerateInterpolator(
            DECELERATE_INTERPOLATION_FACTOR
        )

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
        mHeadViewContainer!!.measure(
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
            mOriginalOffsetTop = -(mHeadViewContainer!!.measuredHeight)
            mCurrentTargetOffsetTop = mOriginalOffsetTop
            updateListenerCallBack()
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

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
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
        var distance = mCurrentTargetOffsetTop + mHeadViewContainer!!.measuredHeight
        if (!targetScrollWithLayout) {
            // 判断标志位，如果目标View不跟随手指的滑动而滑动，将下拉偏移量设置为0
            distance = 0
        }
        val child: View = mTarget as View
        val childLeft = paddingLeft
        val childTop = paddingTop + distance - pushDistance // 根据偏移量distance更新

        val childWidth = width - paddingLeft - paddingRight
        val childHeight = height - paddingTop - paddingBottom
        Log.d(
            LOG_TAG,
            "debug:onLayout childHeight = $childHeight"
        )
        child.layout(
            childLeft, childTop, childLeft + childWidth, childTop
                    + childHeight
        ) // 更新目标View的位置

        val headViewWidth = mHeadViewContainer!!.measuredWidth
        val headViewHeight = mHeadViewContainer!!.measuredHeight
        mHeadViewContainer!!.layout(
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
     * 确保mTarget不为空<br></br>
     * mTarget一般是可滑动的ScrollView,ListView,RecyclerView等
     */
    private fun ensureTarget() {
        if (mTarget == null) {
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                if (child != mHeadViewContainer
                    && child != mFooterViewContainer
                ) {
                    mTarget = child
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
     * 主要判断是否应该拦截子View的事件<br></br>
     * 如果拦截，则交给自己的OnTouchEvent处理<br></br>
     * 否者，交给子View处理<br></br>
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
                setTargetOffsetTopAndBottom(
                    mOriginalOffsetTop - mHeadViewContainer!!.top, true
                ) // 恢复HeaderView的初始位置
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0)
                mIsBeingDragged = false
                val initialMotionY = getMotionEventY(ev, mActivePointerId)
                if (initialMotionY == -1f) {
                    return false
                }
                mInitialMotionY = initialMotionY // 记录按下的位置
                if (mActivePointerId == INVALID_POINTER) {
                    Log.e(
                        LOG_TAG,
                        "Got ACTION_MOVE event but don't have an active pointer id."
                    )
                    return false
                }
                val y = getMotionEventY(ev, mActivePointerId)
                if (y == -1f) {
                    return false
                }
                var yDiff = 0f
                if (isChildScrollToBottom()) {
                    yDiff = mInitialMotionY - y // 计算上拉距离
                    if (yDiff > mTouchSlop && !mIsBeingDragged) { // 判断是否下拉的距离足够
                        mIsBeingDragged = true // 正在上拉
                    }
                } else {
                    yDiff = y - mInitialMotionY // 计算下拉距离
                    if (yDiff > mTouchSlop && !mIsBeingDragged) { // 判断是否下拉的距离足够
                        mIsBeingDragged = true // 正在下拉
                    }
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (mActivePointerId == INVALID_POINTER) {
                    Log.e(
                        LOG_TAG,
                        "Got ACTION_MOVE event but don't have an active pointer id."
                    )
                    return false
                }
                val y = getMotionEventY(ev, mActivePointerId)
                if (y == -1f) {
                    return false
                }
                var yDiff = 0f
                if (isChildScrollToBottom()) {
                    yDiff = mInitialMotionY - y
                    if (yDiff > mTouchSlop && !mIsBeingDragged) {
                        mIsBeingDragged = true
                    }
                } else {
                    yDiff = y - mInitialMotionY
                    if (yDiff > mTouchSlop && !mIsBeingDragged) {
                        mIsBeingDragged = true
                    }
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
        return if (isChildScrollToBottom()) { // 上拉加载更多
            handlerPushTouchEvent(ev, action)
        } else { // 下拉刷新
            handlerPullTouchEvent(ev, action)
        }
    }

    private fun handlerPullTouchEvent(ev: MotionEvent, action: Int): Boolean {
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0)
                mIsBeingDragged = false
            }
            MotionEvent.ACTION_MOVE -> {
                val pointerIndex = MotionEventCompat.findPointerIndex(
                    ev,
                    mActivePointerId
                )
                if (pointerIndex < 0) {
                    Log.e(
                        LOG_TAG,
                        "Got ACTION_MOVE event but have an invalid active pointer id."
                    )
                    return false
                }
                val y = MotionEventCompat.getY(ev, pointerIndex)
                val overscrollTop = (y - mInitialMotionY) * DRAG_RATE
                if (mIsBeingDragged) {
                    val originalDragPercent = overscrollTop / mTotalDragDistance
                    if (originalDragPercent < 0) {
                        return false
                    }
                    val dragPercent = Math.min(1f, Math.abs(originalDragPercent))
                    val extraOS = Math.abs(overscrollTop) - mTotalDragDistance
                    var slingshotDist: Float
                    if (mUsingCustomStart) {
                        slingshotDist = mSpinnerFinalOffset - mOriginalOffsetTop
                    } else {
                        slingshotDist = mSpinnerFinalOffset
                    }
                    val tensionSlingshotPercent = max(
                        0f,
                        Math.min(extraOS, slingshotDist * 2) / slingshotDist
                    )
                    val tensionPercent = (tensionSlingshotPercent / 4 - Math
                        .pow((tensionSlingshotPercent / 4).toDouble(), 2.0)).toFloat() * 2f
                    val extraMove = slingshotDist * tensionPercent * 2
                    val targetY = (mOriginalOffsetTop
                            + (slingshotDist * dragPercent + extraMove).toInt())
                    if (mHeadViewContainer!!.visibility != VISIBLE) {
                        mHeadViewContainer!!.visibility = VISIBLE
                    }
                    if (overscrollTop < mTotalDragDistance) {
                        if (mScale) {
                            setAnimationProgress(overscrollTop / mTotalDragDistance)
                        }
                        mListener?.onPullEnable(false)
                    } else {
                        startRotateAnimation()
                        mListener?.onPullEnable(true)
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
                        Log.e(
                            LOG_TAG,
                            "Got ACTION_UP event but don't have an active pointer id."
                        )
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
                if (overscrollTop > mTotalDragDistance) {
                    setRefreshing(true, true /* notify */)
                } else {
                    mRefreshing = false
                    var listener: Animation.AnimationListener? = null
                    if (!mScale) {
                        listener = object : Animation.AnimationListener {
                            override fun onAnimationStart(animation: Animation) {}
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

    private fun startScaleDownAnimation(listener: Animation.AnimationListener?) {
        mScaleDownAnimation = object : Animation() {
            override fun applyTransformation(
                interpolatedTime: Float,
                t: Transformation
            ) {
                setAnimationProgress(1 - interpolatedTime)
            }
        }
        mScaleDownAnimation!!.duration = SCALE_DOWN_DURATION.toLong()
        mHeadViewContainer!!.setAnimationListener(listener)
        mHeadViewContainer!!.clearAnimation()
        mHeadViewContainer!!.startAnimation(mScaleDownAnimation)
    }

    /**
     * 开始旋转动画
     */
    private fun startRotateAnimation() {
        rotateAnimation = object : RotateAnimation(
            0f,
            360f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        ) {}
        rotateAnimation!!.duration = 1000
        rotateAnimation!!.repeatCount = 6
        rotateAnimation!!.setAnimationListener(mRefreshListener)
        mTargetChild!!.animation = rotateAnimation
        mTargetChild!!.startAnimation(rotateAnimation)
    }

    private fun setAnimationProgress(progress: Float) {
        var progress = progress
        progress = 1f
        ViewCompat.setScaleX(mHeadViewContainer, progress)
        ViewCompat.setScaleY(mHeadViewContainer, progress)
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
            mAnimateToStartPosition!!.duration = ANIMATE_TO_START_DURATION.toLong()
            mAnimateToStartPosition.interpolator = mDecelerateInterpolator
            if (listener != null) {
                mHeadViewContainer!!.setAnimationListener(listener)
            }
            mHeadViewContainer!!.clearAnimation()
            mHeadViewContainer!!.startAnimation(mAnimateToStartPosition)
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
                    ViewCompat.getScaleX(mHeadViewContainer) + (-ViewCompat.getScaleX(
                        mHeadViewContainer
                    ) * interpolatedTime)
                setAnimationProgress(targetScale)
                moveToStart(interpolatedTime)
            }
        }
        mScaleDownToStartAnimation!!.duration = SCALE_DOWN_DURATION.toLong()
        if (listener != null) {
            mHeadViewContainer!!.setAnimationListener(listener)
        }
        mHeadViewContainer!!.clearAnimation()
        mHeadViewContainer!!.startAnimation(mScaleDownToStartAnimation)
    }

    /**
     * 重置Target位置
     *
     * @param delay
     */
    fun resetTargetLayoutDelay(delay: Int) {
        Handler().postDelayed({ resetTargetLayout() }, delay.toLong())
    }

    /**
     * 处理上拉加载更多的Touch事件
     *
     * @param ev
     * @param action
     * @return
     */
    private fun handlerPushTouchEvent(ev: MotionEvent, action: Int): Boolean {
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0)
                mIsBeingDragged = false
                Log.d(LOG_TAG, "debug:onTouchEvent ACTION_DOWN")
            }
            MotionEvent.ACTION_MOVE -> {
                val pointerIndex = MotionEventCompat.findPointerIndex(
                    ev,
                    mActivePointerId
                )
                if (pointerIndex < 0) {
                    Log.e(
                        LOG_TAG,
                        "Got ACTION_MOVE event but have an invalid active pointer id."
                    )
                    return false
                }
                val y = MotionEventCompat.getY(ev, pointerIndex)
                val overscrollBottom = (mInitialMotionY - y) * DRAG_RATE
                if (mIsBeingDragged) {
                    pushDistance = overscrollBottom.toInt()
                    updateFooterViewPosition()
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
                        Log.e(
                            LOG_TAG,
                            "Got ACTION_UP event but don't have an active pointer id."
                        )
                    }
                    return false
                }
                val pointerIndex = MotionEventCompat.findPointerIndex(
                    ev,
                    mActivePointerId
                )
                val y = MotionEventCompat.getY(ev, pointerIndex)
                val overscrollBottom =
                    (mInitialMotionY - y) * DRAG_RATE // 松手是下拉的距离
                mIsBeingDragged = false
                mActivePointerId = INVALID_POINTER
                pushDistance = if (overscrollBottom < mFooterViewHeight
//                    || mOnPushLoadMoreListener == null
                ) { // 直接取消
                    0
                } else { // 下拉到mFooterViewHeight
                    mFooterViewHeight
                }
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                    updateFooterViewPosition()
                } else {
                    if (pushDistance < 0) {
                        resetTargetLayout()
                    }
                }
                return false
            }
        }
        return true
    }

    /**
     * Notify the widget that refresh state has changed. Do not call this when
     * refresh is triggered by a swipe gesture.
     *
     * @param refreshing Whether or not the view should show refresh progress.
     */
    fun setRefreshing(refreshing: Boolean) {
        if (refreshing && mRefreshing != refreshing) {
            // scale and show
            mRefreshing = refreshing
            var endTarget = 0
            endTarget = if (!mUsingCustomStart) {
                (mSpinnerFinalOffset + mOriginalOffsetTop).toInt()
            } else {
                mSpinnerFinalOffset.toInt()
            }
            setTargetOffsetTopAndBottom(
                endTarget - mCurrentTargetOffsetTop,
                true /* requires update */
            )
            mNotify = false
            startScaleUpAnimation(mRefreshListener)
        } else {
            setRefreshing(refreshing, false /* notify */)
//            if (usingDefaultHeader) {
//                defaultProgressView.setOnDraw(false)
//            }
        }
    }

    private fun setRefreshing(refreshing: Boolean, notify: Boolean) {
        if (mRefreshing != refreshing) {
            mNotify = notify
            ensureTarget()
            mRefreshing = refreshing
            if (mRefreshing) {
                animateOffsetToCorrectPosition(
                    mCurrentTargetOffsetTop,
                    mRefreshListener
                )
            } else {
                animateOffsetToStartPosition(mCurrentTargetOffsetTop, mRefreshListener)
            }
        }
    }

    private fun startScaleUpAnimation(listener: Animation.AnimationListener?) {
        mHeadViewContainer!!.visibility = VISIBLE
        mScaleAnimation = object : Animation() {
            override fun applyTransformation(
                interpolatedTime: Float,
                t: Transformation
            ) {
                setAnimationProgress(interpolatedTime)
            }
        }
        mScaleAnimation!!.duration = mMediumAnimationDuration.toLong()
        if (listener != null) {
            mHeadViewContainer!!.setAnimationListener(listener)
        }
        mHeadViewContainer!!.clearAnimation();
        mHeadViewContainer!!.startAnimation(mScaleAnimation);
    }

    private fun animateOffsetToCorrectPosition(
        from: Int,
        listener: Animation.AnimationListener?
    ) {
        mFrom = from
        if (listener != null) {
            mHeadViewContainer!!.setAnimationListener(listener)
        }
        mHeadViewContainer!!.clearAnimation()
        mHeadViewContainer!!.startAnimation(mAnimateToCorrectPosition)
    }

    private val mAnimateToCorrectPosition: Animation = object : Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            var targetTop = 0
            var endTarget = 0
            endTarget = if (!mUsingCustomStart) {
                (mSpinnerFinalOffset - Math
                    .abs(mOriginalOffsetTop)).toInt()
            } else {
                mSpinnerFinalOffset.toInt()
            }
            targetTop = mFrom + ((endTarget - mFrom) * interpolatedTime).toInt()
            val offset = targetTop - mHeadViewContainer!!.top
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
//        mOnPushLoadMoreListener?.onPushDistance(pushDistance)
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
        mHeadViewContainer!!.bringToFront()
        mHeadViewContainer!!.offsetTopAndBottom(offset)
        mCurrentTargetOffsetTop = mHeadViewContainer!!.top
        if (requiresUpdate && Build.VERSION.SDK_INT < 11) {
            invalidate()
        }
        updateListenerCallBack()
    }


    /**
     * 判断目标View是否滑动到顶部-还能否继续滑动
     *
     * @return
     */
    fun isChildScrollToTop(): Boolean {
        return if (Build.VERSION.SDK_INT < 14) {
            mTarget!!.scrollY <= 0
        } else {
            !ViewCompat.canScrollVertically(mTarget, -1)
        }
    }

    /**
     * 是否滑动到底部
     *
     * @return
     */
    fun isChildScrollToBottom(): Boolean {
        if (isChildScrollToTop()) {
            return false
        }
        if (mTarget is RecyclerView) {
            val recyclerView = mTarget as RecyclerView
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
                val lastItem = Math.max(lastItems[0], lastItems[1])
                if (lastItem == count - 1) {
                    return true
                }
            }
            return false
        }
        return false
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
        mHeadViewContainer = HeadViewContainer(context)
        mHeadViewContainer!!.visibility = GONE
        addView(mHeadViewContainer)
    }

    /**
     * 添加头布局
     *
     * @param child
     */
    fun setHeaderView(child: View?) {
        if (child == null) {
            return
        }
        if (mHeadViewContainer == null) {
            return
        }
        mHeadViewContainer!!.removeAllViews()
        val layoutParams = RelativeLayout.LayoutParams(
            mHeaderViewWidth, mHeaderViewHeight
        )
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
        mHeadViewContainer!!.addView(child, layoutParams)
        mTargetChild = child.header_progress
    }

    /**
     * 添加底部布局
     */
    private fun createFooterViewContainer() {
        mFooterViewContainer.setVisibility(GONE)
        addView(mFooterViewContainer)
    }

    /**
     * 更新回调
     */
    private fun updateListenerCallBack() {
        val distance = mCurrentTargetOffsetTop + mHeadViewContainer!!.height
        mListener?.onPullDistance(distance)
    }


    private class HeadViewContainer(context: Context?) :
        RelativeLayout(context) {
        private var mListener: Animation.AnimationListener? = null

        fun setAnimationListener(listener: Animation.AnimationListener?) {
            mListener = listener
        }

        public override fun onAnimationStart() {
            super.onAnimationStart()
            mListener!!.onAnimationStart(animation)
        }

        public override fun onAnimationEnd() {
            super.onAnimationEnd()
            if (null != mListener && animation != null) {
                mListener!!.onAnimationEnd(animation)
            }
        }
    }


    /**
     * 下拉时，超过距离之后，弹回来的动画监听器
     */
    private val mRefreshListener: Animation.AnimationListener =
        object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
            }

            override fun onAnimationRepeat(animation: Animation) {

            }

            override fun onAnimationEnd(animation: Animation) {
                if (mRefreshing) {
                    setRefreshing(false, true)
                    if (mNotify && null != mListener) {
                        mListener!!.onRefresh()
                    }
                } else {
                    mTargetChild!!.clearAnimation()
                    mHeadViewContainer!!.visibility = GONE
                    setTargetOffsetTopAndBottom(
                        mOriginalOffsetTop
                                - mCurrentTargetOffsetTop, true
                    )
                }
                mCurrentTargetOffsetTop = mHeadViewContainer!!.top
                updateListenerCallBack()
            }
        }

    /**
     * 重置Target的位置
     */
    private fun resetTargetLayout() {
        val width = measuredWidth
        val height = measuredHeight
        val child = mTarget!!
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
        val headViewWidth = mHeadViewContainer!!.measuredWidth
        val headViewHeight = mHeadViewContainer!!.measuredHeight
        mHeadViewContainer!!.layout(
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
        val offset = targetTop - mHeadViewContainer!!.top
        setTargetOffsetTopAndBottom(offset, false /* requires update */)
    }
}