package com.se7en.screentrack.ui

import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.se7en.screentrack.R
import com.se7en.screentrack.adapters.AppsUsageAdapter
import com.se7en.screentrack.data.AppUsageManager
import com.se7en.screentrack.data.database.AppDatabase
import com.se7en.screentrack.models.App
import com.se7en.screentrack.viewmodels.HomeViewModel
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment: Fragment(R.layout.fragment_home) {

    private lateinit var appUsageManager: AppUsageManager
    private val usageAdapter = AppsUsageAdapter(::onCurrentListChanged, ::onItemClick)

    private lateinit var viewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.let {
            val usageStatsManager =
                it.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager?

            appUsageManager =
                AppUsageManager(it, usageStatsManager)

            viewModel = ViewModelProvider(
                it,
                object: ViewModelProvider.Factory {
                    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                        return HomeViewModel(
                            AppDatabase.getInstance(it.applicationContext),
                            appUsageManager
                        ) as T
                    }
                }
            ).get(HomeViewModel::class.java)
        } ?: throw Exception("Invalid activity")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupViewModelObservers()

        usageRecyclerView.apply {
            adapter = usageAdapter
            layoutManager = LinearLayoutManager(context)

            addItemDecoration(AppsListItemDecoration())
        }

        timeFilterLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                filterList(tab.position)
            }

            override fun onTabReselected(tab: TabLayout.Tab) {}
            override fun onTabUnselected(tab: TabLayout.Tab) {}
        })

        filterList(timeFilterLayout.selectedTabPosition)
    }

    private fun setupViewModelObservers() {
        viewModel.usageStatsLiveData.observe(viewLifecycleOwner, Observer { usageData ->
            Log.d("HomeFragment", usageData.toString())
            usageAdapter.submitList(usageData.usageList)
//            lastUpdated.text = Utils.getUsageTimeString(
//                System.currentTimeMillis() - usageData.totalStats[0]
//            )
        })
    }

    fun filterList(tabPosition: Int) {
        val filter = when(tabPosition) {
            0 -> AppUsageManager.FILTER.TODAY
            1 -> AppUsageManager.FILTER.THIS_WEEK

            else -> throw Exception("Unknown tab position: $tabPosition")
        }
        viewModel.filterLiveData.value = filter

        println("TAB FILTER = $filter")
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
