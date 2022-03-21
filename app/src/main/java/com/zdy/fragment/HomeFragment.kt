package com.zdy.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.zdy.mykotlin.R
import com.zdy.paging.AtomActivity
import com.zdy.paging.PagingActivity
import com.zdy.paging.RecommendActivity

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class HomeFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<View>(R.id.ll_contents).setOnClickListener(View.OnClickListener {
            startActivity(Intent(activity, PagingActivity::class.java))
        })
        view.findViewById<View>(R.id.insurance_view).setOnClickListener(View.OnClickListener {
            startActivity(Intent(activity, RecommendActivity::class.java))
        })
        view.findViewById<View>(R.id.technology_view).setOnClickListener(View.OnClickListener {
            startActivity(Intent(activity, AtomActivity::class.java))
        })

        val ivImage:ImageView = view.findViewById<View>(R.id.iv_bottom) as ImageView
        Glide.with(ivImage).load("https://ai-os-feeds-admin-static.vivo.com.cn/ai-os-feeds-admin/news/yangshi/20220308/ac3583abe6244a04b064cf16e75352c2.webp")
            .into(ivImage)

    }
}