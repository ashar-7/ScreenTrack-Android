package com.se7en.screentrack.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.se7en.screentrack.R
import com.se7en.screentrack.Utils
import com.se7en.screentrack.data.database.AppDatabase
import com.se7en.screentrack.models.App
import com.se7en.screentrack.viewmodels.AppDetailViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_app_detail.*
import org.threeten.bp.format.TextStyle
import org.threeten.bp.temporal.ChronoUnit
import java.util.*

class AppDetailFragment: Fragment(R.layout.fragment_app_detail) {

    private val viewModel by viewModels<AppDetailViewModel> {
        object: ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return AppDetailViewModel(AppDatabase.getInstance(requireContext())) as T
            }
        }
    }
    private val args by navArgs<AppDetailFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as MainActivity).toolbar_title.text = args.appName
        setupViewModelObservers()

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
            var todayEntry: BarEntry? = null
            val entries = arrayListOf<BarEntry>()
            val labels = arrayListOf<String>()
            it.forEachIndexed { index, stats ->
                val entry = BarEntry(index.toFloat(), stats.totalTime.toFloat())
                val label = stats.dayId.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())

                if(stats.dayId.truncatedTo(ChronoUnit.DAYS).isEqual(today)) {
                    todayEntry = entry
                    todayTotal = stats.totalTime
                    label.plus(" (Today)")
                }
                entries.add(entry)
                labels.add(label)

                weekTotal += stats.totalTime
                if(stats.lastUsed > lastUsedTime) lastUsedTime = stats.lastUsed
            }

            setupChart(entries, labels, todayEntry)

            lastUsed.text = getString(R.string.last_used_template, Utils.getLastUsedFormattedDate(
                lastUsedTime
            ))
            usedToday.text = Utils.getUsageTimeString(todayTotal)
            usedThisWeek.text = Utils.getUsageTimeString(weekTotal)
            average.text = Utils.getUsageTimeString(weekTotal / 7)

            Log.d("AppDetailFragment", it.toString())
        })
    }

    private fun setupChart(entries: List<BarEntry>, labels: List<String>, todayEntry: BarEntry?) {
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

        barChart.data = BarData(barDataSet)
        barChart.setDrawGridBackground(false)
        if(todayEntry != null) {
            barChart.highlightValue(todayEntry.x, 0)
        }
        barChart.invalidate()
    }
}
