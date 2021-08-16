package com.zdy.fragment.collect.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.zdy.mykotlin.R

/**
 * 创建日期：8/15/21 on 6:42 PM
 * 描述：
 * 作者：zhudongyong
 */
class HeaderAdapter(
    private val refresh: () -> Unit
):LoadStateAdapter<HeaderAdapter.HooterHolder>() {

    override fun onBindViewHolder(holder: HooterHolder, loadState: LoadState) {
            holder.showLoading()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): HeaderAdapter.HooterHolder {
        return HooterHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_header, parent, false))
    }

    class HooterHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val pbLoading by lazy {
            itemView.findViewById<ProgressBar>(R.id.header_icon)
        }
        private val tvLoading by lazy {
            itemView.findViewById<TextView>(R.id.header_loading)
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