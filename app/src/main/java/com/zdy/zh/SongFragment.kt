package com.zdy.zh

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zdy.mykotlin.R
import java.util.*

/**
 * 创建日期：8/27/21 on 10:34 PM
 * 描述：
 * 作者：zhudongyong
 */
class SongFragment:Fragment() {

    private var mRecyclerView: RecyclerView? = null
    private val mDatas = ArrayList<String>()
    private lateinit var mAdater:SongAdater


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
        mRecyclerView = view.findViewById(R.id.recyclerView)
        mRecyclerView?.layoutManager = LinearLayoutManager(context)
    }

    private fun initAdapter() {
        for (i in 0..31) {
            mDatas.add("张三"+i)
        }
        mAdater = SongAdater(context,mDatas)
        mRecyclerView?.adapter = mAdater
    }


}