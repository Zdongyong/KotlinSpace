package com.zdy.fragment.collect.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.zdy.mykotlin.R

/**
 * 创建日期：8/15/21 on 6:42 PM
 * 描述：
 * 作者：zhudongyong
 */
class FootAdapter(
    parent: ViewGroup, var retry: () -> Void
):LoadStateAdapter<FootAdapter.FooterHolder>() {

    override fun onBindViewHolder(holder: FootAdapter.FooterHolder, loadState: LoadState) {
        if (loadState.endOfPaginationReached) {
            holder.showNoMore()
        } else {
            holder.showLoading()
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): FootAdapter.FooterHolder {
        return FooterHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_footer, parent, false))
    }

    class FooterHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val pbLoading by lazy {
            itemView.findViewById<ProgressBar>(R.id.pb_loading)
        }
        private val tvLoading by lazy {
            itemView.findViewById<TextView>(R.id.tv_loading)
        }

        fun showLoading() {
            pbLoading.visibility = View.VISIBLE
            tvLoading.text = "加载中。。。"
        }

        fun showNoMore() {
            pbLoading.visibility = View.GONE
            tvLoading.text = "人家也是有底线的"
        }
    }
}