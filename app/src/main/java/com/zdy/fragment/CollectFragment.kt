package com.zdy.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.zdy.fragment.collect.CollectViewModel
import com.zdy.fragment.collect.adapter.FootAdapter
import com.zdy.fragment.collect.adapter.RepoAdapter
import com.zdy.mykotlin.R
import kotlinx.android.synthetic.main.fragment_collect.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.IOException

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class CollectFragment : Fragment() {

    companion object{
        val  TAG : String = "CollectFragment"
    }

    private val repoAdapter = RepoAdapter{ position, it, adapter ->
        it?.chapterName = "黄林晴${position}"
        adapter.notifyDataSetChanged()
    }

    private val collectViewModel: CollectViewModel by viewModels()

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_collect, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        recycler_view.layoutManager = LinearLayoutManager(activity)
//        recycler_view.adapter = repoAdapter

        recycler_view.adapter  =
            repoAdapter.withLoadStateFooter(
                footer = FootAdapter(retry = {repoAdapter.retry()})
            )

        lifecycleScope.launch {
            //观察 PagingData 流
            collectViewModel.getData().collectLatest { pagingData ->
                repoAdapter.submitData(pagingData)
            }
        }

        //初始状态添加监听
        repoAdapter.addLoadStateListener {
            when (it.refresh) {
                is LoadState.NotLoading -> {
                    Log.d(TAG, "is NotLoading")
                }
                is LoadState.Loading -> {
                    Log.d(TAG, "is Loading")
                }
                is LoadState.Error -> {
                    Log.d(TAG, "is Error:")
                        repoAdapter.retry()
                    when ((it.refresh as LoadState.Error).error) {
                        is IOException -> {
                            Log.d(TAG, "IOException")
                        }
                        else -> {
                            Log.d(TAG, "others exception")
                        }
                    }
                }
            }
        }

    }

    private fun initView() {
        val  view = layoutInflater.inflate(R.layout.item_header,null)
        swipe_refresh.setHeaderView(view)
//        swipe_refresh.setRefreshing(true)
    }


}