package com.joshuawaheed.projemanag.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.joshuawaheed.projemanag.R
import com.joshuawaheed.projemanag.activities.TaskListActivity
import com.joshuawaheed.projemanag.models.Card
import com.joshuawaheed.projemanag.models.SelectedMembers

open class CardListItemsAdapter(
    private val context: Context,
    private var list: ArrayList<Card>
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var onClickListener: OnClickListener? = null

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var model = list[position]

        if (holder is MyViewHolder) {
            val labelColor: View = holder.itemView.findViewById(R.id.view_label_color)

            if (model.labelColor.isNotEmpty()) {
                labelColor.visibility = View.VISIBLE
                labelColor.setBackgroundColor(Color.parseColor(model.labelColor))
            } else {
                labelColor.visibility = View.GONE
            }

            holder.itemView.findViewById<TextView>(R.id.tv_card_name).text = model.name

            if ((context as TaskListActivity).mAssignedMemberDetailList.size > 0) {
                val selectedMembersList: ArrayList<SelectedMembers> = ArrayList()

                for (i in context.mAssignedMemberDetailList.indices) {
                    for (j in model.assignedTo) {
                        if (context.mAssignedMemberDetailList[i].id == j) {
                            val selectedMembers = SelectedMembers(
                                context.mAssignedMemberDetailList[i].id,
                                context.mAssignedMemberDetailList[i].image
                            )

                            selectedMembersList.add(selectedMembers)
                        }
                    }
                }

                val rvCardSelectedMembersList: RecyclerView = holder.itemView.findViewById(R.id.rv_card_selected_members_list)

                if (selectedMembersList.size > 0) {
                    if (selectedMembersList.size == 1 && selectedMembersList[0].id == model.createdBy) {
                        rvCardSelectedMembersList.visibility = View.GONE
                    } else {
                        rvCardSelectedMembersList.visibility = View.VISIBLE
                        rvCardSelectedMembersList.layoutManager = GridLayoutManager(context, 4)
                        val adapter = CardMemberListItemsAdapter(context, selectedMembersList, false)
                        rvCardSelectedMembersList.adapter = adapter
                        adapter.setOnClickListener(object: CardMemberListItemsAdapter.OnClickListener {
                            override fun onClick() {
                                if (onClickListener != null) {
                                    onClickListener!!.onClick(position)
                                }
                            }
                        })
                    }
                } else {
                    rvCardSelectedMembersList.visibility = View.GONE
                }
            }

            holder.itemView.setOnClickListener {
                if (onClickListener != null) {
                    onClickListener!!.onClick(position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_card,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(position: Int)
    }

    class MyViewHolder(view: View): RecyclerView.ViewHolder(view)
}