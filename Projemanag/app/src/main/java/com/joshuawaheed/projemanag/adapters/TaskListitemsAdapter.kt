package com.joshuawaheed.projemanag.adapters

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.joshuawaheed.projemanag.R
import com.joshuawaheed.projemanag.models.Task

open class TaskListitemsAdapter(
    private val context: Context,
    private var list: ArrayList<Task>
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context).inflate(
            R.layout.item_task,
            parent,
            false
        )

        val layoutParams = LinearLayout.LayoutParams(
            (parent.width * 0.7).toInt(),
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        layoutParams.setMargins((15.toDp()).toPx(), 0, (40.toDp()).toPx(), 0)

        view.layoutParams = layoutParams

        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var model = list[position]

        if (holder is MyViewHolder) {
            val taskList: TextView = holder.itemView.findViewById(R.id.tv_add_task_list)
            val taskItem: LinearLayout = holder.itemView.findViewById(R.id.ll_task_item)

            if (position == list.size - 1) {
                taskList.visibility = View.VISIBLE
                taskItem.visibility = View.GONE
            } else {
                taskList.visibility = View.GONE
                taskItem.visibility = View.VISIBLE
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private fun Int.toDp(): Int = (this / Resources.getSystem().displayMetrics.density).toInt()

    private fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

    class MyViewHolder(view: View): RecyclerView.ViewHolder(view)
}