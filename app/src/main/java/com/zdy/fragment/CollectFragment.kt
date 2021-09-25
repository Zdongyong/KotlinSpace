package com.zdy.fragment

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.zdy.fragment.collect.CollectViewModel
import com.zdy.fragment.collect.adapter.FootAdapter
import com.zdy.fragment.collect.adapter.RepoAdapter
import com.zdy.mykotlin.R
import com.zdy.view.OnPullRefreshListener
import com.zdy.view.OnPushLoadMoreListener
import kotlinx.android.synthetic.main.fragment_collect.*
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.IOException

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class CollectFragment : Fragment(),OnPullRefreshListener,OnPushLoadMoreListener{

    companion object{
        val  TAG : String = "CollectFragment"
    }

    private val repoAdapter = RepoAdapter{ position, it, adapter ->
        it?.chapterName = "黄林晴${position}"
        adapter.notifyDataSetChanged()
    }

    // Header View
    private var progressBar: ProgressBar? = null
    private var textView: TextView? = null
    private var imageView: ImageView? = null

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
        recycler_view.adapter  =
            repoAdapter.withLoadStateFooter(
                footer = FootAdapter(retry = { repoAdapter.retry() })
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
        repoAdapter.refresh()

    }

    private fun initView() {
        swipe_refresh?.setHeaderView(createHeaderView()) // add headerView
        swipe_refresh?.setFooterView(createFooterView())
        swipe_refresh.setTargetScrollWithLayout(true)
        swipe_refresh.setHeaderViewBackgroundColor(resources.getColor(R.color.white))
        swipe_refresh?.setOnPullRefreshListener(this)
        swipe_refresh?.setOnPushLoadMoreListener(this)
    }

    private fun createHeaderView():View{
        val headerView: View = LayoutInflater.from(swipe_refresh!!.context)
            .inflate(R.layout.layout_head, null)
        progressBar = headerView.findViewById<View>(R.id.pb_view) as ProgressBar
        textView = headerView.findViewById<View>(R.id.text_view) as TextView
        textView?.text = ("下拉刷新")
        imageView = headerView.findViewById<View>(R.id.image_view) as ImageView
        imageView?.visibility = View.VISIBLE
        imageView?.setImageResource(R.drawable.down_arrow)
        progressBar?.visibility = View.GONE
        return headerView
    }

    private fun createFooterView():View{
        val footerView: View = LayoutInflater.from(swipe_refresh!!.context)
            .inflate(R.layout.item_footer, null)
        return footerView
    }

    override fun onRefresh() {
        textView?.text = ("正在刷新")
        imageView?.visibility = (View.GONE)
        progressBar?.visibility = (View.VISIBLE)
        Handler().postDelayed({
            repoAdapter.refresh()
            swipe_refresh.setRefreshing(false)
            progressBar?.visibility = View.GONE
        }, 1000)
    }

    override fun onPullEnable(enable: Boolean) {
        imageView?.rotation = (if (enable) 180F else 0.toFloat())
        textView?.text = (if (enable) "松开刷新" else "下拉刷新")
        imageView?.visibility = View.VISIBLE
    }

    override fun onLoadMore() {
    }

    override fun onPushEnable(enable: Boolean) {
    }


}