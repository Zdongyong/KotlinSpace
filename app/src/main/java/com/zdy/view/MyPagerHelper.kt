package com.zdy.view

import android.animation.Animator
import android.animation.ValueAnimator
import android.os.Handler
import android.util.Log
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.view.animation.PathInterpolator
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.zdy.view.verticalTab.VerticalTabView
import java.lang.reflect.Method
import kotlin.math.abs

/**
 * 创建日期：11/2/21 on 10:47 PM
 * 描述：
 * 作者：zhudongyong
 */
class MyPagerHelper constructor(private val vp: ViewPager2){

    companion object {
        private const val DURATION_SHORT: Long = 450
        private const val DURATION_MIDDLE: Long = 900
        private const val DURATION_LONG: Long = 1200
    }

    private val recyclerView: RecyclerView
    private val mScrollEventAdapter: Any
    private val getRelativeScrollPositionMethod: Method
    private val notifyProgrammaticScrollMethod: Method

    private var previousValue: Int = 0

    private val mDecelerateInterpolator: DecelerateInterpolator = DecelerateInterpolator(2f)

    private val translationAnimPath = PathInterpolator(0.4f, 0f, 0.20f, 0.96f)


    init {
        val mRecyclerViewField = ViewPager2::class.java.getDeclaredField("mRecyclerView")
        mRecyclerViewField.isAccessible = true
        recyclerView = mRecyclerViewField.get(vp) as RecyclerView
        val mScrollEventAdapterField =
            ViewPager2::class.java.getDeclaredField("mScrollEventAdapter")
        mScrollEventAdapterField.isAccessible = true
        mScrollEventAdapter = mScrollEventAdapterField.get(vp)
        getRelativeScrollPositionMethod =
            mScrollEventAdapter.javaClass.getDeclaredMethod("getRelativeScrollPosition")
        getRelativeScrollPositionMethod.isAccessible = true
        notifyProgrammaticScrollMethod = mScrollEventAdapter.javaClass.getDeclaredMethod(
            "notifyProgrammaticScroll",
            Int::class.java,
            Boolean::class.java
        )
        notifyProgrammaticScrollMethod.isAccessible = true
    }

    /**
     * 设置当前Item
     * @param verticalTabView    VerticalTabView
     * @param cardTransformer    CardTransformer
     * @param item     下一个跳转的item
     */
    fun setCurrentItemWithAnimator(
        verticalTabView: VerticalTabView,
        cardTransformer: CardTransformer,
        item: Int
    ) {
//        previousValue = 0
        val currentItem = vp.currentItem
        val pxToDrag = vp.width * (item - currentItem)
        val intAnimator = ValueAnimator.ofInt(0, pxToDrag)
        intAnimator.addUpdateListener { animation ->
            val currentValue = animation.animatedValue as Int
            val currentPxToDrag = (currentValue - previousValue).toFloat()
            if (vp.isFakeDragging) {
                vp.fakeDragBy(-currentPxToDrag)
            }
            previousValue = currentValue
        }

        intAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                if (!vp.isFakeDragging) {
                    vp.beginFakeDrag()
                }
//                verticalTabView.setCanTouch(false)
            }

            override fun onAnimationEnd(animation: Animator) {
                if (vp.isFakeDragging) {
                    vp.endFakeDrag()
                }
//                verticalTabView.setCanTouch(true)
                if (item - currentItem > 0) { // 顺时针
                    cardTransformer.setRotationClockWise()
                } else {
                    cardTransformer.setRotationUnClockWise()
                }
            }

            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })

        intAnimator.interpolator = translationAnimPath
        var abst = abs(item - currentItem)
//        intAnimator.duration = DURATION_SHORT * abst
        intAnimator.duration = when {
            abst <= 1 -> {
                DURATION_SHORT
            }
            abst <= 3 -> {
                DURATION_MIDDLE
            }
            else -> {
                DURATION_LONG
            }
        }
        intAnimator.start()

    }

    fun setCurrentItem(
        item: Int
    ) {
        previousValue = 0
        val currentItem = vp.currentItem
        val pxToDrag = vp.width * (item - currentItem)
        val intAnimator = ValueAnimator.ofInt(0, pxToDrag)
        intAnimator.addUpdateListener { animation ->
            val currentValue = animation.animatedValue as Int
            val currentPxToDrag = (currentValue - previousValue).toFloat()
            if (vp.isFakeDragging) {
                vp.fakeDragBy(-currentPxToDrag)
            }
            previousValue = currentValue
        }
        intAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                if (!vp.isFakeDragging) {
                    vp.beginFakeDrag()
                }
            }

            override fun onAnimationEnd(animation: Animator) {
                if (vp.isFakeDragging) {
                    vp.endFakeDrag()
                }
            }

            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
        intAnimator.duration = 10
        intAnimator.start()

    }

}