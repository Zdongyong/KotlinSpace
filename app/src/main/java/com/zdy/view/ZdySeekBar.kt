package com.zdy.view

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.core.view.ViewCompat
import com.zdy.mykotlin.R
import java.text.DecimalFormat


/**
 * 创建日期：6/20/22 on 10:32 PM
 * 描述：
 * 作者：zhudongyong
 */
class ZdySeekBar : AppCompatSeekBar {

    private val TAG:String = "ZdySeekBar"

    private var mTextPaint = Paint()
    private var mLinePaint = Paint()
    private var mRectF: RectF? = null
    private var mCirclePaint = Paint()
    private var mCirclePaint2 = Paint()

    private var mThumb: Drawable? = null


    private var mMaxShowValue //需要显示的最大值
            = 0f
    private var mPrecisionMode //精度模式
            = 0
    private var mViewWidth = 0
    private var mCenterX = 0
    private var mCenterY = 0
    private var mThumbHeight = 0

    @JvmOverloads
    constructor(context: Context) : this(context, null)

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        val typedArray: TypedArray =
            context.obtainStyledAttributes(attrs, R.styleable.DigitalThumbSeekbar)
        mMaxShowValue =
            typedArray.getFloat(
                R.styleable.DigitalThumbSeekbar_maxShowValue,
                max.toFloat()
            ) //获取最大显示值

        mPrecisionMode = typedArray.getInt(R.styleable.DigitalThumbSeekbar_PrecisionMode, 0) //进度模式

        typedArray.recycle() //释放资源

        //设置滑块样式
        mThumb = context.resources.getDrawable(R.drawable.circle_thumb)
        thumb = mThumb
        thumbOffset = 0
        initPaint() //初始化画笔
    }


    private fun initPaint() {
        //文字画笔
        mCirclePaint.color = resources.getColor(R.color.white,null);
        mTextPaint.isAntiAlias = true
        mTextPaint.textSize = (mThumb!!.minimumHeight * 3 / 7).toFloat() //文字大小为滑块高度的2/3
        mTextPaint.textAlign = Paint.Align.CENTER // 设置文本对齐方式，居中对齐

        //进度画笔
        mLinePaint.isAntiAlias = true

        //实心圆
        mCirclePaint.color = resources.getColor(R.color.colorPrimary,null);
        mCirclePaint.isAntiAlias = true
        mCirclePaint.style = Paint.Style.FILL_AND_STROKE
        //        mCirclePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));

        //空心圆
        mCirclePaint.color = resources.getColor(R.color.colorPrimaryDark,null);
        mCirclePaint2.isAntiAlias = true
        mCirclePaint2.strokeWidth = (mThumb!!.minimumHeight / 20).toFloat()
        mCirclePaint2.style = Paint.Style.STROKE
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //获取滑块坐标
        val thumbRect: Rect = thumb.bounds
        mCenterX = thumbRect.centerX() //中心X坐标

        mCenterY = thumbRect.centerY() //中心Y坐标

        mThumbHeight = thumbRect.height() //滑块高度

        //绘制进度条
        drawRect(canvas)

        //绘制滑块
        canvas.drawCircle(
            mCenterX.toFloat(),
            mCenterY.toFloat(),
            (mThumbHeight / 2).toFloat(),
            mCirclePaint!!
        )
        canvas.drawCircle(
            mCenterX.toFloat(),
            mCenterY.toFloat(),
            (mThumbHeight / 2 - mThumbHeight / 20).toFloat(),
            mCirclePaint2
        ) //描边

        //绘制进度文字
        drawProgress(canvas)
    }

    @Synchronized
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //父容器传过来的宽度的值
        var heightMeasureSpec = heightMeasureSpec
        mViewWidth = MeasureSpec.getSize(widthMeasureSpec) - paddingLeft - paddingRight
        //根据滑块的尺寸确定大小 布局文件中的android:layout_height="" 任何设置不会改变绘制的大小
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(mThumb!!.minimumHeight, MeasureSpec.EXACTLY)
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }


    /**
     * 绘制进度条
     * @param canvas
     */
    private fun drawRect(canvas: Canvas) {
        //绘制左边的进度
        mRectF = RectF()
        mRectF?.left = 0f
        //        mRectF.right = thumbRect.left;
        mRectF?.right = mCenterX.toFloat()
        mRectF?.top = (mCenterY - mThumbHeight / 4).toFloat()
        mRectF?.bottom = (mCenterY + mThumbHeight / 4).toFloat()
        mLinePaint.color = Color.GREEN
        canvas.drawRoundRect(
            mRectF!!, (mThumbHeight / 4).toFloat(),
            (mThumbHeight / 4).toFloat(), mLinePaint!!
        )

        //绘制右边剩余的进度
        mRectF?.left = mCenterX.toFloat()
        mRectF?.right = mViewWidth.toFloat()
        mRectF?.top = (mCenterY - mThumbHeight / 15).toFloat()
        mRectF?.bottom = (mCenterY + mThumbHeight / 15).toFloat()
        mLinePaint?.setARGB(255, 255, 65, 130)
        canvas.drawRoundRect(
            mRectF!!, (mThumbHeight / 15).toFloat(),
            (mThumbHeight / 15).toFloat(), mLinePaint!!
        )
    }

//    override fun onTouchEvent(event: MotionEvent?): Boolean {
//        when (event?.action) {
//            MotionEvent.ACTION_DOWN -> {
//                Log.i(TAG, "===========ACTION_DOWN============")
//            }
//            MotionEvent.ACTION_MOVE -> {
//
//            }
//            // 抬起/取消
//            MotionEvent.ACTION_CANCEL,
//            MotionEvent.ACTION_UP -> {
//                Log.i(TAG, "===========ACTION_UP============")
//            }
//        }
//        return false
//    }


    /**
     * 绘制显示的进度文本
     * @param canvas
     */
    private fun drawProgress(canvas: Canvas) {
        val progress: String
        val score: Float = mMaxShowValue * getProgress() / max
        progress = when (mPrecisionMode) {
            1 -> {
                val f1Score = Math.round(score * 10).toFloat() / 10
                "" + f1Score
            }
            2 -> {
                val fnum = DecimalFormat("##0.00")
                fnum.format(score)
            }
            else -> "" + score.toInt()
        }
        //测量文字高度
        val bounds = Rect()
        mTextPaint?.getTextBounds(progress, 0, progress.length, bounds)
        val mTextHeight: Int = bounds.height() //文字高度
        //      float mTextWidth = mTextPaint.measureText(progress);
        canvas.drawText(
            progress, mCenterX.toFloat(),
            (mCenterY + mTextHeight / 2).toFloat(), mTextPaint!!
        )
    }

}