package com.zdy.paging.iterm

import android.content.Context
import android.util.SparseArray
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.*
import androidx.core.app.ActivityCompat

/**
 * 创建日期：8/15/21 on 8:54 PM
 * 描述：
 * 作者：zhudongyong
 */
class ItemHelper (
    private val viewHolder: ExtPagingDataAdapter<*>.PagingViewHolder
) : View.OnClickListener, View.OnLongClickListener {
    private val viewCache = SparseArray<View>()
    private val clickListenerCache = ArrayList<Int>()
    private val longClickListenerCache = ArrayList<Int>()
    private val mTags = HashMap<String, Any>()
    lateinit var adapter: ExtPagingDataAdapter<out Any>

    @LayoutRes
    @get:LayoutRes
    var itemLayoutResId: Int = 0
    val position get() = viewHolder.bindingAdapterPosition
    val itemView: View = viewHolder.itemView
    val context: Context = itemView.context
    var tag: Any? = null

    private lateinit var mOnItemChildClickListener:
                (adapter: ExtPagingDataAdapter<out Any>, v: View, position: Int) -> Unit

    private lateinit var mOnItemChildLongClickListener:
                (adapter: ExtPagingDataAdapter<out Any>, v: View, position: Int) -> Unit

    fun setLayoutResId(@LayoutRes layoutResId: Int) {
        this.itemLayoutResId = layoutResId
    }

    fun setOnItemChildClickListener(
        onItemChildClickListener:
            (adapter: ExtPagingDataAdapter<out Any>, v: View, position: Int) -> Unit
    ) {
        mOnItemChildClickListener = onItemChildClickListener
    }

    fun setOnItemChildLongClickListener(
        onItemChildLongClickListener:
            (adapter: ExtPagingDataAdapter<out Any>, v: View, position: Int) -> Unit
    ) {
        mOnItemChildLongClickListener = onItemChildLongClickListener
    }

    fun setRVAdapter(pagedListAdapter: ExtPagingDataAdapter<out Any>) {
        adapter = pagedListAdapter
    }

    fun setTag(key: String, tag: Any) {
        mTags[key] = tag
    }

    fun getTag(key: String): Any? {
        return mTags[key]
    }

    @Suppress("UNCHECKED_CAST")
    fun <V : View> findViewById(@IdRes viewId: Int): V {
        val v = viewCache.get(viewId)
        val view: V?
        if (v == null) {
            view = itemView.findViewById(viewId)
            if (view == null) {
                val entryName = itemView.resources.getResourceEntryName(viewId)
                throw NullPointerException("id: R.id.$entryName can not find in this item!")
            }
            viewCache.put(viewId, view)
        } else {
            view = v as V
        }
        return view
    }

    fun <V : View> getViewById(@IdRes viewId: Int, call: (V) -> Unit = {}): ItemHelper {
        val view = findViewById<V>(viewId)
        call(view)
        return this
    }

    /**
     * 给按钮或文本框设置文字
     *
     * @param viewId 控件id
     * @param text   设置的文字
     */
    fun setText(@IdRes viewId: Int, text: CharSequence?): ItemHelper {
        getViewById<View>(viewId) {
            if (it is TextView) {
                it.text = text
            } else {
                val entryName = it.resources.getResourceEntryName(viewId)
                throw ClassCastException("id: R.id.$entryName are not TextView")
            }
        }
        return this
    }

    /**
     * 给按钮或文本框设置文字
     *
     * @param viewId 控件id
     * @param resId  设置的文字资源
     */
    fun setText(@IdRes viewId: Int, @StringRes resId: Int): ItemHelper {
        getViewById<View>(viewId) {
            if (it is TextView) {
                it.text = try {
                    it.resources.getString(resId)
                } catch (e: Exception) {
                    resId.toString()
                }
            } else {
                val entryName = it.resources.getResourceEntryName(viewId)
                throw ClassCastException("id: R.id.$entryName are not TextView")
            }
        }
        return this
    }

    /**
     * 设置文本颜色
     *
     * @param viewId 要设置文本的控件，TextView及其子类都可以
     * @param color  颜色int值，不是资源Id
     */
    fun setTextColor(@IdRes viewId: Int, @ColorInt color: Int): ItemHelper {
        getViewById<View>(viewId) {
            if (it is TextView) {
                it.setTextColor(color)
            } else {
                val entryName = it.resources.getResourceEntryName(viewId)
                throw ClassCastException("id: R.id.$entryName are not TextView")
            }
        }
        return this
    }

    /**
     * 设置文本颜色
     *
     * @param viewId     要设置文本的控件，TextView及其子类都可以
     * @param colorResId 颜色资源Id
     */
    fun setTextColorRes(@IdRes viewId: Int, @ColorRes colorResId: Int): ItemHelper {
        getViewById<View>(viewId) {
            if (it is TextView) {
                it.setTextColor(ActivityCompat.getColor(context, colorResId))
            } else {
                val entryName = it.resources.getResourceEntryName(viewId)
                throw ClassCastException("id: R.id.$entryName are not TextView")
            }
        }
        return this
    }

    /**
     * 给图片控件设置资源图片
     *
     * @param viewId 图片控件id
     * @param resId  资源id
     */
    fun setImageResource(@IdRes viewId: Int, @DrawableRes resId: Int): ItemHelper {
        getViewById<View>(viewId) {
            if (it is ImageView) {
                it.setImageResource(resId)
            } else {
                val entryName = it.resources.getResourceEntryName(viewId)
                throw ClassCastException("id: R.id.$entryName are not ImageView")
            }
        }
        return this
    }

    /**
     * 设置view的背景
     *
     * @param viewId 控件id
     * @param resId  资源id
     */
    fun setBackgroundResource(@IdRes viewId: Int, @DrawableRes resId: Int): ItemHelper {
        getViewById<View>(viewId) {
            it.setBackgroundResource(resId)
        }
        return this
    }

    fun setVisibility(@IdRes viewId: Int, visibility: Int): ItemHelper {
        getViewById<View>(viewId) {
            it.visibility = visibility
        }
        return this
    }

    fun setVisibleOrGone(@IdRes viewId: Int, visibility: () -> Boolean): ItemHelper {
        getViewById<View>(viewId) {
            it.visibility = if (visibility()) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
        return this
    }

    fun setVisibleOrInVisible(@IdRes viewId: Int, visibility: () -> Boolean): ItemHelper {
        getViewById<View>(viewId) {
            it.visibility = if (visibility()) {
                View.VISIBLE
            } else {
                View.INVISIBLE
            }
        }
        return this
    }

    fun setViewVisible(@IdRes vararg viewId: Int): ItemHelper {
        for (id in viewId) {
            getViewById<View>(id) {
                it.visibility = View.VISIBLE
            }
        }
        return this
    }

    fun setViewInvisible(@IdRes vararg viewId: Int): ItemHelper {
        for (id in viewId) {
            getViewById<View>(id) {
                it.visibility = View.INVISIBLE
            }
        }
        return this
    }

    fun setViewGone(@IdRes vararg viewId: Int): ItemHelper {
        for (id in viewId) {
            getViewById<View>(id) {
                it.visibility = View.GONE
            }
        }
        return this
    }

    /**
     * 给条目中的view添加点击事件
     *
     * @param viewId 控件id
     */
    fun addOnClickListener(@IdRes viewId: Int): ItemHelper {
        val contains = clickListenerCache.contains(viewId)
        if (!contains) {
            getViewById<View>(viewId) { it.setOnClickListener(this) }
            clickListenerCache.add(viewId)
        }
        return this
    }

    /**
     * 给条目中的view添加长按事件
     *
     * @param viewId 控件id
     */
    fun addOnLongClickListener(@IdRes viewId: Int): ItemHelper {
        val contains = longClickListenerCache.contains(viewId)
        if (!contains) {
            getViewById<View>(viewId) { it.setOnLongClickListener(this) }
            longClickListenerCache.add(viewId)
        }
        return this
    }

    override fun onClick(v: View) {
        if (::mOnItemChildClickListener.isInitialized) {
            mOnItemChildClickListener(adapter, v, position)
        }
    }


    override fun onLongClick(v: View): Boolean {
        if (::mOnItemChildLongClickListener.isInitialized) {
            mOnItemChildLongClickListener(adapter, v, position)
            return true
        }
        return false
    }

    var mItemHolder: ItemHolder<Any>? = null

    @Suppress("UNCHECKED_CAST")
    fun setItemHolder(itemHolderClass: Class<out ItemHolder<out Any>>): ItemHolder<Any>? {
        try {
            if (mItemHolder == null) {
                val newInstance = itemHolderClass.newInstance()
                mItemHolder = newInstance as ItemHolder<Any>?
                mItemHolder?.initView(this, adapter.getItemObject(position))
            }
            mItemHolder?.bindData(this, adapter.getItemObject(position))
        } catch (e: InstantiationException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
        return mItemHolder
    }

    fun setItemHolder(itemHolder: ItemHolder<*>) {
        if (mItemHolder == null) {
            mItemHolder = itemHolder as? ItemHolder<Any>
            mItemHolder?.initView(this, adapter.getItemObject(position))
        }
        mItemHolder?.bindData(this, adapter.getItemObject(position))
    }
}