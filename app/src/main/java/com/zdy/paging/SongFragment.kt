package com.zdy.paging

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zdy.mykotlin.R
import com.zdy.view.swipeRefresh.OnPullRefreshListener
import com.zdy.view.swipeRefresh.ZZSwipeRefreshView
import java.util.*

/**
 * 创建日期：8/27/21 on 10:34 PM
 * 描述：
 * 作者：zhudongyong
 */
class SongFragment:Fragment() {

    private var mRecyclerView: RecyclerView? = null
    private var swipeRefreshLayout: ZZSwipeRefreshView? = null
    private val mDatas = ArrayList<String>()
    private lateinit var mAdater:SongAdater

    // Header View
    private var progressBar: ProgressBar? = null
    private var textView: TextView? = null
    private var imageView: ImageView? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_song_layout, container, false)
        initView(view)
        initAdapter()
        return view
    }

    private fun initView(view: View) {
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh)
//        swipeRefreshLayout?.setHeaderViewBackgroundColor(-0x777778)
        swipeRefreshLayout?.setHeaderView(createHeaderView()) // add headerView
        swipeRefreshLayout?.setTargetScrollWithLayout(true)
        swipeRefreshLayout?.setOnPullRefreshListener(object :
            OnPullRefreshListener {
                override fun onRefresh() {
                    textView?.text = "正在刷新"
                    imageView?.visibility = View.GONE
                    progressBar?.visibility = View.VISIBLE
                    Handler().postDelayed({
                        swipeRefreshLayout?.setRefreshing(false)
                        progressBar?.visibility = View.GONE
                    }, 2000)
                }

                override fun onPullEnable(enable: Boolean) {
                    textView?.text = (if (enable) "松开刷新" else "下拉刷新")
                    imageView?.visibility = View.VISIBLE
                    imageView?.rotation = (if (enable) 180F else 0.toFloat())
                }
            })
        mRecyclerView = view.findViewById(R.id.recyclerView)
        mRecyclerView?.layoutManager = LinearLayoutManager(context)
    }

    private fun createHeaderView():View{
        val headerView: View = LayoutInflater.from(swipeRefreshLayout!!.context)
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

    private fun initAdapter() {
        for (i in 0..31) {
            mDatas.add("夜曲" + i)
        }
        mAdater = SongAdater(context, mDatas)
        mRecyclerView?.adapter = mAdater
    }


}