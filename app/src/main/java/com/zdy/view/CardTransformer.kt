package com.zdy.view

import android.animation.ObjectAnimator
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.View
import android.view.animation.PathInterpolator
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
        private const val ROTATION_OFFSET_X = 35
        private const val ROTATION_FIRST = 117L
        private const val ROTATION_SECOND = 333L
    }

    private lateinit var view: View
    private val rotationAnimPathF =
        PathInterpolator(0.32f, 0.8f, 0.78f, 1.0f)
    private val rotationAnimPathS =
        PathInterpolator(0.27f, 0.01f, 0.71f, 1.0f)

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
                    page.cameraDistance = -30000f
                    flipPage(page, position, percentage)
                }
                position < RIGHT -> { // (0,1]A
                    page.cameraDistance = -30000f
                    flipNextPage(page, position, percentage) //下一页
                }
            }

            if (position > -1.0f && position < 1.0f) {
                page.visibility = View.VISIBLE
            } else {
                page.visibility = View.INVISIBLE
            }
        }
    }

    /**
     * 处理当前页
     */
    private fun flipPage(page: View, position: Float, percentage: Float) {
        setPivot(page, page.width.toFloat(), page.height.toFloat()) //设置翻转轴
        setTranslation(page)
//        if (position != 0f) {
//            if (isClockWise){ //需要区分是顺时针还是逆时针
//                page.alpha = 1.0f * percentage / 2
//            } else{
//                page.alpha = 1.0f * percentage * 3 / 2
//            }
//        } else {
//            page.alpha = 1.0f
//        }
        if (position <= 0) {
            page.rotationY = 180 * (percentage + 1)
        }
        view = page
    }

    /**
     * 处理下一页
     */
    private fun flipNextPage(page: View, position: Float, percentage: Float) {
        setTranslation(page)
        setPivot(page, page.width.toFloat(), page.height.toFloat()) //设置翻转轴
//        page.alpha = 1.0f * percentage
        if (position > 0) {
            page.rotationY = 90 * (1 - percentage)
        }
        view = page
    }

    fun setRotationClockWise() {
        view.clearAnimation()
        var animator = ObjectAnimator.ofFloat(view, "rotationY", -360f, -361.3f)
        animator.duration = ROTATION_FIRST
        animator.interpolator = rotationAnimPathF
        animator = ObjectAnimator.ofFloat(view, "rotationY", -361.3f, -360f)
        animator.duration = ROTATION_SECOND
        animator.interpolator = rotationAnimPathS
        animator.start()
    }

    fun setRotationUnClockWise() {
        view.clearAnimation()
        var animator = ObjectAnimator.ofFloat(view, "rotationY", 360f, 361.3f)
        animator.duration = ROTATION_FIRST
        animator.interpolator = rotationAnimPathF
        animator = ObjectAnimator.ofFloat(view, "rotationY", 361.3f, 360f)
        animator.duration = ROTATION_SECOND
        animator.interpolator = rotationAnimPathS
        animator.start()
    }

    private fun setPivot(page: View, pivotX: Float, pivotY: Float) {
        page.pivotX = pivotX + ROTATION_OFFSET_X
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

}