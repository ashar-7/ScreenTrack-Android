package com.se7en.screentrack.ui

import android.app.usage.UsageStatsManager
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.tabs.TabLayout
import com.se7en.screentrack.R
import com.se7en.screentrack.Utils
import com.se7en.screentrack.adapters.AppsUsageAdapter
import com.se7en.screentrack.data.AppUsageManager
import com.se7en.screentrack.data.database.AppDatabase
import com.se7en.screentrack.viewmodels.HomeViewModel
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment: Fragment() {

    private lateinit var appUsageManager: AppUsageManager
    private val usageAdapter = AppsUsageAdapter(::onCurrentListChanged)

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
                            Room.databaseBuilder(
                                it.applicationContext,
                                AppDatabase::class.java,
                                "app-database"
                            ).build().usageDao(),
                            appUsageManager
                        ) as T
                    }
                }
            ).get(HomeViewModel::class.java)
        } ?: throw Exception("Invalid activity")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
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
            usageAdapter.submitList(usageData.usageStats)
            lastUpdated.text = Utils.getUsageTimeString(
                System.currentTimeMillis() - usageData.lastUpdated
            )
        })
    }

    fun filterList(tabPosition: Int) {
        val filter = when(tabPosition) {
            0 -> AppUsageManager.FILTER.TODAY
            1 -> AppUsageManager.FILTER.LAST_7_DAYS

            else -> throw Exception("Unknown tab position: $tabPosition")
        }
        viewModel.filterLiveData.value = filter

        println("TAB FILTER = $filter")
    }

    private fun onCurrentListChanged() {
        usageRecyclerView.scrollToPosition(0)
    }
}