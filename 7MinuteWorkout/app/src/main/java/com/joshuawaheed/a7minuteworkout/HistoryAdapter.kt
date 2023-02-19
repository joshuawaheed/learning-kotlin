package com.joshuawaheed.a7minuteworkout

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.joshuawaheed.a7minuteworkout.databinding.ItemHistoryRowBinding

class HistoryAdapter(
    private val items: ArrayList<HistoryEntity>
): RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    class ViewHolder(binding: ItemHistoryRowBinding): RecyclerView.ViewHolder(binding.root) {
        val llHistoryItem = binding.llHistoryItem
        val tvHistoryItemIndex = binding.tvHistoryItemIndex
        val tvHistoryItemDate = binding.tvHistoryItemDate
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemHistoryRowBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var item = items[position]

        holder.tvHistoryItemIndex.text = (position + 1).toString()
        holder.tvHistoryItemDate.text = item.date

        if (position % 2 == 0) {
            holder.llHistoryItem.setBackgroundColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.color_light_grey
                )
            )
        } else {
            holder.llHistoryItem.setBackgroundColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.white
                )
            )
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

}