package com.se7en.screentrack.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.se7en.screentrack.R
import com.se7en.screentrack.adapters.TimeLimitListAdapter
import com.se7en.screentrack.models.App
import com.se7en.screentrack.viewmodels.TimeLimitListViewModel
import kotlinx.android.synthetic.main.time_limit_list_fragment.*

class TimeLimitListFragment : Fragment(R.layout.time_limit_list_fragment) {

    private val viewModel: TimeLimitListViewModel by activityViewModels()
    private val timeLimitListAdapter = TimeLimitListAdapter(::onItemClick)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        timeLimitListRecyclerView.apply {
            adapter = timeLimitListAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(AppsListItemDecoration())
        }


        viewModel.timeLimitList.observe(viewLifecycleOwner) { timeLimitList ->
            timeLimitListAdapter.submitList(timeLimitList)
        }
    }

    private fun onItemClick(app: App) {
        val action = TimeLimitListFragmentDirections.actionTimeLimitListFragmentToAppDetailFragment(
            packageName = app.packageName,
            appName = app.appName
        )
        findNavController().navigate(action)
    }
}
