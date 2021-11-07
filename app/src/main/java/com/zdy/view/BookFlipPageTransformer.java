package com.zdy.view;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;
import static java.lang.Math.min;
import com.nineoldandroids.view.ViewHelper;

public class BookFlipPageTransformer implements ViewPager2.PageTransformer {

    private int THREE_d = 0;
    private int ROTATE_EFFECT = 1;
    private int ALPHA_EFFECT = 2;
    private int DEGREE_EFFECT = 3;
    private int PAGE_TURNING_EFFECT = 4;
    private int ZOOM_IN_EFFECT = 5;
    private int ZOOM_OUT_EFFECT = 6;
    private int ZOOM_SLIDE_EFFECT = 7;
    private int ACCORDION_EFFECT = 8;
    private int BACKGROUND_TO_FORE = 9;
    private int CUBE_IN_EFFECT = 10;
    private int FLIP_HORIZONTAL_EFFECT = 11;
    private int FLIP_VERTICAL_EFFECT = 12;
    private int ROTATE_UP = 13;
    private int SCALE_IN_OUT = 14;
    private int STACK_EFFECT = 15;
    private int TABLE_EFFECT = 16;
    private int mEffect = 0;
    private float MIN_SCALE = 0.75f;
    private final float MIN_SCALE_ALPHA = 0.5f;
    private final float MIN_ALPHA = 0.5f;

    public BookFlipPageTransformer(int effect) {
        this.mEffect = effect;
    }

    @Override
    public void transformPage(@NonNull View page, float position) {
        if (mEffect == THREE_d) {
            three_d(page, position);
        } else if (mEffect == ROTATE_EFFECT) {
            setRotate(page, position);
        } else if (mEffect == ALPHA_EFFECT) {
            setAlpha(page, position);
        } else if (mEffect == DEGREE_EFFECT) {
            setDegree(page, position);
        } else if (mEffect == PAGE_TURNING_EFFECT) {
            pageTurning(page, position);
        } else if (mEffect == ZOOM_IN_EFFECT) {
            zoomIn(page, position);
        } else if (mEffect == ZOOM_OUT_EFFECT) {
            zoomOut(page, position);
        } else if (mEffect == ZOOM_SLIDE_EFFECT) {
            zoomSlide(page, position);
        } else if (mEffect == ACCORDION_EFFECT) {
            accordion(page, position);
        } else if (mEffect == BACKGROUND_TO_FORE) {
            backgroundToForeground(page, position);
        } else if (mEffect == CUBE_IN_EFFECT) {
            cubeIn(page, position);
        } else if (mEffect == FLIP_HORIZONTAL_EFFECT) {
            flipHorizontal(page, position);
        } else if (mEffect == FLIP_VERTICAL_EFFECT) {
            flipVertical(page, position);
        } else if (mEffect == ROTATE_UP) {
            rotateUp(page, position);
        } else if (mEffect == SCALE_IN_OUT) {
            scaleInOut(page, position);
        } else if (mEffect == STACK_EFFECT) {
            stack(page, position);
        } else if (mEffect == TABLE_EFFECT) {
            table(page, position);
        }
    }

    private void three_d(View page, float position) {
//3d旋转
        int width = page.getWidth();
        int pivotX = 0;
        if (position <= 1 && position > 0) {// right scrolling
            pivotX = 0;
        } else if (position == 0) {

        } else if (position < 0 && position >= -1) {// left scrolling
            pivotX = width;
        }
//设置x轴的锚点
        page.setPivotX(pivotX);
//设置绕Y轴旋转的角度
        page.setRotationY(90f * position);
    }

