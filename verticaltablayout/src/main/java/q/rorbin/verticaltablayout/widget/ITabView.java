package q.rorbin.verticaltablayout.widget;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;


/**
 * @author chqiu
 *         Email:qstumn@163.com
 */

public interface ITabView {


    ITabView setIcon(TabIcon icon);

    ITabView setTitle(TabTitle title);

    ITabView setBackground(int resid);

    TabIcon getIcon();

    TabTitle getTitle();

    View getTabView();

    class TabIcon {

        private Builder mBuilder;

        private TabIcon(Builder mBuilder) {
            this.mBuilder = mBuilder;
        }

        public int getSelectedIcon() {
            return mBuilder.mSelectedIcon;
        }

        public int getNormalIcon() {
            return mBuilder.mNormalIcon;
        }

        public int getIconGravity() {
            return mBuilder.mIconGravity;
        }

        public int getIconWidth() {
            return mBuilder.mIconWidth;
        }

        public int getIconHeight() {
            return mBuilder.mIconHeight;
        }

        public int getMargin() {
            return mBuilder.mMargin;
        }

        public static class Builder {
            private int mSelectedIcon;
            private int mNormalIcon;
            private int mIconGravity;
            private int mIconWidth;
            private int mIconHeight;
            private int mMargin;

            public Builder() {
                mSelectedIcon = 0;
                mNormalIcon = 0;
                mIconWidth = -1;
                mIconHeight = -1;
                mIconGravity = Gravity.START;
                mMargin = 0;
            }

            public Builder setIcon(int selectIconResId, int normalIconResId) {
                mSelectedIcon = selectIconResId;
                mNormalIcon = normalIconResId;
                return this;
            }

            public Builder setIconSize(int width, int height) {
                mIconWidth = width;
                mIconHeight = height;
                return this;
            }

            public Builder setIconGravity(int gravity) {
                if (gravity != Gravity.START && gravity != Gravity.END
                        & gravity != Gravity.TOP & gravity != Gravity.BOTTOM) {
                    throw new IllegalStateException("iconGravity only support Gravity.START " +
                            "or Gravity.END or Gravity.TOP or Gravity.BOTTOM");
                }
                mIconGravity = gravity;
                return this;
            }

            public Builder setIconMargin(int margin) {
                mMargin = margin;
                return this;
            }

            public TabIcon build() {
                return new TabIcon(this);
            }
        }
    }

    class TabTitle {
        private Builder mBuilder;

        private TabTitle(Builder mBuilder) {
            this.mBuilder = mBuilder;
        }

        public int getColorSelected() {
            return mBuilder.mColorSelected;
        }

        public int getColorNormal() {
            return mBuilder.mColorNormal;
        }

        public int getTitleTextSize() {
            return mBuilder.mTitleTextSize;
        }

        public String getContent() {
            return mBuilder.mContent;
        }

        public static class Builder {
            private int mColorSelected;
            private int mColorNormal;
            private int mTitleTextSize;
            private String mContent;

            public Builder() {
                this.mColorSelected = 0xFFFF4081;
                this.mColorNormal = 0xFF757575;
                this.mTitleTextSize = 16;
                this.mContent = "";
            }

            public Builder setTextColor(int colorSelected, int colorNormal) {
                mColorSelected = colorSelected;
                mColorNormal = colorNormal;
                return this;
            }

            public Builder setTextSize(int sizeSp) {
                mTitleTextSize = sizeSp;
                return this;
            }

            public Builder setContent(String content) {
                mContent = content;
                return this;
            }

            public TabTitle build() {
                return new TabTitle(this);
            }
        }
    }


}
