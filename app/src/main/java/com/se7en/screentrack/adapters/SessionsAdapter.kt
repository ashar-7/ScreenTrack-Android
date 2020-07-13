package com.se7en.screentrack.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.se7en.screentrack.R
import com.se7en.screentrack.Utils
import com.se7en.screentrack.models.SessionMinimal
import kotlinx.android.synthetic.main.session_rv_item.view.*
import org.threeten.bp.format.DateTimeFormatter

class SessionsAdapter(
    val listChangedListener: (isEmpty: Boolean) -> Unit
): ListAdapter<SessionMinimal, SessionsAdapter.ViewHolder>(SessionDiffUtil()) {

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(item: SessionMinimal) {
            with(itemView) {
                val timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss")
                val dateFormat = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy")

                val start = Utils.getZonedDateTime(item.startMillis)
                sessionStartTime.text = start.format(timeFormat)
                sessionStartDate.text = start.format(dateFormat)

                val end = Utils.getZonedDateTime(item.endMillis)
                sessionEndTime.text = end.format(timeFormat)
                sessionEndDate.text = end.format(dateFormat)
            }
        }
    }

    override fun onCurrentListChanged(
        previousList: MutableList<SessionMinimal>,
        currentList: MutableList<SessionMinimal>
    ) {
        super.onCurrentListChanged(previousList, currentList)
        listChangedListener(currentList.isEmpty())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.session_rv_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class SessionDiffUtil: DiffUtil.ItemCallback<SessionMinimal>() {
        override fun areItemsTheSame(oldItem: SessionMinimal, newItem: SessionMinimal): Boolean {
            return oldItem.packageName == newItem.packageName
        }

        override fun areContentsTheSame(oldItem: SessionMinimal, newItem: SessionMinimal): Boolean {
            return (oldItem.startMillis == newItem.startMillis
                    && oldItem.endMillis == newItem.endMillis)
        }
    }
}
