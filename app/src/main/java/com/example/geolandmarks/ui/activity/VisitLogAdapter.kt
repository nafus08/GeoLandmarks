package com.example.geolandmarks.ui.activity

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.geolandmarks.data.local.VisitLogEntity
import com.example.geolandmarks.databinding.ItemVisitLogBinding
import java.text.SimpleDateFormat
import java.util.*

class VisitLogAdapter : ListAdapter<VisitLogEntity, VisitLogAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder(val binding: ItemVisitLogBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemVisitLogBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.tvVisitTitle.text = item.title
        var statusText = "Status: ${item.status}"
        if (item.distance != null) {
            statusText += " (Dist: ${String.format(Locale.getDefault(), "%.2f", item.distance)}m)"
        }
        holder.binding.tvVisitStatus.text = statusText
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        holder.binding.tvVisitTime.text = sdf.format(Date(item.timestamp))
    }

    object DiffCallback : DiffUtil.ItemCallback<VisitLogEntity>() {
        override fun areItemsTheSame(oldItem: VisitLogEntity, newItem: VisitLogEntity) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: VisitLogEntity, newItem: VisitLogEntity) = oldItem == newItem
    }
}
