package com.se7en.screentrack.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.se7en.screentrack.R
import com.se7en.screentrack.adapters.UsageFragmentAdapter
import com.se7en.screentrack.data.AppUsageManager
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment: Fragment(R.layout.fragment_home) {

    private lateinit var usageFragmentAdapter: UsageFragmentAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        usageFragmentAdapter = UsageFragmentAdapter(this)
        pager.adapter = usageFragmentAdapter
        TabLayoutMediator(timeFilterLayout, pager) { tab, position ->
            when(AppUsageManager.FILTER.values()[position]) {
                AppUsageManager.FILTER.TODAY -> {
                    tab.text = getString(R.string.today)
                }

                AppUsageManager.FILTER.THIS_WEEK -> {
                    tab.text = getString(R.string.this_week)
                }
            }
        }.attach()
    }
}

