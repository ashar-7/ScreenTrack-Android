package com.se7en.screentrack.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.se7en.screentrack.Constants
import com.se7en.screentrack.data.AppUsageManager
import com.se7en.screentrack.ui.UsageListFragment

class UsageFragmentAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = AppUsageManager.FILTER.values().size

    override fun createFragment(position: Int): Fragment {
        // Return a NEW fragment instance in createFragment(int)
        val fragment = UsageListFragment()
        fragment.arguments = Bundle().apply {
            putInt(Constants.FILTER_KEY, position)
        }
        return fragment
    }
}
