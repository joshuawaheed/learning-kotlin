package com.joshuawaheed.projemanag.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.joshuawaheed.projemanag.R
import com.joshuawaheed.projemanag.models.Board

open class BoardItemsAdapter(
    private val context: Context,
    private val list: ArrayList<Board>
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater
            .from(context)
            .inflate(R.layout.item_board, parent, false)

        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {
            val ivBoardImage: ShapeableImageView = holder.itemView.findViewById(
                R.id.iv_board_image
            )

            Glide
                .with(context)
                .load(model.image)
                .centerCrop()
                .placeholder(R.drawable.ic_board_place_holder)
                .into(ivBoardImage)

            val tvItemBoardName: TextView = holder.itemView.findViewById(
                R.id.tv_item_board_name
            )

            tvItemBoardName.text = model.name

            val tvItemBoardCreatedBy: TextView = holder.itemView.findViewById(
                R.id.tv_item_board_created_by
            )

            tvItemBoardCreatedBy.text = "Created by: ${model.createdBy}"

            holder.itemView.setOnClickListener {
                if (onClickListener != null) {
                    onClickListener!!.onClick(position, model)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface OnClickListener {
        fun onClick(position: Int, model: Board)
    }

    private class MyViewHolder(view: View): RecyclerView.ViewHolder(view) {

    }
}