package com.se7en.screentrack.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.se7en.screentrack.Constants
import com.se7en.screentrack.R
import com.se7en.screentrack.adapters.AppsUsageAdapter
import com.se7en.screentrack.data.AppUsageManager
import com.se7en.screentrack.models.App
import com.se7en.screentrack.viewmodels.HomeViewModel
import kotlinx.android.synthetic.main.fragment_usage_list.*

class UsageListFragment: Fragment(R.layout.fragment_usage_list) {

    private val usageAdapter = AppsUsageAdapter(::onCurrentListChanged, ::onItemClick)
    private val viewModel: HomeViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupViewModelObservers()

        usageRecyclerView.apply {
            adapter = usageAdapter
            layoutManager = LinearLayoutManager(context)

            addItemDecoration(AppsListItemDecoration())
        }

    }

    private fun setupViewModelObservers() {
        val filter = when(arguments?.getInt(Constants.FILTER_KEY)) {
            0 -> AppUsageManager.FILTER.TODAY
            1 -> AppUsageManager.FILTER.THIS_WEEK

            else -> AppUsageManager.FILTER.TODAY
        }

        viewModel.getUsageLiveData(filter).observe(viewLifecycleOwner, Observer { usageData ->
            Log.d("UsageListFragment", usageData.toString())

            usageAdapter.submitList(usageData.usageList)
        })
    }

    private fun onCurrentListChanged() {
        usageRecyclerView.scrollToPosition(0)
    }

    private fun onItemClick(app: App) {
        val action = HomeFragmentDirections.actionHomeFragmentToAppDetailFragment(
            packageName = app.packageName,
            appName = app.appName
        )
        findNavController().navigate(action)
    }
}
