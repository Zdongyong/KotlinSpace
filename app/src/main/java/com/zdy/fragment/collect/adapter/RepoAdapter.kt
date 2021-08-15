package com.zdy.fragment.collect.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.zdy.fragment.collect.bean.DataBean
import com.zdy.mykotlin.R
import com.zdy.mykotlin.databinding.ItemDataBinding

/**
 * 创建日期：8/10/21 on 12:23 AM
 * 描述：
 * 作者：zhudongyong
 */
class RepoAdapter(
    val itemUpdate: (Int, DataBean?, RepoAdapter) -> Unit
) : PagingDataAdapter<DataBean, RecyclerView.ViewHolder>(DataComparator) {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val dataBean = getItem(position)
        (holder as DataViewHolder).binding.demoReaData = dataBean
        holder.binding.btnUpdate.setOnClickListener {
            itemUpdate(position, dataBean, this)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: ItemDataBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_data,
                parent,
                false
            )
        return DataViewHolder(binding)
    }


    inner class DataViewHolder(private val dataBindingUtil: ItemDataBinding) :
        RecyclerView.ViewHolder(dataBindingUtil.root) {
        var binding = dataBindingUtil
    }


    object DataComparator : DiffUtil.ItemCallback<DataBean>() {

        override fun areItemsTheSame(oldItem: DataBean, newItem: DataBean): Boolean {
            return oldItem.id == newItem.id
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: DataBean, newItem: DataBean): Boolean {
            return oldItem == newItem
        }

    }

}