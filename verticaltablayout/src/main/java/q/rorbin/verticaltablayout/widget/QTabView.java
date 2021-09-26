package q.rorbin.verticaltablayout.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Px;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author chqiu
 *         Email:qstumn@163.com
 */
public class QTabView extends TabView {
    private Context mContext;
    private TextView mTitle;
    private TabIcon mTabIcon;
    private TabTitle mTabTitle;
    private boolean mChecked;
    private Drawable mDefaultBackground;


    public QTabView(Context context) {
        super(context);
        mContext = context;
        mTabIcon = new TabIcon.Builder().build();
        mTabTitle = new TabTitle.Builder().build();
        initView();
        int[] attrs;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            attrs = new int[]{android.R.attr.selectableItemBackgroundBorderless};
        } else {
            attrs = new int[]{android.R.attr.selectableItemBackground};
        }
        TypedArray a = mContext.getTheme().obtainStyledAttributes(attrs);
        mDefaultBackground = a.getDrawable(0);
        a.recycle();
        setDefaultBackground();
    }

    private void initView() {
        if (mTitle == null) {
            mTitle = new TextView(mContext);
            LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
            params.gravity = Gravity.CENTER;
            mTitle.setLayoutParams(params);
            this.addView(mTitle);
        }
        initTitleView();
        initIconView();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void setPaddingRelative(@Px int start, @Px int top, @Px int end, @Px int bottom) {
        mTitle.setPaddingRelative(start, top, end, bottom);
    }

    @Override
    public void setPadding(@Px int left, @Px int top, @Px int right, @Px int bottom) {
        mTitle.setPadding(left, top, right, bottom);
    }

    private void initTitleView() {
        mTitle.setTextColor(isChecked() ? mTabTitle.getColorSelected() : mTabTitle.getColorNormal());
        mTitle.setTextSize(mTabTitle.getTitleTextSize());
        mTitle.setText(mTabTitle.getContent());
        mTitle.setGravity(Gravity.CENTER);
        mTitle.setEllipsize(TextUtils.TruncateAt.END);
        refreshDrawablePadding();
    }

    private void initIconView() {
        int iconResid = mChecked ? mTabIcon.getSelectedIcon() : mTabIcon.getNormalIcon();
        Drawable drawable = null;
        if (iconResid != 0) {
            drawable = mContext.getResources().getDrawable(iconResid);
            int r = mTabIcon.getIconWidth() != -1 ? mTabIcon.getIconWidth() : drawable.getIntrinsicWidth();
            int b = mTabIcon.getIconHeight() != -1 ? mTabIcon.getIconHeight() : drawable.getIntrinsicHeight();
            drawable.setBounds(0, 0, r, b);
        }
        switch (mTabIcon.getIconGravity()) {
            case Gravity.START:
                mTitle.setCompoundDrawables(drawable, null, null, null);
                break;
            case Gravity.TOP:
                mTitle.setCompoundDrawables(null, drawable, null, null);
                break;
            case Gravity.END:
                mTitle.setCompoundDrawables(null, null, drawable, null);
                break;
            case Gravity.BOTTOM:
                mTitle.setCompoundDrawables(null, null, null, drawable);
                break;
        }
        refreshDrawablePadding();
    }

    private void refreshDrawablePadding() {
        int iconResid = mChecked ? mTabIcon.getSelectedIcon() : mTabIcon.getNormalIcon();
        if (iconResid != 0) {
            if (!TextUtils.isEmpty(mTabTitle.getContent()) && mTitle.getCompoundDrawablePadding() != mTabIcon.getMargin()) {
                mTitle.setCompoundDrawablePadding(mTabIcon.getMargin());
            } else if (TextUtils.isEmpty(mTabTitle.getContent())) {
                mTitle.setCompoundDrawablePadding(0);
            }
        } else {
            mTitle.setCompoundDrawablePadding(0);
        }
    }

    @Override
    public QTabView setIcon(TabIcon icon) {
        if (icon != null) {
            mTabIcon = icon;
        }
        initIconView();
        return this;
    }

    @Override
    public QTabView setTitle(TabTitle title) {
        if (title != null) {
            mTabTitle = title;
        }
        initTitleView();
        return this;
    }

    /**
     * @param resId The Drawable res to use as the background, if less than 0 will to remove the
     *              background
     */
    @Override
    public QTabView setBackground(int resId) {
        if (resId == 0) {
            setDefaultBackground();
        } else if (resId <= 0) {
            setBackground(null);
        } else {
            super.setBackgroundResource(resId);
        }
        return this;
    }

    @Override
    public TabIcon getIcon() {
        return mTabIcon;
    }

    @Override
    public TabTitle getTitle() {
        return mTabTitle;
    }

    @Override
    @Deprecated
    public ImageView getIconView() {
        return null;
    }

    @Override
    public TextView getTitleView() {
        return mTitle;
    }

    @Override
    public void setBackground(Drawable background) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            super.setBackground(background);
        } else {
            super.setBackgroundDrawable(background);
        }
    }

    @Override
    public void setBackgroundResource(int resid) {
        setBackground(resid);
    }

    private void setDefaultBackground() {
        if (getBackground() != mDefaultBackground) {
            setBackground(mDefaultBackground);
        }
    }

    @Override
    public void setChecked(boolean checked) {
        mChecked = checked;
        setSelected(checked);
        refreshDrawableState();
        mTitle.setTextColor(checked ? mTabTitle.getColorSelected() : mTabTitle.getColorNormal());
        initIconView();
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void toggle() {
        setChecked(!mChecked);
    }
}