package com.se7en.screentrack.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.se7en.screentrack.R
import com.se7en.screentrack.models.App
import com.se7en.screentrack.models.timelimit.AppTimeLimit
import kotlinx.android.synthetic.main.time_limit_rv_item.view.*

class TimeLimitListAdapter(
    val onClick: (app: App) -> Unit
) : ListAdapter<AppTimeLimit, TimeLimitListAdapter.ViewHolder>(TimeLimitDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.time_limit_rv_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        fun bind(item: AppTimeLimit) {
            val app = App.fromContext(itemView.context, item.packageName)
            itemView.setOnClickListener { onClick(app) }

            itemView.timeLimit.text = "${item.hour}h ${item.minute}m"
            itemView.appIcon.setImageDrawable(app.iconDrawable)
            itemView.appName.text = app.appName
        }
    }

    class TimeLimitDiffUtil : DiffUtil.ItemCallback<AppTimeLimit>() {
        override fun areItemsTheSame(oldItem: AppTimeLimit, newItem: AppTimeLimit): Boolean {
            return oldItem.packageName == newItem.packageName
        }

        override fun areContentsTheSame(oldItem: AppTimeLimit, newItem: AppTimeLimit): Boolean {
            return (oldItem.packageName == newItem.packageName
                    && oldItem.hour == newItem.hour
                    && oldItem.minute == newItem.minute)
        }
    }
}
