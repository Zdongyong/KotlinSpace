package com.zdy.paging

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import com.zdy.mykotlin.R

/**
 * 创建日期：8/27/21 on 10:29 PM
 * 描述：
 * 作者：zhudongyong
 */
class TabFragment:Fragment() {

    private var name:String?=null

    fun newInstance(name: String?): Fragment? {
        val args = Bundle()
        args.putString("data", name)
        val fragment = TabFragment()
        fragment.arguments = args
        return fragment
    }

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            name = requireArguments().getString("data")
        }
    }

    @Nullable
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_text_layout, container, false)
        val textView = view.findViewById<TextView>(R.id.tv_fragment_tab_text)
        textView.setText(name)
        return view
    }

}