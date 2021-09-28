package com.zdy.paging

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zdy.mykotlin.R

/**
 * 创建日期：8/27/21 on 10:36 PM
 * 描述：
 * 作者：zhudongyong
 */
class SongAdater(
    val context: Context?,
    var list:ArrayList<String>
):RecyclerView.Adapter<SongAdater.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var view:View = LayoutInflater.from(context).inflate(R.layout.item_song_laypout, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.no.text = position.toString()
        holder.name.text = list[position]
    }

    override fun getItemCount(): Int {
        return list?.size
    }

    class MyViewHolder(view:View) : RecyclerView.ViewHolder(view) {
        var no: TextView = view.findViewById(R.id.tv_item_muisc_no) as TextView
        var name: TextView = view.findViewById(R.id.tv_item_muisc_song) as TextView
    }

}