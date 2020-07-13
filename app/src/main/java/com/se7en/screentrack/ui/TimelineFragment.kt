package com.se7en.screentrack.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.se7en.screentrack.R
import com.se7en.screentrack.adapters.TimelineAdapter
import com.se7en.screentrack.models.App
import com.se7en.screentrack.viewmodels.TimelineViewModel
import kotlinx.android.synthetic.main.fragment_timeline.*
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter

class TimelineFragment: Fragment(R.layout.fragment_timeline) {

    private val viewModel: TimelineViewModel by activityViewModels()
    private val timelineAdapter = TimelineAdapter(::onItemClick)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val dateFormat = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy")
        todayDate.text = ZonedDateTime.now().format(dateFormat)

        timelineRecyclerView.apply {
            adapter = timelineAdapter

            layoutManager = LinearLayoutManager(context)
        }

        setupViewModelObservers()
    }

    private fun setupViewModelObservers() {
        viewModel.getSessions().observe(viewLifecycleOwner, Observer {
            timelineAdapter.submitList(it)
        })
    }

    private fun onItemClick(app: App) {
        val action = TimelineFragmentDirections.actionTimelineFragmentToAppDetailFragment(
            packageName = app.packageName,
            appName = app.appName
        )
        findNavController().navigate(action)
    }
}
