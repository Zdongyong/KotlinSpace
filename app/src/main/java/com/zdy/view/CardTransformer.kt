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
    }

    private var mHandler: Handler = object :Handler(){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            setRotationY(msg.obj as View)
        }
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
//                    if (position != 0f){
//                        page.alpha = abs(percentage) / 3
//                    } else{
//                        page.alpha = 1.0f
//                    }
                }
                position < RIGHT -> { // (0,1]
//                    page.alpha = 1.0f * percentage
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
        page.cameraDistance = -30000f
        setPivot(page, page.width.toFloat(), page.height.toFloat()) //设置翻转轴
        setTranslation(page)
        if (position <= 0) {
            page.rotationY = 180 * (percentage + 1)
        }
    }

    /**
     * 处理上一页
     *
     * 回弹效果
     *
     */
    private fun flipNextPage(page: View, position: Float, percentage: Float) {
        page.cameraDistance = -30000f
        setTranslation(page)
        setPivot(page, page.width.toFloat(), page.height.toFloat()) //设置翻转轴
        if (position > 0) {
            page.rotationY = max(-170 * (percentage + 1) - 85, -360f)

        }
//        if (RIGHT - position < position - CENTER){ // 递增 顺时针
//            mHandler.removeMessages(1)
//            val msg = Message.obtain()
//            msg.what = 1
//            msg.obj = page
//            mHandler.sendMessageDelayed(msg,100)
//        }
    }

    private fun setRotationY(view: View) {
        var animator = ObjectAnimator.ofFloat(view, "rotationY", -360f, -361.3f)
        animator.duration = 117
        animator.interpolator = PathInterpolator(0.32f, 0.8f, 0.78f, 1f)
        animator = ObjectAnimator.ofFloat(view, "rotationY", -361.3f, -360f)
        animator.duration = 333
        animator.interpolator = PathInterpolator(0.27f, 0.01f, 0.71f, 1f)
        animator.start()
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