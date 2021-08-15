package com.zdy.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.zdy.fragment.collect.CollectViewModel
import com.zdy.fragment.record.viewHolder.RecordViewHolder
import com.zdy.fragment.record.viewModel.RecordViewModel
import com.zdy.mykotlin.R
import com.zdy.paging.BaseAdapter
import com.zdy.paging.iterm.State
import kotlinx.android.synthetic.main.fragment_record.*
import kotlinx.coroutines.flow.collectLatest

/**
 * 创建日期：8/15/21 on 7:20 PM
 * 描述：
 * 作者：zhudongyong
 */
class RecordFragment: Fragment() {
    
    private val recordAdapter by lazy { BaseAdapter(RecordViewHolder()) }

    private val model: RecordViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_record, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }


    fun initView(){
        rv_record.layoutManager = LinearLayoutManager(activity)
        rv_record.adapter = recordAdapter
        //点击事件
//        recordAdapter.setOnItemClickListener {
//        }
        //设置下拉刷新
        refresh_layout.setOnRefreshListener {
            recordAdapter.refresh()
        }
        //上拉加载更多
//        refresh_layout.setOnLoadMoreListener {
//            recordAdapter.retry()
//        }
        //绑定数据源
        lifecycleScope.launchWhenCreated {
            model.data.collectLatest {
                recordAdapter.setData(this@RecordFragment.lifecycle,it)
            }
        }
        //请求状态
        recordAdapter.setOnRefreshStateListener {
            when (it) {
                is State.Loading -> {
                    //如果是手动下拉刷新，则不展示loading页
//                    if (refresh_layout.state != RefreshState.Refreshing) {
//                        statePager.showLoading()
//                    }
//                    refresh_layout.resetNoMoreData()
                }
                is State.Success -> {
//                    statePager.showContent()
//                    refresh_layout.finishRefresh(true)
//                    refresh_layout.setNoMoreData(it.noMoreData)
                }
                is State.Error -> {
//                    statePager.showError()
//                    refresh_layout.finishRefresh(false)
                }
            }
        }
        //加载更多状态
        recordAdapter.setOnLoadMoreStateListener {
            when (it) {
                is State.Loading -> {
                    //重置上拉加载状态，显示加载loading
//                    refresh_layout.resetNoMoreData()
                }
                is State.Success -> {
                    if (it.noMoreData) {
                        //没有更多了(只能用source的append)
//                        refresh_layout.finishLoadMoreWithNoMoreData()
                    } else {
//                        refresh_layout.finishLoadMore(true)
                    }
                }
                is State.Error -> {
//                    refresh_layout.finishLoadMore(false)
                }
            }
        }
    }

}