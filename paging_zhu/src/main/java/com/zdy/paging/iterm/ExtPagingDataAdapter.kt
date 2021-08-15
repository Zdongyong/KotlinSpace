package com.zdy.paging.iterm

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.zdy.paging.DifferData

/**
 * 创建日期：8/15/21 on 8:18 PM
 * 描述：PagingDataAdapter扩展
 * 作者：zhudongyong
 */
abstract class ExtPagingDataAdapter<T : Any> : PagingDataAdapter<T, RecyclerView.ViewHolder> {

    constructor() : super(itemCallback())

    constructor(diffCallback: DiffUtil.ItemCallback<T>) : super(diffCallback)

    companion object {
        fun <T> itemCallback(
            areItemsTheSame: (T, T) -> Boolean = { o, n -> o == n },
            areContentsTheSame: (T, T) -> Boolean = { o, n -> o == n },
            getChangePayload: (T, T) -> Any? = { _, _ -> null }
        ): DiffUtil.ItemCallback<T> {
            return object : DiffUtil.ItemCallback<T>() {
                override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
                    return areItemsTheSame(oldItem, newItem)
                }

                override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
                    return areContentsTheSame(oldItem, newItem)
                }

                override fun getChangePayload(oldItem: T, newItem: T): Any? {
                    return getChangePayload(oldItem, newItem)
                }
            }
        }
    }

    /**
     * 保存提交的数据集
     */
    protected lateinit var mPagingData: PagingData<T>

    /**
     * 生命周期
     */
    protected lateinit var mLifecycle: Lifecycle

    //绑定时间监听
    private lateinit var mOnItemClickListener:
                (adapter: ExtPagingDataAdapter<out Any>, v: View, position: Int) -> Unit
    private lateinit var mOnItemLongClickListener:
                (adapter: ExtPagingDataAdapter<out Any>, v: View, position: Int) -> Unit
    private lateinit var mOnItemChildClickListener:
                (adapter: ExtPagingDataAdapter<out Any>, v: View, position: Int) -> Unit
    private lateinit var mOnItemChildLongClickListener:
                (adapter: ExtPagingDataAdapter<out Any>, v: View, position: Int) -> Unit
    val mOnItemChildClickListenerProxy:
                (adapter: ExtPagingDataAdapter<out Any>, v: View, position: Int) -> Unit =
        { adapter, v, position ->
            if (::mOnItemChildClickListener.isInitialized) {
                mOnItemChildClickListener(adapter, v, position)
            }
        }
    val mOnItemChildLongClickListenerProxy:
                (adapter: ExtPagingDataAdapter<out Any>, v: View, position: Int) -> Unit =
        { adapter, v, position ->
            if (::mOnItemChildLongClickListener.isInitialized) {
                mOnItemChildLongClickListener(adapter, v, position)
            }
        }

    /**
     * 刷新状态监听
     */
    private lateinit var mOnRefreshStateListener: (State) -> Unit

    /**
     * 向后加载更多状态监听
     */
    private lateinit var mOnLoadMoreStateListener: (State) -> Unit

    /**
     * 向前加载更多监听
     */
    private lateinit var mOnPrependStateListener: (State) -> Unit

    /**
     * 根据position获取布局
     */
    override fun getItemViewType(position: Int): Int {
        return getItemLayout(position)
    }

    /**
     * 给条目绑定数据
     *
     * @param helper  条目帮助类
     * @param data    对应数据
     * @param payloads item局部变更
     */
    protected abstract fun bindData(
        helper: ItemHelper,
        data: T?,
        payloads: MutableList<Any>? = null
    )

    /**
     * 创建viewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return PagingViewHolder(parent,viewType)
    }

    /**
     * 绑定viewHolder
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? ExtPagingDataAdapter<*>.PagingViewHolder)?.onBindViewHolder(position)
    }

    abstract fun getItemLayout(position: Int): Int

    fun getData(position: Int): T? {
        return getItem(position)
    }

    fun getItemObject(position: Int): T? {
        return getItem(position)
    }

    inner class PagingViewHolder internal constructor(parent: ViewGroup, layout: Int) :
        RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(layout, parent, false)
        ),
        View.OnClickListener,
        View.OnLongClickListener {
        val itemHelper: ItemHelper = ItemHelper(this)

        init {
            itemHelper.setLayoutResId(layout)
            itemHelper.setOnItemChildClickListener(mOnItemChildClickListenerProxy)
            itemHelper.setOnItemChildLongClickListener(mOnItemChildLongClickListenerProxy)
            itemHelper.setRVAdapter(this@ExtPagingDataAdapter)
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }

        val mPosition get() = bindingAdapterPosition

        fun onBindViewHolder(position: Int, payloads: MutableList<Any>? = null) {
            bindData(itemHelper, getItem(position), payloads)
        }

        override fun onClick(v: View) {
            if (::mOnItemClickListener.isInitialized) {
                mOnItemClickListener(this@ExtPagingDataAdapter, v, mPosition)
            }
        }

        override fun onLongClick(v: View): Boolean {
            if (::mOnItemLongClickListener.isInitialized) {
                mOnItemLongClickListener(this@ExtPagingDataAdapter, v, mPosition)
                return true
            }
            return false
        }
    }

    /**
     * 采用setPagingData 可以动态增减数据
     */
    open fun setPagingData(lifecycle: Lifecycle, pagingData: PagingData<T>) {
        mLifecycle = lifecycle
        mPagingData = pagingData
        submitData(lifecycle, pagingData)
    }


    fun setOnItemClickListener(
        onItemClickListener:
            (adapter: ExtPagingDataAdapter<out Any>, v: View, position: Int) -> Unit
    ) {
        mOnItemClickListener = onItemClickListener
    }

    /**
     * 刷新状态监听
     */
    fun setOnRefreshStateListener(listener: (State) -> Unit) {
        mOnRefreshStateListener = listener
    }

    /**
     * 向后加载更多状态监听
     */
    fun setOnLoadMoreStateListener(listener: (State) -> Unit) {
        mOnLoadMoreStateListener = listener
    }

    /**
     * 向前加载更多状态监听
     */
    fun setOnPrependStateListener(listener: (State) -> Unit) {
        mOnPrependStateListener = listener
    }


//    object DataComparator : DiffUtil.ItemCallback<T>() {
//
//        override fun areItemsTheSame(oldItem: DataBean, newItem: DataBean): Boolean {
//            return oldItem.id == newItem.id
//        }
//
//        @SuppressLint("DiffUtilEquals")
//        override fun areContentsTheSame(oldItem: DataBean, newItem: DataBean): Boolean {
//            return oldItem == newItem
//        }
//
//    }

}