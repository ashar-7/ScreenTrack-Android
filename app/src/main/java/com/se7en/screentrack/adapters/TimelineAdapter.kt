package com.se7en.screentrack.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.se7en.screentrack.R
import com.se7en.screentrack.Utils
import com.se7en.screentrack.models.App
import com.se7en.screentrack.models.Session
import kotlinx.android.synthetic.main.timeline_session_rv_item.view.*
import org.threeten.bp.format.DateTimeFormatter

class TimelineAdapter(
    val onClick: (app: App) -> Unit
): ListAdapter<Session, TimelineAdapter.ViewHolder>(SessionDiffUtil()) {

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(item: Session) {
            with(itemView) {
                setOnClickListener { onClick(item.app) }

                val timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss")

                val start = Utils.getZonedDateTime(item.startMillis)
                sessionStartTime.text = start.format(timeFormat)

                val end = Utils.getZonedDateTime(item.endMillis)
                sessionEndTime.text = end.format(timeFormat)

                appIcon.setImageDrawable(item.app.iconDrawable)
                appName.text = item.app.appName
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.timeline_session_rv_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class SessionDiffUtil: DiffUtil.ItemCallback<Session>() {
        override fun areItemsTheSame(oldItem: Session, newItem: Session): Boolean {
            return (oldItem.startMillis == newItem.startMillis
                    && oldItem.endMillis == newItem.endMillis
                    && oldItem.app.packageName == newItem.app.packageName)
        }

        override fun areContentsTheSame(oldItem: Session, newItem: Session): Boolean {
            return (oldItem.startMillis == newItem.startMillis
                    && oldItem.endMillis == newItem.endMillis
                    && oldItem.app.packageName == newItem.app.packageName)
        }
    }
}
