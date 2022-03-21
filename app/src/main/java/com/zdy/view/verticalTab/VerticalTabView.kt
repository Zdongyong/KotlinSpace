package com.zdy.view.verticalTab

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.*
import androidx.core.view.marginTop
import com.bumptech.glide.Glide
import com.zdy.mykotlin.R
import kotlin.math.ceil
import kotlin.math.floor

/**
 * 创建日期：9/26/21 on 11:39 PM
 * 描述：存在滑动和点击两个效果
 *      通过ViewPager - onPageScrolled 来控制变化效果
 * 作者：zhudongyong
 */
class VerticalTabView : ScrollView {

    private var mSelectedTab: TitleView? = null //默认选中的title
    private var holderView: HolderView? = null//容器
    private var mIndicatorRadius = 15f // 指示器半径

    //动画插值器 开始快然后慢
    private var mDecelerateInterpolator: DecelerateInterpolator = DecelerateInterpolator(
        2f
    )

    private var onTabSelectedListener: OnTabSelectedListener? = null

    constructor(context: Context?) : this(context, null)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    private fun initView() {
        holderView = HolderView(context)
        addView(
            holderView, LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
            )
        )
    }

    /**
     * 设置title (name + title)
     */
    fun setTab(tabTitles: List<TabTitle>) {
        tabTitles.forEach {
            val titleView = TitleView(context)
            titleView.setTitle(it.icon, it.title)
            addTabInHolder(titleView)
        }
    }


    private fun addTabInHolder(titleView: TitleView) {
        var layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        holderView?.addView(titleView, layoutParams)
        if (holderView?.indexOfChild(titleView) == 0) {
            titleView.isChecked = true
            layoutParams = titleView.layoutParams as LinearLayout.LayoutParams
            layoutParams.setMargins(0, 0, 0, 0)
            titleView.layoutParams = layoutParams
            mSelectedTab = titleView
            post {
                holderView?.moveIndicator(0f)
            }
        }
        titleView.setOnClickListener {
            val index = holderView?.indexOfChild(it)
            setTabSelected(index)
        }
    }

    /**
     * 选中tab
     */
    fun setTabSelected(index: Int?) {
        index?.let {
            val currentView = holderView?.getChildAt(it) as TitleView
            var selected = false
            if (currentView !== mSelectedTab) {
                if (mSelectedTab != null) {
                    mSelectedTab!!.isChecked = false
                }
                selected = true
                currentView.isChecked = true
                holderView?.moveIndicatorWithAnimator(it)
                mSelectedTab = currentView
            }

            onTabSelectedListener?.let { it1 ->
                if (selected){
                    it1.onTabSelected(index)
                }
            }
        }
    }

    /**
     * tab-title
     */
    inner class TitleView: FrameLayout, Checkable {
        private var _icon: ImageView? = null
        private var _title: TextView? = null
        private var view: View? = null
        private var isChecked:Boolean = false

        @JvmOverloads
        constructor(context: Context) : super(context){
            view = inflate(context, R.layout.vertical_iterm, this)
            view?.let {
                _icon = it.findViewById(R.id.title_icon)
                _title = it.findViewById(R.id.title_name)
            }
        }

        fun setTitle(sourceId: Int, title: String) {
            _icon?.let {
                Glide.with(it).load("https://ai-os-feeds-admin-static.vivo.com.cn/ai-os-feeds-admin/news/yangshi/20220308/ac3583abe6244a04b064cf16e75352c2.webp")
                    .into(it)
            }
//            _icon?.setImageResource(sourceId)
            _title?.text = title
        }

        override fun setChecked(checked: Boolean) {
            isChecked = checked
        }

        override fun isChecked(): Boolean {
            return isChecked
        }

        override fun toggle() {
            setChecked(!isChecked)
        }
    }

    /**
     * 容器，用于承载ScrollView里面的view
     *
     *
     */
    inner class HolderView(context: Context?) : LinearLayout(context) {

        private var mIndicatorPaint: Paint? = null
        private var mLinePaint: Paint? = null
        private var mIndicatorX = 0f
        private var mIndicatorY = 0f
        private var mIndicatorAnimatorSet: AnimatorSet? = null


        init {
            setWillNotDraw(false);
            mIndicatorPaint = Paint()
            mIndicatorPaint?.color = Color.RED
            mIndicatorPaint?.style = Paint.Style.STROKE //空心
            mIndicatorPaint?.strokeWidth = 10f
            mIndicatorPaint?.isAntiAlias = true
            mLinePaint = Paint()
            mLinePaint?.color = Color.WHITE //设置画笔颜色
            mLinePaint?.strokeWidth = 2.0f

            orientation = LinearLayout.VERTICAL
            setPadding(0, 20, mIndicatorRadius.toInt(), 0)
            post {
                mIndicatorX = width - mIndicatorRadius * 2
//                mIndicatorX = width.toFloat()
                invalidate()
            }
        }

        fun moveIndicator(offset: Float) {
            getIndicatorY(offset)
            invalidate()
        }

        fun moveIndicatorWithAnimator(position: Int) {
            val childView = getChildAt(position)
            val targetTop = childView.top.toFloat() + width / 2
            if (childView == mSelectedTab || targetTop == mIndicatorY) return
            if (null != mIndicatorAnimatorSet && mIndicatorAnimatorSet!!.isRunning) {
                mIndicatorAnimatorSet?.end()
            }
            post {
                val startAnime = ValueAnimator.ofFloat(mIndicatorY, targetTop)
                startAnime.duration = 500
                startAnime.addUpdateListener(ValueAnimator.AnimatorUpdateListener { animation ->
                    mIndicatorY = animation.animatedValue.toString().toFloat()
                    invalidate()
                })
                if (startAnime != null) {
                    mIndicatorAnimatorSet = AnimatorSet()
                    mIndicatorAnimatorSet?.interpolator = mDecelerateInterpolator
                    mIndicatorAnimatorSet?.play(startAnime)
                    mIndicatorAnimatorSet?.start()
                }
            }
        }

        private fun getIndicatorY(offset: Float) {
            val index = floor(offset.toDouble()).toInt()
            val currentChildView = getChildAt(index)
            mIndicatorY =
                if (floor(offset.toDouble()) != (childCount - 1).toDouble() && ceil(offset.toDouble()) != 0.0) {
                    val nextView = getChildAt(index + 1)
                    currentChildView.top + (nextView.top - currentChildView.top + currentChildView.height / 2 + marginTop + 10) * (offset - index)
                } else {
                    (currentChildView.top + currentChildView.height / 2 + marginTop + 10).toFloat()
                }
        }

        /**
         * 绘制位置
         */
        override fun onDraw(canvas: Canvas?) {
            super.onDraw(canvas)
            canvas?.drawCircle(mIndicatorX, mIndicatorY, mIndicatorRadius, mIndicatorPaint!!)
            canvas?.drawLine(mIndicatorX, 0f, mIndicatorX, height.toFloat(), mLinePaint!!)
        }
    }

    interface OnTabSelectedListener {
        fun onTabSelected(position: Int)
    }

    fun setOnTabSelectedListener(listener: OnTabSelectedListener) {
        onTabSelectedListener = listener
    }

}