    private void setRotate(View page, float position) {

        if (position <= 0f) {
            page.setTranslationX(0f);
            page.setScaleX(1f);
            page.setScaleY(1f);
        } else if (position <= 1f) {
            final float scaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position));
            page.setAlpha(1 - position);
            page.setPivotY(0.5f * page.getHeight());
            page.setTranslationX(page.getWidth() * -position);
            page.setScaleX(scaleFactor);
            page.setScaleY(scaleFactor);
        }
        page.setRotation(180 * position);
    }

    private void setAlpha(View page, float position) {
        float scaleFactor = MIN_SCALE_ALPHA + (1 - MIN_SCALE_ALPHA) * (1 - Math.abs(position));
        float alphaFactor = MIN_ALPHA + (1 - MIN_ALPHA) * (1 - Math.abs(position));
        page.setScaleY(scaleFactor);
        page.setAlpha(alphaFactor);
    }

    private void setDegree(View page, float position) {
        int pageWidth = page.getWidth();
        if (position < -1) { // [-Infinity,-1)
// This page is way off-screen to the left.
// view.setAlpha(0);
            ViewHelper.setAlpha(page, 0);
        } else if (position <= 0)// a页滑动至b页 ； a页从 0.0 -1 ；b页从1 ~ 0.0
        { // [-1,0]
// Use the default slide transition when moving to the left page
// view.setAlpha(1);
            ViewHelper.setAlpha(page, 1);
// view.setTranslationX(0);
            ViewHelper.setTranslationX(page, 0);
// view.setScaleX(1);
            ViewHelper.setScaleX(page, 1);
// view.setScaleY(1);
            ViewHelper.setScaleY(page, 1);
        } else if (position <= 1) { // (0,1]
// Fade the page out.
// view.setAlpha(1 - position);
            ViewHelper.setAlpha(page, 1 - position);
// Counteract the default slide transition
// view.setTranslationX(pageWidth * -position);
            ViewHelper.setTranslationX(page, pageWidth * -position);
// Scale the page down (between MIN_SCALE and 1)
            float scaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 - position);
// view.setScaleX(scaleFactor);
            ViewHelper.setScaleX(page, scaleFactor);
// view.setScaleY(1);
            ViewHelper.setScaleY(page, scaleFactor);
        } else { // (1,+Infinity]
// This page is way off-screen to the right.
// view.setAlpha(0);
            ViewHelper.setAlpha(page, 1);
        }
    }

    private static final float ROT_MAX = 20.0f;
    private float mRot;

    private void pageTurning(View page, float position) {
        if (position < -1) { // [-Infinity,-1)
// This page is way off-screen to the left.
            ViewHelper.setRotation(page, 0);
        } else if (position <= 1) // a页滑动至b页 ； a页从 0.0 ~ -1 ；b页从1 ~ 0.0
        { // [-1,1]
// Modify the default slide transition to shrink the page as well
            if (position < 0) {
                mRot = (ROT_MAX * position);
                ViewHelper.setPivotX(page, page.getMeasuredWidth() * 0.5f);
                ViewHelper.setPivotY(page, page.getMeasuredHeight());
                ViewHelper.setRotation(page, mRot);
            } else {
                mRot = (ROT_MAX * position);
                ViewHelper.setPivotX(page, page.getMeasuredWidth() * 0.5f);
                ViewHelper.setPivotY(page, page.getMeasuredHeight());
                ViewHelper.setRotation(page, mRot);
            }
// Scale the page down (between MIN_SCALE and 1)
// Fade the page relative to its size.
        } else { // (1,+Infinity]
// This page is way off-screen to the right.
            ViewHelper.setRotation(page, 0);
        }
    }

    private void zoomIn(View page, float position) {
        final float scale = position < 0 ? position + 1f : Math.abs(1f - position);
        page.setScaleX(scale);
        page.setScaleY(scale);
        page.setPivotX(page.getWidth() * 0.5f);
        page.setPivotY(page.getHeight() * 0.5f);
        page.setAlpha(position < -1f || position > 1f ? 0f : 1f - (scale - 1f));
    }

    private void zoomOut(View page, float position) {
        int pageWidth = page.getWidth();
        int pageHeight = page.getHeight();

        if (position < -1) { // [-Infinity,-1)
// This page is way off-screen to the left.
            page.setAlpha(0);
        } else if (position <= 1) { // [-1,1]
// Modify the default slide transition to
// shrink the page as well
            float scaleFactor = Math.max(0.85f, 1 - Math.abs(position));
            float vertMargin = pageHeight * (1 - scaleFactor) / 2;
            float horzMargin = pageWidth * (1 - scaleFactor) / 2;
            if (position < 0) {
                page.setTranslationX(horzMargin - vertMargin / 2);
            } else {
                page.setTranslationX(-horzMargin + vertMargin / 2);
            }
// Scale the page down (between MIN_SCALE and 1)
            page.setScaleX(scaleFactor);
            page.setScaleY(scaleFactor);
// Fade the page relative to its size.
            page.setAlpha(MIN_ALPHA + (scaleFactor - 0.85f)
                    / (1 - 0.85f) * (1 - MIN_ALPHA));
        } else { // (1,+Infinity]
// This page is way off-screen to the right.
            page.setAlpha(0);
        }
    }


    private void zoomSlide(View page, float position) {
        if (position >= -1 || position <= 1) {
// Modify the default slide transition to shrink the page as well
            final float height = page.getHeight();
            final float width = page.getWidth();
            final float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
            final float vertMargin = height * (1 - scaleFactor) / 2;
            final float horzMargin = width * (1 - scaleFactor) / 2;

// Center vertically
            page.setPivotY(0.5f * height);
            page.setPivotX(0.5f * width);

            if (position < 0) {
                page.setTranslationX(horzMargin - vertMargin / 2);
            } else {
                page.setTranslationX(-horzMargin + vertMargin / 2);
            }

// Scale the page down (between MIN_SCALE and 1)
            page.setScaleX(scaleFactor);
            page.setScaleY(scaleFactor);

// Fade the page relative to its size.
            page.setAlpha(MIN_ALPHA + (scaleFactor - MIN_SCALE) / (1 - MIN_SCALE) * (1 - MIN_ALPHA));
        }
    }

    private void accordion(View page, float position) {
        page.setPivotX(position < 0 ? 0 : page.getWidth());
        page.setScaleX(position < 0 ? 1f + position : 1f - position);
    }

    private void backgroundToForeground(View page, float position) {
        final float height = page.getHeight();
        final float width = page.getWidth();
        final float scale = min(position < 0 ? 1f : Math.abs(1f - position), 0.5f);

        page.setScaleX(scale);
        page.setScaleY(scale);
        page.setPivotX(width * 0.5f);
        page.setPivotY(height * 0.5f);
        page.setTranslationX(position < 0 ? width * position : -width * position * 0.25f);
    }

    private void cubeIn(View page, float position) {
        page.setPivotX(position > 0 ? 0 : page.getWidth());
        page.setPivotY(0);
        page.setRotationY(-90f * position);
    }

    private void flipHorizontal(View page, float position) {
        final float rotation = 180f * position;
        page.setAlpha(rotation > 90f || rotation < -90f ? 0 : 1);
        page.setPivotX(page.getWidth() * 0.5f);
        page.setPivotY(page.getHeight() * 0.5f);
        page.setRotationY(rotation);
    }

    private void flipVertical(View page, float position) {
        final float rotation = -180f * position;

        page.setAlpha(rotation > 90f || rotation < -90f ? 0f : 1f);
        page.setPivotX(page.getWidth() * 0.5f);
        page.setPivotY(page.getHeight() * 0.5f);
        page.setRotationX(rotation);
    }

    private void rotateUp(View page, float position) {
        float ROT_MOD = -15f;
        final float width = page.getWidth();
        final float rotation = ROT_MOD * position;

        page.setPivotX(width * 0.5f);
        page.setPivotY(0f);
        page.setTranslationX(0f);
        page.setRotation(rotation);
    }

    private void scaleInOut(View page, float position) {
        page.setPivotX(position < 0 ? 0 : page.getWidth());
        page.setPivotY(page.getHeight() / 2f);
        float scale = position < 0 ? 1f + position : 1f - position;
        page.setScaleX(scale);
        page.setScaleY(scale);
    }

    private void stack(View page, float position) {
        page.setTranslationX(position < 0 ? 0f : -page.getWidth() * position);
    }

    private void table(View page, float position) {
        final float rotation = (position < 0 ? 30f : -30f) * Math.abs(position);

        page.setTranslationX(getOffsetXForRotation(rotation, page.getWidth(), page.getHeight()));
        page.setPivotX(page.getWidth() * 0.5f);
        page.setPivotY(0);
        page.setRotationY(rotation);
    }

    private static final Matrix OFFSET_MATRIX = new Matrix();
    private static final Camera OFFSET_CAMERA = new Camera();
    private static final float[] OFFSET_TEMP_FLOAT = new float[2];

    protected static final float getOffsetXForRotation(float degrees, int width, int height) {
        OFFSET_MATRIX.reset();
        OFFSET_CAMERA.save();
        OFFSET_CAMERA.rotateY(Math.abs(degrees));
        OFFSET_CAMERA.getMatrix(OFFSET_MATRIX);
        OFFSET_CAMERA.restore();

        OFFSET_MATRIX.preTranslate(-width * 0.5f, -height * 0.5f);
        OFFSET_MATRIX.postTranslate(width * 0.5f, height * 0.5f);
        OFFSET_TEMP_FLOAT[0] = width;
        OFFSET_TEMP_FLOAT[1] = height;
        OFFSET_MATRIX.mapPoints(OFFSET_TEMP_FLOAT);
        return (width - OFFSET_TEMP_FLOAT[0]) * (degrees > 0.0f ? 1.0f : -1.0f);
    }

}
