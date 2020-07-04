package com.se7en.screentrack.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.se7en.screentrack.models.AppStatsModel
import com.se7en.screentrack.R
import kotlinx.android.synthetic.main.usage_rv_item.view.*

class AppsUsageAdapter(
    val listChangedListener: () -> Unit
):
    ListAdapter<AppStatsModel, AppsUsageAdapter.ViewHolder>(UsageDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.usage_rv_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCurrentListChanged(
        previousList: MutableList<AppStatsModel>,
        currentList: MutableList<AppStatsModel>
    ) {
        super.onCurrentListChanged(previousList, currentList)

        listChangedListener()
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(item: AppStatsModel) {
            itemView.totalUsageTime.text = item.totalTimeString
            itemView.appIcon.setImageDrawable(item.iconDrawable)
            itemView.appName.text = item.appName
        }
    }

    class UsageDiffUtil: DiffUtil.ItemCallback<AppStatsModel>() {
        override fun areItemsTheSame(oldItem: AppStatsModel, newItem: AppStatsModel): Boolean {
            return oldItem.packageName == newItem.packageName
        }

        override fun areContentsTheSame(oldItem: AppStatsModel, newItem: AppStatsModel): Boolean {
            return (oldItem.lastUsedMillis == newItem.lastUsedMillis
                    && oldItem.totalTimeMillis == newItem.totalTimeMillis)
        }
    }
}
