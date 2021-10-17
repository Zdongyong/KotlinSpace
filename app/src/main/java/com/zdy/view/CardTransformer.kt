package com.zdy.view

import android.animation.ObjectAnimator
import android.util.Log
import android.view.View
import androidx.annotation.NonNull
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs
import kotlin.math.max


/**
 * 创建日期：10/18/21 on 12:07 AM
 * 描述：
 * 作者：zhudongyong
 */
class CardTransformer : ViewPager2.PageTransformer {

    companion object {
        private const val LEFT = -1
        private const val CENTER = 0 //表示当前
        private const val RIGHT = 1
        private const val scaleAmountPercent = 5f

    }

    /**
     * position - 指定页面相对于屏幕中心(旋转轴)的位置
     */
    override fun transformPage(@NonNull page: View, position: Float) {
        page.apply {
            val percentage = 1 - abs(position)
            when {
                position < LEFT -> { // [-Infinity,-1)
                    page.translationX = -position * page.width
                    page.translationY = 0f
                    page.translationZ = -1f
                    page.rotation = 0f
                }
                position <= CENTER -> { // [-1,0]
                    flipPage(page, position, percentage)
                    val amount: Float =
                        (100 - scaleAmountPercent + scaleAmountPercent * percentage) / 100
                    setSize(page, position, amount)
                }
                position < RIGHT -> { // (0,1]
                    flipLastPage(page, position, percentage)
                }
            }
        }
    }

    private fun setSize(page: View, position: Float, percentage: Float) {
//        Log.d("123123","=====$position======$percentage=")
//        page.alpha(if (position != 0f && position != 1f) percentage else 1f)
    }

    /**
     * 处理当前页
     */
    private fun flipPage(page: View, position: Float, percentage: Float) {
        page.cameraDistance = -30000f
        setTranslation(page)
        setPivot(page, page.width.toFloat(), page.height.toFloat()) //设置翻转轴
        if (position <= 0) {
            page.rotationY = 180 * (percentage + 1)
        }
    }

    /**
     * 处理上一页
     */
    private fun flipLastPage(page: View, position: Float, percentage: Float) {
        page.cameraDistance = -30000f
        setTranslation(page)
        setPivot(page, page.width.toFloat(), page.height.toFloat()) //设置翻转轴
        if (position > 0) {
            page.rotationY = max(-180 * (percentage + 1) - 85, -360f)
        }
    }

    private fun setPivot(page: View, pivotX: Float, pivotY: Float) {
        page.pivotX = pivotX
        page.pivotY = pivotY
    }

    private fun setTranslation(page: View) {
        val viewPager = requireViewPager(page)
        val scroll = viewPager?.scrollX?.minus(page.left)
        if (scroll != null) {
            page.translationX = scroll.toFloat()
        }
        page.translationZ = 1f
    }

    private fun requireViewPager(@NonNull page: View): ViewPager2? {
        val parent = page.parent
        val parentParent = parent.parent
        if (parentParent is ViewPager2) {
            return parentParent
        }
        throw IllegalStateException(
            "Expected the page view to be managed by a ViewPager2 instance."
        )
    }

    private fun setTransformer(view: View) {
//        var rotateAnimation = RotateAnimation(0f, 90f, view.width.toFloat(), view.height.toFloat())
//        rotateAnimation.duration = 1000
//        rotateAnimation = RotateAnimation(-90f, 0f, view.width.toFloat(), view.height.toFloat())
//        rotateAnimation.duration = 1000
        var animator = ObjectAnimator.ofFloat(view, "rotationY", 0f, 90f)
        animator.duration = 1000
        animator = ObjectAnimator.ofFloat(view, "rotationY", -90f, 0f)
        animator.duration = 1000
//        view.startAnimation(rotateAnimation)
        animator.start()
    }

}