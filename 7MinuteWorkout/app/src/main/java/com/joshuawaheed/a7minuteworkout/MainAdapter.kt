package com.joshuawaheed.a7minuteworkout

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.joshuawaheed.a7minuteworkout.databinding.ItemExerciseStatusBinding
import com.joshuawaheed.a7minuteworkout.databinding.ItemRecyclerViewBinding

class MainAdapter(val taskList: ArrayList<Task>): RecyclerView.Adapter<MainAdapter.ViewHolder>() {

    inner class ViewHolder(binding: ItemRecyclerViewBinding): RecyclerView.ViewHolder(binding.root) {
        var tvTitle = binding.tvTitle
        var tvTime = binding.tvTime
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemRecyclerViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model: Task = taskList[position]
        holder.tvTitle.text = model.title
        holder.tvTime.text = model.timeStamp
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

}