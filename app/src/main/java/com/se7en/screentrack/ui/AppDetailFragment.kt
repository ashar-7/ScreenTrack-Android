package com.se7en.screentrack.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.se7en.screentrack.R
import com.se7en.screentrack.Utils
import com.se7en.screentrack.adapters.SessionsAdapter
import com.se7en.screentrack.models.App
import com.se7en.screentrack.models.DayStats
import com.se7en.screentrack.viewmodels.AppDetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_app_detail.*
import org.threeten.bp.format.TextStyle
import org.threeten.bp.temporal.ChronoUnit
import java.util.*


@AndroidEntryPoint
class AppDetailFragment: Fragment(R.layout.fragment_app_detail) {

    private val viewModel: AppDetailViewModel by viewModels()
    private val args by navArgs<AppDetailFragmentArgs>()
    private val sessionsAdapter = SessionsAdapter(::listChangedListener)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupViewModelObservers()

        sessionRecyclerView.apply {
            adapter = sessionsAdapter
            layoutManager = LinearLayoutManager(context)

            addItemDecoration(AppsListItemDecoration())
        }

        val app = App.fromContext(requireContext(), args.packageName)
        appName.text = app.appName
        appIcon.setImageDrawable(app.iconDrawable)
    }

    private fun setupViewModelObservers() {
        viewModel.getDayStats(args.packageName).observe(viewLifecycleOwner, Observer {
            val today = Utils.getZonedDateTime(System.currentTimeMillis(), ChronoUnit.DAYS)
            var lastUsedTime = 0L
            var todayTotal = 0L
            var weekTotal = 0L
            val entries = arrayListOf<BarEntry>()
            val labels = arrayListOf<String>()
            it.forEachIndexed { index, stats ->
                val entry = BarEntry(index.toFloat(), stats.totalTime.toFloat())
                val label = stats.dayId.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                entries.add(entry)
                labels.add(label)

                if(stats.dayId.truncatedTo(ChronoUnit.DAYS).isEqual(today)) {
                    todayTotal = stats.totalTime
                }

                weekTotal += stats.totalTime
                if(stats.lastUsed > lastUsedTime) lastUsedTime = stats.lastUsed
            }

            setupChart(entries, labels, it)

            lastUsed.text = getString(R.string.last_used_template, Utils.getLastUsedFormattedDate(
                lastUsedTime
            ))
            usedToday.text = Utils.getUsageTimeString(todayTotal)
            usedThisWeek.text = Utils.getUsageTimeString(weekTotal)
            average.text = Utils.getUsageTimeString(weekTotal / 7)

            Log.d("AppDetailFragment", it.toString())
        })

        viewModel.sessionsLiveData.observe(viewLifecycleOwner, Observer {
            Log.d("AppDetailFragment", it.toString())

            sessionsAdapter.submitList(it)
        })
    }

    private fun setupChart(
        entries: List<BarEntry>,
        labels: List<String>,
        dayStats: List<DayStats>
    ) {
        val datasetValueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return Utils.getUsageTimeString(value.toLong())
            }
        }

        val xAxisValueFormatter = object: ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return labels[value.toInt()]
            }
        }

        val textColor = ContextCompat.getColor(requireContext(), R.color.material_on_surface_emphasis_high_type)
        val barDataSet = BarDataSet(entries, "Usage")
        barDataSet.valueFormatter = datasetValueFormatter
        barDataSet.highLightColor = ContextCompat.getColor(requireContext(), R.color.colorAccent)
        barDataSet.highLightAlpha = 255
        barDataSet.valueTextColor = textColor
        barDataSet.color = ContextCompat.getColor(requireContext(), R.color.colorPrimary)

        barChart.xAxis.valueFormatter = xAxisValueFormatter
        barChart.xAxis.granularity = 1f
        barChart.xAxis.textColor = textColor
        barChart.legend.textColor = textColor
        barChart.description.isEnabled = false
        barChart.isDoubleTapToZoomEnabled = false
        barChart.axisLeft.isEnabled = false
        barChart.axisRight.isEnabled = false

        barChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onNothingSelected() {
                sessionsAdapter.submitList(listOf())
                Log.d("AppDetailFragment", "nothing selected")
            }

            override fun onValueSelected(e: Entry?, h: Highlight?) {
                Log.d("AppDetailFragment", "something selected ${e?.x}")
                e?.let {
                    viewModel.fetchSessions(args.packageName, dayStats[it.x.toInt()].dayId)
                }
            }
        })
        barChart.data = BarData(barDataSet)
        barChart.setDrawGridBackground(false)
        barChart.invalidate()
    }

    private fun listChangedListener(isEmpty: Boolean) {
        selectBarTextView.visibility = if(isEmpty) View.VISIBLE else View.GONE
    }
}
