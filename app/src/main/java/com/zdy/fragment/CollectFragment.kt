package com.zdy.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zdy.fragment.collect.RepoAdapter
import com.zdy.mykotlin.R
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class CollectFragment : Fragment() {

    private val repoAdapter = RepoAdapter()


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_collect, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recycler_view.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = repoAdapter
        lifecycleScope.launch {
            viewModel.getPagingData().collect { pagingData ->
                repoAdapter.submitData(pagingData)
            }
        }
        repoAdapter.addLoadStateListener {
            when (it.refresh) {
                is LoadState.NotLoading -> {
                    recyclerView.visibility = View.VISIBLE
                }
                is LoadState.Loading -> {
                    recyclerView.visibility = View.INVISIBLE
                }
                is LoadState.Error -> {
                    val state = it.refresh as LoadState.Error
                }
            }
        }
    }

    private fun initData() {



    }


}