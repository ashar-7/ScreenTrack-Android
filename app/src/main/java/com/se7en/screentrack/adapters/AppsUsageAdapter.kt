package com.se7en.screentrack.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.se7en.screentrack.R
import com.se7en.screentrack.Utils
import com.se7en.screentrack.models.AppUsage
import kotlinx.android.synthetic.main.usage_rv_item.view.*

class AppsUsageAdapter(
    val listChangedListener: () -> Unit
):
    ListAdapter<AppUsage, AppsUsageAdapter.ViewHolder>(UsageDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.usage_rv_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCurrentListChanged(
        previousList: MutableList<AppUsage>,
        currentList: MutableList<AppUsage>
    ) {
        super.onCurrentListChanged(previousList, currentList)

        listChangedListener()
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(item: AppUsage) {
            itemView.totalUsageTime.text = Utils.getUsageTimeString(item.totalTime)
            itemView.appIcon.setImageDrawable(item.app.iconDrawable)
            itemView.appName.text = item.app.appName
        }
    }

    class UsageDiffUtil: DiffUtil.ItemCallback<AppUsage>() {
        override fun areItemsTheSame(oldItem: AppUsage, newItem: AppUsage): Boolean {
            return oldItem.app.packageName == newItem.app.packageName
        }

        override fun areContentsTheSame(oldItem: AppUsage, newItem: AppUsage): Boolean {
            return (oldItem.app.packageName == newItem.app.packageName
                    && oldItem.totalTime == newItem.totalTime)
        }
    }
}
