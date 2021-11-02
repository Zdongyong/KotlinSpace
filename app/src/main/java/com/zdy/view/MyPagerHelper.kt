package com.zdy.view

import android.animation.Animator
import android.animation.ValueAnimator
import android.os.Handler
import android.util.Log
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.view.animation.PathInterpolator
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs

/**
 * 创建日期：11/2/21 on 10:47 PM
 * 描述：
 * 作者：zhudongyong
 */
class MyPagerHelper {

    companion object {
        /**
         * 保存前一个animatedValue
         */
        private var previousValue = 0

        /**
         * 设置当前Item
         * @param pager    viewpager2
         * @param item     下一个跳转的item
         * @param duration scroll时长
         */
        fun setCurrentItem(pager: ViewPager2, item: Int, duration: Long) {
            previousValue = 0
            val currentItem = pager.currentItem
            val pagePxWidth = pager.width
            val pxToDrag = pagePxWidth * (item - currentItem)
            val animator = ValueAnimator.ofInt(0, pxToDrag)

            animator.addUpdateListener { animation ->
                val currentValue = animation.animatedValue as Int
                val currentPxToDrag = (currentValue - previousValue).toFloat()
                pager.fakeDragBy(-currentPxToDrag)
                previousValue = currentValue
            }
            animator.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    pager.beginFakeDrag()
                }

                override fun onAnimationEnd(animation: Animator) {
                    pager.endFakeDrag()
                }

                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            })
            animator.interpolator = PathInterpolator(0.4f,0f,0.61f,0.94f)
            animator.duration = duration * abs(item - currentItem)
            animator.start()
        }
    }

}