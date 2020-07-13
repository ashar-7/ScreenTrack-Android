package com.se7en.screentrack.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.se7en.screentrack.R
import com.se7en.screentrack.adapters.AppsUsageAdapter
import com.se7en.screentrack.data.AppUsageManager
import com.se7en.screentrack.models.App
import com.se7en.screentrack.viewmodels.HomeViewModel
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*


class HomeFragment: Fragment() {

    private val usageAdapter = AppsUsageAdapter(::onCurrentListChanged, ::onItemClick)

    private val viewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        with(view) {
            timeFilterLayout.addTab(
                timeFilterLayout.newTab().setText(getString(R.string.today))
            )
            timeFilterLayout.addTab(
                timeFilterLayout.newTab().setText(getString(R.string.this_week))
            )

//            val pos = savedInstanceState?.getInt(getString(R.string.current_tab_key)) ?: 0
//            timeFilterLayout.selectTab(timeFilterLayout.getTabAt(pos))
        }

        return view
    }

    // TODO: fix crash
//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        outState.putInt(getString(R.string.current_tab_key), timeFilterLayout.selectedTabPosition)
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupViewModelObservers()

        usageRecyclerView.apply {
            adapter = usageAdapter
            layoutManager = LinearLayoutManager(context)

            addItemDecoration(AppsListItemDecoration())
        }

        filterList(timeFilterLayout.selectedTabPosition)
    }

    override fun onPause() {
        super.onPause()
        timeFilterLayout.clearOnTabSelectedListeners()
    }

    override fun onResume() {
        super.onResume()
        timeFilterLayout.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                filterList(tab.position)
            }

            override fun onTabReselected(tab: TabLayout.Tab) {}
            override fun onTabUnselected(tab: TabLayout.Tab) {}
        })
    }

    private fun setupViewModelObservers() {
        viewModel.usageStatsLiveData.observe(viewLifecycleOwner, Observer { usageData ->
            Log.d("HomeFragment", usageData.toString())
            usageAdapter.submitList(usageData.usageList)
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

