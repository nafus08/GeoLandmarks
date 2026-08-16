package com.example.geolandmarks.ui.landmarks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.geolandmarks.data.local.LandmarkEntity
import com.example.geolandmarks.databinding.ItemLandmarkBinding

class LandmarkAdapter(private val onVisitClick: (LandmarkEntity) -> Unit) :
    ListAdapter<LandmarkEntity, LandmarkAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder(val binding: ItemLandmarkBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLandmarkBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.tvTitle.text = item.title
        holder.binding.tvScore.text = "Score: ${item.score}"
        Glide.with(holder.itemView.context).load(item.imageUrl).into(holder.binding.ivLandmark)
        holder.binding.btnVisit.setOnClickListener { onVisitClick(item) }
    }

    object DiffCallback : DiffUtil.ItemCallback<LandmarkEntity>() {
        override fun areItemsTheSame(oldItem: LandmarkEntity, newItem: LandmarkEntity) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: LandmarkEntity, newItem: LandmarkEntity) = oldItem == newItem
    }
}
